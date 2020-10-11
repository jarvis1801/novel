package com.jarvis.novel.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Manga
import com.jarvis.novel.data.MangaVolume
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.ui.recyclerview.CustomMultiTypeAdapter
import com.jarvis.novel.ui.recyclerview.MangaProvider
import com.jarvis.novel.util.ItemTouchHelperCallback
import com.jarvis.novel.util.SharedPreferenceUtil
import com.jarvis.novel.viewModel.MangaViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_manga.*

class MangaListFragment : BaseFragment() {
    private var viewManager: RecyclerView.LayoutManager? = null

    private val model: MangaViewModel by activityViewModels()

    private var mangaListDB: List<Manga> = arrayListOf()

    private val mangaAdapter = CustomMultiTypeAdapter {
        mangaItems.forEachIndexed { index, it ->
            val data = it as Manga
            data.index = index
            val observable = Observable.fromCallable {
                getDataBase().mangaDao().insertOneReplace(data)
            }.subscribeOn(Schedulers.io())
                .subscribe({}, {})

            compositeDisposable.add(observable)
        }
    }
    private val mangaItems = ArrayList<Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_manga, container, false)

        init()

        return root
    }

    private fun init() {
        initLiveData()
        getDataBase().mangaDao().getAll().observeOnce(viewLifecycleOwner, {
            mangaListDB = it ?: arrayListOf()
            if (mangaListDB.isNullOrEmpty()) {
                model.getMangaList(requireContext())
            } else {
                checkNewAddAndUpdateManga()
            }
        })
    }

    private fun checkNewAddAndUpdateManga() {
        val addMangaList = SharedPreferenceUtil.getAddNovelList()

        val mangaIdList = arrayListOf<String>()

        if (addMangaList != null) {
            mangaIdList.addAll(addMangaList)
        }

        if (mangaIdList.size > 0) {
            model.addUpdateMangaList(mangaIdList)
        } else {
            getDataBase().mangaDao().getAll().observeOnce(viewLifecycleOwner, {
                mangaListDB = it ?: arrayListOf()
                model.mangaListLiveData.postValue(mangaListDB)
                hideLoadingDialog()
            })
        }
    }

    private fun initLiveData() {
        model.mangaListLiveData.observeWithoutOnResume(viewLifecycleOwner, {
            if (it != null) {
                if (mangaItems.isNotEmpty()) {
                    mangaItems.clear()
                }
                insertAllUserNotReplace(it)

                val sortedList = it.sortedWith(compareBy<Manga>{ data -> data.index }.thenBy{ data -> data.createdAt })

                sortedList.forEachIndexed { i, data ->
                    if (data.index == null) {
                        data.index = i
                    }
                }

                mangaItems.addAll(sortedList)
                mangaAdapter.notifyDataSetChanged()
            }
            hideLoadingDialog()
        })

        model.addUpdateMangaListLiveData.observeWithoutOnResume(viewLifecycleOwner, {
            if (it != null) {
                if (it.isNotEmpty()) {
                    insertAllReplace(it)
                }
                getDataBase().mangaDao().getAll().observeOnce(viewLifecycleOwner, {
                    mangaListDB = it ?: arrayListOf()
                    model.mangaListLiveData.postValue(mangaListDB)
                })
            }
            hideLoadingDialog()
        })

        model.isShowThumbnail.observeWithoutOnResume(viewLifecycleOwner, {
            mangaAdapter.notifyDataSetChanged()
        })

        model.mHideLoadingDialog.observe(viewLifecycleOwner, {
            if (it == true) {
                hideLoadingDialog()
                model.mHideLoadingDialog.postValue(false)
            }
        })
    }

    private fun insertAllReplace(it: List<Manga>) {
        val observable = Observable.fromCallable {
            getDataBase().mangaDao().insertAllReplace(it)
        }.subscribeOn(Schedulers.io())
            .subscribe({}, {})

        compositeDisposable.add(observable)
    }

    private fun insertAllUserNotReplace(it: List<Manga>) {
        val observable = Observable.fromCallable {
            getDataBase().mangaDao().insertAllNotReplace(it)
        }.subscribeOn(Schedulers.io())
            .subscribe({}, {})

        compositeDisposable.add(observable)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        viewManager = GridLayoutManager(requireActivity(), 3)
        mangaAdapter.register(Manga::class.java, MangaProvider {
            showLoadingDialog()
            App.instance.addFragment(createMangaChapterIndexFragment(it), R.id.fragment_container, type = "add", addToBackStack = true, fm = requireActivity().supportFragmentManager, tag = "manga_list_page")
        })
        mangaAdapter.items = mangaItems
        val itemTouchHelperCallback = ItemTouchHelperCallback(mangaAdapter)
        itemTouchHelperCallback.setDragEnable(true)
        itemTouchHelperCallback.setSwipeEnable(false)
        val helper = ItemTouchHelper(itemTouchHelperCallback)

        recyclerview_manga_list?.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mangaAdapter
        }
        helper.attachToRecyclerView(recyclerview_manga_list)
    }

    private fun createMangaChapterIndexFragment(manga: Manga): MangaVolumeChapterMainFragment {
        requireActivity().bottom_navigation?.visibility = View.GONE
        requireActivity().container_show_thumbnail?.visibility = View.GONE
        val fragment = MangaVolumeChapterMainFragment()
        val bundle = Bundle()
        bundle.putString("mangaId", manga._id)

        fragment.arguments = bundle

        return fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()

        model.reset()
    }
}