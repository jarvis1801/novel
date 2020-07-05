package com.jarvis.novel.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Novel
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.ui.recyclerview.NovelAdapter
import com.jarvis.novel.util.SharedPreferenceUtil
import com.jarvis.novel.viewModel.NovelViewModel
import kotlinx.android.synthetic.main.fragment_novel.*

class NovelListFragment : BaseFragment() {

    private var novelAdapter: NovelAdapter? = null
    private var viewManager: RecyclerView.LayoutManager? = null

    private val model: NovelViewModel by activityViewModels()

    private var novelListDB: List<Novel> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_novel, container, false)

        init()

        return root
    }

    private fun init() {
        initLiveData()
        novelListDB = getDataBase().userDao().getAll()
        if (novelListDB.isNullOrEmpty()) {
            model.getNovelList()
        } else {
            checkNewAddAndUpdateNovel()
        }
    }

    private fun checkNewAddAndUpdateNovel() {
        val addNovelList = SharedPreferenceUtil.getAddNovelList()
        val updateNovelList = SharedPreferenceUtil.getUpdateNovelList()

        val novelIdList = arrayListOf<String>()

        if (addNovelList != null) {
            novelIdList.addAll(addNovelList)
        }
        if (updateNovelList != null) {
            novelIdList.addAll(updateNovelList)
        }

        if (novelIdList.size > 0) {
            model.addUpdateNovelList(novelIdList)
        } else {
            novelListDB = getDataBase().userDao().getAll()
            model.novelListLiveData.postValue(novelListDB)
        }
    }

    private fun initLiveData() {
        model.novelListLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                getDataBase().userDao().insertAllNotReplace(it)
                val sortedList = it.sortedBy { data ->
                    data.createdAt
                }
                novelAdapter?.updateList(sortedList)
            }
        })

        model.addUpdateNovelListLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.isNotEmpty()) {
                    getDataBase().userDao().insertAllReplace(it)
                }
                novelListDB = getDataBase().userDao().getAll()
                model.novelListLiveData.postValue(novelListDB)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        viewManager = GridLayoutManager(requireActivity(), 3)
        novelAdapter = NovelAdapter {
            App.instance.addFragment(createNovelVolumeChapterFragment(it), R.id.fragment_container, addToBackStack = true, fm = requireActivity().supportFragmentManager)
        }
        recyclerview_novel_list?.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = MergeAdapter(novelAdapter)
        }
    }

    private fun createNovelVolumeChapterFragment(novel: Novel): NovelVolumeChapterFragment {
        val fragment = NovelVolumeChapterFragment()
        val bundle = Bundle()
        bundle.putString("novelId", novel._id)

        fragment.arguments = bundle

        return fragment
    }
}