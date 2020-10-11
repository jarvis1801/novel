package com.jarvis.novel.ui.activity.manga

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.viewpager.widget.ViewPager
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.MangaChapter
import com.jarvis.novel.data.MangaVolume
import com.jarvis.novel.ui.activity.QRCodeActivity
import com.jarvis.novel.ui.base.BaseActivity
import com.jarvis.novel.ui.viewpager.MangaContentPageAdapter
import com.jarvis.novel.viewModel.MangaContentViewModel
import com.jarvis.novel.viewModel.MangaVolumeIndexViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manga_content.*

class MangaContentActivity : BaseActivity() {
    private val model: MangaContentViewModel by viewModels()
    private val volumeModel: MangaVolumeIndexViewModel by viewModels()

    //    private lateinit var mangaContentAdapter: MangaContentAdapter
    private lateinit var mangaContentAdapter: MangaContentPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manga_content)

        getArgs()
        init()

        initView()
    }

    private fun init() {
        initLiveData()
    }

    private fun initLiveData() {

    }

    private fun initView() {
        viewpager.apply {
            adapter = mangaContentAdapter
            addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    mangaContentAdapter.getItem().size.let {
                        txt_page.text = "[${position + 1}/$it]"

                        if (position + 1 == it) {

                            val chapter = model.chapter.value
                            chapter?.apply {
                                isRead = true
                            }
                            model.chapter.postValue(chapter)
                            volumeModel.mUpdateEndChapter.postValue(chapter?._id)
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {}

            })
        }

        img_qrcode.setOnClickListener {
            launchActivity<QRCodeActivity> {
                val chapter = model.chapter.value
                val volume = model.volume.value
                volume?.let {
                    chapter?.let {
                        putExtra("mangaVolumeId", volume._id)
                        putExtra("mangaChapterId", chapter._id)
                        putExtra("lastPosition", viewpager.currentItem)
                    }
                }
            }
        }
    }

    private fun getArgs() {
        val chapterId = intent.getStringExtra("chapterId") ?: ""
        val volumeId = intent.getStringExtra("volumeId") ?: ""
        val resetPage = intent.getBooleanExtra("resetPage", false)

        mangaContentAdapter = MangaContentPageAdapter(
            context = this,
            onClick1 = {
                val changePosition = viewpager.currentItem - 1
                if (changePosition >= 0) {
                    progress_bar.progress = changePosition
                }
            },
            onClick2 = {
                val isShowOverlay = model.isShowOverlay.value
                if (isShowOverlay == null || isShowOverlay == false) {
                    overlay_top.visibility = View.VISIBLE
                    overlay_bottom.visibility = View.VISIBLE
                    ObjectAnimator.ofFloat(overlay_top, "translationY", App.instance.dpToPixel(0f).toFloat()).apply {
                        duration = 1000
                        start()
                    }

                    ObjectAnimator.ofFloat(overlay_bottom, "translationY", App.instance.dpToPixel(0f).toFloat()).apply {
                        duration = 1000
                        start()
                    }
                    model.isShowOverlay.postValue(true)
                } else {
                    ObjectAnimator.ofFloat(overlay_top, "translationY", -App.instance.dpToPixel(40f).toFloat()).apply {
                        duration = 1000
                        start()
                    }

                    ObjectAnimator.ofFloat(overlay_bottom, "translationY", App.instance.dpToPixel(40f).toFloat()).apply {
                        duration = 1000
                        start()
                    }

                    model.isShowOverlay.postValue(false)
                }
            },
            onClick3 = {
                val chapter = model.chapter.value
                chapter?.content?.let {
                    val changePosition = viewpager.currentItem + 1
                    if (changePosition < chapter.content.size) {
                        progress_bar.progress = changePosition
                    }
                }
            }
        )

        volumeId?.let {
            getDatabase().mangaVolumeDao().findOneById(volumeId).observeOnce(this, {
                it?.let {
                    val chapter = it.chapterList.first {
                        it._id == chapterId
                    }

                    txt_volume.text = it.sectionName
                    txt_title.text = chapter.sectionName

                    mangaContentAdapter.setItem(chapter.content)
                    mangaContentAdapter.notifyDataSetChanged()

                    progress_bar.apply {
                        max = chapter.content.size
                        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                viewpager.setCurrentItem(progress, true)
                            }
                            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                        })
                        if (!resetPage) {
                            progress = chapter.lastPosition
                        }
                    }

                    model.chapter.postValue(chapter)
                    model.volume.postValue(it)
                }
            })
        }
    }

    override fun onPause() {
        updateChapterPosition()

        super.onPause()
    }

    private fun updateChapterPosition() {
        val chapter = model.chapter.value
        val volume = model.volume.value
        chapter?.let {
            volume?.let {
                val chapterIndex: Int? = volume.chapterList.indexOf(chapter)

                val chapterList = mutableListOf<MangaChapter>()
                chapterList.addAll(volume.chapterList.toCollection(ArrayList()))

                chapter.lastPosition = viewpager.currentItem

                chapterList[chapterIndex!!] = chapter
                volume.chapterList = chapterList

                insertOneReplace(volume)
            }
        }
    }

    private fun insertOneReplace(mangaVolumeDB: MangaVolume) {
        val observable = Observable.fromCallable {
            getDatabase().mangaVolumeDao().insertOneReplace(mangaVolumeDB)
        }.subscribeOn(Schedulers.io())
            .subscribe({}, {})

        compositeDisposable.add(observable)
    }

    override fun onDestroy() {
        super.onDestroy()

        model.reset()
    }
}