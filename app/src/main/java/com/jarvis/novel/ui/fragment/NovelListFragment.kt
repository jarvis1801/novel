package com.jarvis.novel.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Novel
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.ui.recyclerview.NovelAdapter
import com.jarvis.novel.util.SharedPreferenceUtil
import com.jarvis.novel.viewModel.NetworkViewModel
import com.jarvis.novel.viewModel.NovelViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_novel.*

class NovelListFragment : BaseFragment() {

    private var novelAdapter: NovelAdapter? = null
    private var viewManager: RecyclerView.LayoutManager? = null

    private val model: NovelViewModel by activityViewModels()
    private val networkViewModel: NetworkViewModel by activityViewModels()

    private var novelListDB: List<Novel> = arrayListOf()

    private var snackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_novel, container, false)

        init()

        return root
    }

    private fun init() {
        initLiveData()
        getDataBase().novelDao().getAll().observeOnce(viewLifecycleOwner, {
            novelListDB = it ?: arrayListOf()
            if (novelListDB.isNullOrEmpty()) {
                model.getNovelList()
            } else {
                checkNewAddAndUpdateNovel()
            }
        })
    }

    private fun checkNewAddAndUpdateNovel() {
        val addNovelList = SharedPreferenceUtil.getAddNovelList()

        val novelIdList = arrayListOf<String>()

        if (addNovelList != null) {
            novelIdList.addAll(addNovelList)
        }

        if (novelIdList.size > 0) {
            model.addUpdateNovelList(novelIdList)
        } else {
            getDataBase().novelDao().getAll().observeOnce(viewLifecycleOwner, {
                novelListDB = it ?: arrayListOf()
                model.novelListLiveData.postValue(novelListDB)
            })
        }
    }

    private fun initLiveData() {
        model.novelListLiveData.observeWithoutOnResume(viewLifecycleOwner, {
            if (it != null) {
                insertAllUserNotReplace(it)

                val sortedList = it.sortedBy { data ->
                    data.createdAt
                }
                novelAdapter?.updateList(sortedList)
            }
            hideLoadingDialog()
        })

        model.addUpdateNovelListLiveData.observeWithoutOnResume(viewLifecycleOwner, {
            if (it != null) {
                if (it.isNotEmpty()) {
                    insertAllReplace(it)
                }
                getDataBase().novelDao().getAll().observeOnce(viewLifecycleOwner, {
                    novelListDB = it ?: arrayListOf()
                    model.novelListLiveData.postValue(novelListDB)
                })
            }
            hideLoadingDialog()
        })

//        networkViewModel.networkStatus.observe(viewLifecycleOwner, {
//            snackbar?.dismiss()
//            if (it == NetworkViewModel.ON_CONNECT) {
//                snackbar = Snackbar.make(requireView(), "已連接", Snackbar.LENGTH_LONG)
//                snackbar?.show()
//            } else if (it == NetworkViewModel.NO_NETWORK) {
//                snackbar = Snackbar.make(requireView(), "網絡中斷", Snackbar.LENGTH_LONG)
//                snackbar?.show()
//            }
//        })

        model.isShowThumbnail.observeWithoutOnResume(viewLifecycleOwner, {
            novelAdapter?.notifyDataSetChanged()
        })
    }

    private fun insertAllReplace(it: List<Novel>) {
        val observable = Observable.fromCallable {
            getDataBase().novelDao().insertAllReplace(it)
        }.subscribeOn(Schedulers.io())
            .subscribe({}, {})

        compositeDisposable.add(observable)
    }

    private fun insertAllUserNotReplace(it: List<Novel>) {
        val observable = Observable.fromCallable {
            getDataBase().novelDao().insertAllNotReplace(it)
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
        novelAdapter = NovelAdapter {
            showLoadingDialog()
            App.instance.addFragment(createNovelVolumeChapterFragment(it), R.id.fragment_container, type = "add", addToBackStack = true, fm = requireActivity().supportFragmentManager, tag = "novel_list_page")
        }
        recyclerview_novel_list?.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = ConcatAdapter(novelAdapter)
        }
    }

    private fun createNovelVolumeChapterFragment(novel: Novel): NovelVolumeChapterFragment {
        requireActivity().bottom_navigation?.visibility = View.GONE
        requireActivity().container_show_thumbnail?.visibility = View.GONE
        val fragment = NovelVolumeChapterFragment()
        val bundle = Bundle()
        bundle.putString("novelId", novel._id)

        fragment.arguments = bundle

        return fragment
    }
}