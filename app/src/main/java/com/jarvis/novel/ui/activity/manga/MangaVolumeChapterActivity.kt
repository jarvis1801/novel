package com.jarvis.novel.ui.activity.manga

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Manga
import com.jarvis.novel.ui.base.BaseActivity
import com.jarvis.novel.ui.viewpager.MangaVolumeChapterViewPagerAdapter
import com.jarvis.novel.util.GlideHelper
import com.jarvis.novel.viewModel.MangaVolumeChapterViewModel
import kotlinx.android.synthetic.main.activity_manga_chapter_main.*

class MangaVolumeChapterActivity : BaseActivity() {
    private val model: MangaVolumeChapterViewModel by viewModels()
    private lateinit var mangaId: String

    private var viewpagerAdapter: MangaVolumeChapterViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manga_chapter_main)


        showLoadingDialog()

        init()
        initView()
    }

    private fun init() {
        initLiveData()
        getArgs()
    }

    private fun initLiveData() {
        if (!model.mMangaId.hasObservers()) {
            model.mMangaId.observe(this, {
                if (it.isNullOrEmpty()) {
                    finish()
                    txt_is_end.textSize = 20f
                } else {
                    getDatabase().mangaDao().findById(it).observeOnce(this, { manga ->
                        model.mangaLiveData.postValue(manga)
                    })
                }
            })
        }

        if (!model.mangaLiveData.hasObservers()) {
            model.mangaLiveData.observe(this, {
                if (it == null) {
                    finish()
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
                        this,
                        "${getString(R.string.base_url)}file/${manga.thumbnailSection.content}",
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
        mangaId = intent.getStringExtra("mangaId") ?: ""
        model.mMangaId.postValue(mangaId)
    }

    private fun initView() {
        viewpagerAdapter = MangaVolumeChapterViewPagerAdapter(mangaId, applicationContext, supportFragmentManager, lifecycle)
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

    override fun onDestroy() {
        super.onDestroy()

        Log.d("chris", "onDestroy")

        model.mMangaId.postValue(null)
        model.mangaLiveData.postValue(null)

        viewpagerAdapter = null

//        requireActivity().bottom_navigation?.visibility = View.VISIBLE
//        requireActivity().container_show_thumbnail?.visibility = View.VISIBLE
    }
}