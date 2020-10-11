package com.jarvis.novel.ui.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Manga
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.ui.viewpager.MangaVolumeChapterViewPagerAdapter
import com.jarvis.novel.util.GlideHelper
import com.jarvis.novel.viewModel.MangaVolumeChapterViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_manga_chapter_main.*

class MangaVolumeChapterMainFragment : BaseFragment() {
    private val model: MangaVolumeChapterViewModel by activityViewModels()
    private lateinit var mangaId: String

    private var viewpagerAdapter: MangaVolumeChapterViewPagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_manga_chapter_main, container, false)

        init()

        return root
    }

    private fun init() {
        initLiveData()
        getArgs()
    }

    private fun initLiveData() {
        if (!model.mMangaId.hasObservers()) {
            model.mMangaId.observe(viewLifecycleOwner, {
                if (it.isNullOrEmpty()) {
                    childFragmentManager.popBackStackImmediate()
                    txt_is_end.textSize = 20f
                } else {
                    getDataBase().mangaDao().findById(it).observeOnce(viewLifecycleOwner, { manga ->
                        model.mangaLiveData.postValue(manga)
                    })
                }
            })
        }

        if (!model.mangaLiveData.hasObservers()) {
            model.mangaLiveData.observe(viewLifecycleOwner, {
                if (it == null) {
                    childFragmentManager.popBackStackImmediate()
                } else {
                    updateUI(it)
                }
            })
        }
    }

    private fun updateUI(manga: Manga) {
        when (App.instance.isShowThumbnail) {
            true -> {
                manga.thumbnailSection?.let {
                    GlideHelper().loadImage(
                        requireContext(),
                        "${requireContext().getString(R.string.base_url)}file/${manga.thumbnailSection.content}",
                        img_thumbnail,
                        manga.thumbnailSection.content!!
                    )
//                    Glide.with(requireContext())
//                        .load("${requireContext().getString(R.string.base_url)}file/${manga.thumbnailSection.content}")
//                        .diskCacheStrategy(DiskCacheStrategy.DATA)
//                        .into(img_thumbnail)
                } ?: run {
                    createPlaceholder()
                }
            }
            false -> createPlaceholder()
        }

        txt_title.text = manga.name
        txt_author.text = manga.author

        when (manga.isEnd) {
            true -> txt_is_end.text = "已完結"
            else -> txt_is_end.text = "未完結"
        }
    }

    private fun createPlaceholder() {
        img_thumbnail.setImageBitmap(
            App.instance.compressedBitmap(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.placeholder
            ),
            App.instance.getScreenWidth()
        ))
    }

    private fun getArgs() {
        mangaId = requireArguments().getString("mangaId", "")
        model.mMangaId.postValue(mangaId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        viewpagerAdapter = MangaVolumeChapterViewPagerAdapter(mangaId, requireActivity().applicationContext, childFragmentManager, viewLifecycleOwner.lifecycle)
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

        model.mMangaId.postValue(null)
        model.mangaLiveData.postValue(null)

        viewpagerAdapter = null

        requireActivity().bottom_navigation?.visibility = View.VISIBLE
        requireActivity().container_show_thumbnail?.visibility = View.VISIBLE
    }
}