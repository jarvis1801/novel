package com.jarvis.novel.ui.fragment

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.MangaChapter
import com.jarvis.novel.data.MangaVolume
import com.jarvis.novel.data.Volume
import com.jarvis.novel.ui.activity.manga.MangaContentActivity
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.ui.recyclerview.HeaderItemDecoration
import com.jarvis.novel.ui.recyclerview.MangaVolumeProvider
import com.jarvis.novel.util.SharedPreferenceUtil
import com.jarvis.novel.viewModel.MangaVolumeIndexViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_manga_volume_index.*

class MangaVolumeIndexFragment() : BaseFragment() {
    private val model: MangaVolumeIndexViewModel by activityViewModels()

    private var mangaChapterListDB: List<MangaVolume>? = listOf()

    private val mangaChapterAdapter = MultiTypeAdapter()
    private val mangaChapterItems = ArrayList<Any>()
    private var viewManager: LinearLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_manga_volume_index, container, false)

        init()

        return root
    }

    private fun init() {
        initLiveData()
        getArgs()
    }

    private fun getArgs() {
        val mangaId = requireArguments().getString("mangaId", "")
        if (mangaId.isNotEmpty()) {
            model.mMangaId.postValue(mangaId)
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initLiveData() {
//        if (!model.mNovelId.hasObservers()) {
        model.mMangaId.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                val updateMangaList = SharedPreferenceUtil.getUpdateNovelList()
                val isGetFromAPI = SharedPreferenceUtil.getIsGetFromAPI()

                if (updateMangaList.isNullOrEmpty() || !isGetFromAPI) {
                    initMangaVolumeList()
                } else {
                    val mangaId = model.mMangaId.value
                    val isCurrentMangaIdInList = updateMangaList.contains(mangaId)
                    if (isCurrentMangaIdInList) {
                        model.getVolumeList(requireContext(), it) {
                            val filterList = updateMangaList.filterNot {
                                it == mangaId
                            }

                            SharedPreferenceUtil.setUpdateNovelListByStringArray(filterList)
                        }
                    } else {
                        initMangaVolumeList()
                    }
                }
            }
        })
//        }

//        if (!model.volumeListLiveData.hasObservers()) {
        model.mangaChapterListLiveData.observe(viewLifecycleOwner, {
            mangaChapterItems.clear()
            var tempReadMangaVolumeIndex = 0
            var readMangaVolumeIndex = 0

            if (it != null) {
                val sortedList = it.sortedWith(compareBy<MangaVolume>{ data -> data.index }.thenByDescending { data -> data.isStickyHeader }).also { list ->
                    list.forEach { volume ->
                        volume.chapterList = volume.chapterList.sortedBy { chapter ->
                            chapter.index
                        }
                    }
                }

                var readVolumeId: String? = null

                sortedList.forEach { list ->
                    tempReadMangaVolumeIndex = list.index
                    readVolumeId = list._id
                }

                readMangaVolumeIndex = sortedList.indexOfFirst {
                    it._id == readVolumeId
                }

                insertAllVolumeReplace(sortedList)
                mangaChapterItems.addAll(sortedList)
            }
            mangaChapterAdapter.notifyDataSetChanged()

            if (tempReadMangaVolumeIndex != 0) {
                viewManager?.scrollToPositionWithOffset(readMangaVolumeIndex, getChapterTagHeight())
            }
        })
//        }

        if (!model.mUpdateEndChapter.hasObservers()) {
            model.mUpdateEndChapter.observe(viewLifecycleOwner, {
                it?.let { id ->
                    val list = mangaChapterItems.toList()
                    list.forEach { listItem ->
                        if (listItem is MangaVolume) {
                            listItem.chapterList.forEach { chapter ->
                                if (id == chapter._id) {
                                    chapter.isRead = true
                                }
                            }
                        }
                    }
                    mangaChapterItems.clear()
                    mangaChapterItems.addAll(list)
                    mangaChapterAdapter.notifyDataSetChanged()
                }
                model.mUpdateEndChapter.postValue(null)
            })
        }

        if (!model.mHideLoadingDialog.hasObservers()) {
            model.mHideLoadingDialog.observe(viewLifecycleOwner, {
                if (it == true) {
                    hideLoadingDialog()
                    model.mHideLoadingDialog.postValue(false)
                }
            })
        }

        if (!model.mUpdateDownloadSize.hasObservers()) {
            model.mUpdateDownloadSize.observe(viewLifecycleOwner, {
                it?.let {
                    showLoadingPercent()
                    updateLoadingPercent("${it}%")
                }
            })
        }
    }

    private fun insertAllVolumeReplace(sortedList: List<MangaVolume>) {
        val observable = Observable.fromCallable {
            getDataBase().mangaVolumeDao().insertAllReplace(sortedList)
        }.subscribeOn(Schedulers.io())
            .subscribe({}, {})

        compositeDisposable.add(observable)
    }

    private fun initMangaVolumeList() {
        val mangaId = model.mMangaId.value
        mangaId?.let {
            getDataBase().mangaVolumeDao().findById(mangaId).observeOnce(viewLifecycleOwner, {
                mangaChapterListDB = it
                val isGetFromAPI = SharedPreferenceUtil.getIsGetFromAPI()
                if (mangaChapterListDB.isNullOrEmpty() && isGetFromAPI) {
                    model.getVolumeList(requireContext(), mangaId)
                } else {
                    mangaChapterListDB = mangaChapterListDB!!.sortedWith(compareBy {
                        it.index
                    })
                    mangaChapterListDB!!.forEach {
                        it.chapterList = it.chapterList.sortedWith(compareBy { chapter ->
                            chapter.index
                        })
                    }
                    model.mangaChapterListLiveData.postValue(mangaChapterListDB)
                    hideLoadingDialog()
                }
            })
        }
    }

    private fun getChapterTagHeight(): Int {
        val textView = TextView(requireActivity().applicationContext)
        textView.textSize = 20f
        textView.setTextColor(Color.parseColor("#000000"))
        textView.text = "123"
        textView.setPadding(App.instance.dpToPixel(5f), App.instance.dpToPixel(5f), App.instance.dpToPixel(5f), App.instance.dpToPixel(5f))

        val bounds = Rect()
        val textPaint: Paint = textView.paint
        textPaint.getTextBounds(textView.text.toString(), 0, textView.length(), bounds)
        val height: Int = bounds.height()

        return App.instance.dpToPixel(height.toFloat())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        viewManager = LinearLayoutManager(requireActivity())

        mangaChapterAdapter.register(MangaVolume::class.java, MangaVolumeProvider(
            onClick = { chapterId, volumeId ->
                startMangaContentActivity(chapterId, volumeId)
            },
            onLongPress = { chapterId, volumeId ->
                startMangaContentActivity(chapterId, volumeId, true)
            }
        ))
        mangaChapterAdapter.items = mangaChapterItems

        recyclerview_manga_volume.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mangaChapterAdapter
            addItemDecoration(HeaderItemDecoration(recyclerview_manga_volume, false) {

                val item = mangaChapterItems[it]
                if (item is MangaVolume) {
                    if (item.isStickyHeader) {
                        return@HeaderItemDecoration false
                    }
                }
                false
            })
        }
    }

    private fun startMangaContentActivity(chapterId: String, volumeId: String, isResetPage: Boolean = false) {
        requireActivity().launchActivity<MangaContentActivity> {
            putExtra("chapterId", chapterId)
            putExtra("volumeId", volumeId)
            if (isResetPage) {
                putExtra("resetPage", true)
            }
        }
    }

    override fun onResume() {
        super.onResume()

//        showLoadingDialog()
//        mangaChapterItems.clear()
//        initMangaVolumeList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        model.reset()
    }
}