package com.jarvis.novel.ui.fragment

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Volume
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.ui.recyclerview.HeaderItemDecoration
import com.jarvis.novel.ui.recyclerview.NovelVolumeProvider
import com.jarvis.novel.util.SharedPreferenceUtil
import com.jarvis.novel.viewModel.NovelVolumeIndexModel
import kotlinx.android.synthetic.main.fragment_novel_volume_index.*

class NovelVolumeIndexFragment : BaseFragment() {
    private val model: NovelVolumeIndexModel by activityViewModels()

    private var volumeListDB: List<Volume>? = listOf()

    private lateinit var novelId: String

    private val novelVolumeAdapter = MultiTypeAdapter()
    private val novelVolumeItems = ArrayList<Any>()
    private var viewManager: LinearLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_novel_volume_index, container, false)

        init()

        return root
    }

    private fun init() {
        initLiveData()
        getArgs()
    }

    private fun getArgs() {
        novelId = requireArguments().getString("novelId", "")
        if (novelId.isNotEmpty()) {
            model.mNovelId.postValue(novelId)
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private val mNovelIdObserver = Observer<String?> {
        if (!it.isNullOrEmpty()) {
            val updateNovelList = SharedPreferenceUtil.getUpdateNovelList()

            if (updateNovelList.isNullOrEmpty()) {
                initVolumeList()
            } else {
                val isCurrentNovelIdInList = updateNovelList.contains(novelId)
                if (isCurrentNovelIdInList) {
                    model.getVolumeList(it) {
                        val filterList = updateNovelList.filterNot {
                            it == novelId
                        }

                        SharedPreferenceUtil.setUpdateNovelListByStringArray(filterList)
                    }
                } else {
                    initVolumeList()
                }
            }
        }
    }

    private val volumeListObserver = Observer<List<Volume>?> {
        novelVolumeItems.clear()
        var tempReadVolumeIndex = 0
        var readVolumeIndex = 0

        if (it != null) {
            val sortedList = it.sortedWith(compareBy<Volume>{ data -> data.index }.thenByDescending { data -> data.isStickyHeader }).also { list ->
                list.forEach { volume ->
                    volume.chapterList = volume.chapterList.sortedBy { chapter ->
                        chapter.index
                    }
                }
            }

            var readVolumeId: String? = null

            sortedList.forEach { volume ->
                volume.chapterList.forEach { chapter ->
                    if (chapter.isRead && volume.index > tempReadVolumeIndex) {
                        tempReadVolumeIndex = volume.index
                        readVolumeId = volume._id
                    }
                }
            }

            readVolumeIndex = sortedList.indexOfFirst {
                it._id == readVolumeId
            }
            AsyncTask.execute {
                getDataBase().volumeDao().insertAllReplace(sortedList)
            }
            novelVolumeItems.addAll(sortedList)
            hideLoadingDialog()
        }
        novelVolumeAdapter.notifyDataSetChanged()

        viewManager?.scrollToPositionWithOffset(readVolumeIndex, getChapterTagHeight())
    }

    private val mUpdateEndChapterObserver = Observer<String?> {
        it?.let { id ->
            val list = novelVolumeItems.toList()
            list.forEach { listItem ->
                if (listItem is Volume) {
                    listItem.chapterList.forEach { chapter ->
                        if (id == chapter._id) {
                            chapter.isRead = true
                        }
                    }
                }
            }
            novelVolumeItems.clear()
            novelVolumeItems.addAll(list)
            novelVolumeAdapter.notifyDataSetChanged()
        }
    }

    private fun initLiveData() {
//        if (!model.mNovelId.hasObservers()) {
            model.mNovelId.observe(viewLifecycleOwner, mNovelIdObserver)
//        }

//        if (!model.volumeListLiveData.hasObservers()) {
            model.volumeListLiveData.observe(viewLifecycleOwner, volumeListObserver)
//        }

        if (!model.mUpdateEndChapter.hasObservers()) {
            model.mUpdateEndChapter.observe(viewLifecycleOwner, mUpdateEndChapterObserver)
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

    private fun initVolumeList() {
        getDataBase().volumeDao().findById(model.mNovelId.value!!).observeOnce(viewLifecycleOwner, Observer {
            volumeListDB = it
            if (volumeListDB.isNullOrEmpty()) {
                model.getVolumeList(model.mNovelId.value!!)
            } else {
                volumeListDB = volumeListDB!!.sortedWith(compareBy {
                    it.index
                })
                volumeListDB!!.forEach {
                    it.chapterList = it.chapterList.sortedWith(compareBy { chapter ->
                        chapter.index
                    })
                }
                model.volumeListLiveData.postValue(volumeListDB)
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        viewManager = LinearLayoutManager(requireActivity())

        novelVolumeAdapter.register(Volume::class.java, NovelVolumeProvider())
        novelVolumeAdapter.items = novelVolumeItems

        recyclerview_novel_volume.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = novelVolumeAdapter
            addItemDecoration(HeaderItemDecoration(recyclerview_novel_volume, false) {

                val item = novelVolumeItems[it]
                if (item is Volume) {
                    if (item.isStickyHeader) {
                        return@HeaderItemDecoration true
                    }
                }
                false
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        model.mNovelId.postValue(null)
        model.volumeListLiveData.postValue(null)
    }
}