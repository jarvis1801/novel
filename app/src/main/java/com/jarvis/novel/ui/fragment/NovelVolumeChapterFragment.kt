package com.jarvis.novel.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Novel
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.ui.viewpager.NovelVolumeChapterViewPagerAdapter
import com.jarvis.novel.viewModel.VolumeChapterViewModel
import kotlinx.android.synthetic.main.fragment_novel_volume_chapter_page.*

class NovelVolumeChapterFragment : BaseFragment() {
    private val model: VolumeChapterViewModel by activityViewModels()
    private lateinit var novelId: String

    private var viewpagerAdapter: NovelVolumeChapterViewPagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_novel_volume_chapter_page, container, false)

        init()

        return root
    }

    private fun init() {
        initLiveData()
        getArgs()
    }

    private fun initLiveData() {
        if (!model.mNovelId.hasObservers()) {
            model.mNovelId.observe(viewLifecycleOwner, Observer {
                if (it.isNullOrEmpty()) {
                    childFragmentManager.popBackStackImmediate()
                    txt_is_end.textSize = 20f
                } else {
                    getDataBase().userDao().findById(it).observeOnce(viewLifecycleOwner, Observer {
                        model.novelLiveData.postValue(it)
                    })
                }
            })
        }

        if (!model.novelLiveData.hasObservers()) {
            model.novelLiveData.observe(viewLifecycleOwner, Observer {
                if (it == null) {
                    childFragmentManager.popBackStackImmediate()
                } else {
                    updateUI(it)
                }
            })
        }
    }

    private fun updateUI(novel: Novel) {
        novel.thumbnailSectionBlob?.let {
            val bitmap = App.instance.byteArrayToCompressedBitmap(
                novel.thumbnailSectionBlob,
                App.instance.getScreenWidth()
            )
            if (bitmap != null) {
                img_thumbnail.setImageBitmap(bitmap)
            }
        }
        txt_title.text = novel.name
        txt_author.text = novel.author

        when (novel.isEnd) {
            true -> txt_is_end.text = "已完結"
            else -> txt_is_end.text = "未完結"
        }
    }

    private fun getArgs() {
        novelId = requireArguments().getString("novelId", "")
        model.mNovelId.postValue(novelId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        viewpagerAdapter = NovelVolumeChapterViewPagerAdapter(novelId, requireActivity().applicationContext, childFragmentManager, viewLifecycleOwner.lifecycle)
        viewpager.apply {
            adapter = viewpagerAdapter
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        TabLayoutMediator(tab_main, viewpager) { tab, position ->
            viewpagerAdapter?.fragmentTitleList?.get(position).let {
                tab.text = it
            }
        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()

        model.mNovelId.postValue(null)
        model.novelLiveData.postValue(null)

        viewpagerAdapter = null
    }
}