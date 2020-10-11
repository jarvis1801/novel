package com.jarvis.novel.ui.activity.novel

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Novel
import com.jarvis.novel.ui.base.BaseActivity
import com.jarvis.novel.ui.viewpager.NovelVolumeChapterViewPagerAdapter
import com.jarvis.novel.util.GlideHelper
import com.jarvis.novel.viewModel.VolumeChapterViewModel
import kotlinx.android.synthetic.main.activity_novel_volume_chapter_page.*

class NovelVolumeChapterActivity : BaseActivity() {
    private val model: VolumeChapterViewModel by viewModels()
    private lateinit var novelId: String

    private var viewpagerAdapter: NovelVolumeChapterViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novel_volume_chapter_page)

        init()

        initView()
    }

    private fun init() {
        showLoadingDialog()
        initLiveData()
        getArgs()
    }

    private fun initLiveData() {
        if (!model.mNovelId.hasObservers()) {
            model.mNovelId.observe(this, {
                if (it.isNullOrEmpty()) {
                    finish()
                    txt_is_end.textSize = 20f
                } else {
                    getDatabase().novelDao().findById(it).observeOnce(this, { novel ->
                        model.novelLiveData.postValue(novel)
                    })
                }
            })
        }

        if (!model.novelLiveData.hasObservers()) {
            model.novelLiveData.observe(this, {
                if (it == null) {
                    finish()
                } else {
                    updateUI(it)
                }
            })
        }
    }

    private fun updateUI(novel: Novel) {
        when (App.instance.isShowThumbnail) {
            true -> {
                novel.thumbnailSection?.let {
                    GlideHelper().loadImage(
                        this,
                        "${getString(R.string.base_url)}file/${it.content}",
                        img_thumbnail,
                        it.content!!
                    )
//                    Glide.with(requireContext())
//                        .load("${requireContext().getString(R.string.base_url)}file/${it.content}")
//                        .diskCacheStrategy(DiskCacheStrategy.DATA)
//                        .into(img_thumbnail)
                } ?: run {
                    createPlaceholder()
                }
            }
            false -> createPlaceholder()
        }

        txt_title.text = novel.name
        txt_author.text = novel.author

        when (novel.isEnd) {
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
        novelId = intent.getStringExtra("novelId") ?: ""
        model.mNovelId.postValue(novelId)
    }

    private fun initView() {
        viewpagerAdapter = NovelVolumeChapterViewPagerAdapter(novelId, applicationContext, supportFragmentManager, lifecycle)
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

        model.mNovelId.postValue(null)
        model.novelLiveData.postValue(null)

        viewpagerAdapter = null
    }
}