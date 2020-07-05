package com.jarvis.novel.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Chapter
import com.jarvis.novel.data.Volume
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.viewModel.VolumeChapterViewModel
import kotlinx.android.synthetic.main.fragment_novel_volume_index.*

class NovelVolumeIndexFragment : BaseFragment() {
    private val model: VolumeChapterViewModel by activityViewModels()

    private var volumeListDB: List<Volume>? = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_novel_volume_index, container, false)

        init()

        return root
    }

    private fun init() {
        initLiveData()
    }

    private fun initLiveData() {

        model.mNovelId.observe(viewLifecycleOwner, Observer {
            volumeListDB = getDataBase().volumeDao().findById(model.mNovelId.value!!)
            if (volumeListDB.isNullOrEmpty()) {
                model.getVolumeList()
            } else {
                volumeListDB = volumeListDB!!.sortedWith(compareBy {
                    it.index
                })
                volumeListDB!!.forEach { it ->
                    it.chapterList = it.chapterList.sortedWith(compareBy {
                        it.index
                    })
                }
                model.volumeListLiveData.postValue(volumeListDB)
            }
        })

        model.volumeListLiveData.observeOnce(viewLifecycleOwner, Observer {
            if (it != null) {
                getDataBase().volumeDao().insertAllNotReplace(it)
                addViewToMainContainer(it)
            }
        })
    }

    private fun addViewToMainContainer(volumeList: List<Volume>) {
        volumeList.forEach { volume ->
            val volumeTitleTextView = createVolumeTitleTextView(volume)
            mainContainer.addView(volumeTitleTextView)

            val chapterTagContainer = FlexboxLayout(requireContext())
            chapterTagContainer.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            chapterTagContainer.flexWrap = FlexWrap.WRAP

            mainContainer.addView(chapterTagContainer)

            volume.chapterList.forEach { it ->
                val chapterTag = createChapterTag(it, volume._id)
                chapterTagContainer.addView(chapterTag)
            }
        }
    }

    private fun createChapterTag(chapter: Chapter, volumeId: String) : TextView {
        val textView = TextView(requireContext())

        textView.textSize = App.instance.pixelToDp(resources.getDimension(R.dimen.txt_size_normal).toInt()).toFloat()
        textView.setTextColor(Color.parseColor("#000000"))
        textView.text = chapter.sectionName
        textView.background = resources.getDrawable(R.drawable.bg_chapter_tag, null)
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.setMargins(App.instance.dpToPixel(4f), App.instance.dpToPixel(4f), App.instance.dpToPixel(4f), App.instance.dpToPixel(4f))
        }
        textView.setPadding(App.instance.dpToPixel(10f), App.instance.dpToPixel(2f), App.instance.dpToPixel(10f), App.instance.dpToPixel(2f))
        textView.gravity = Gravity.CENTER

        textView.setOnClickListener {
            App.instance.addFragment(createNovelContentFragment(chapter, volumeId), R.id.fragment_container, addToBackStack = true, fm = requireActivity().supportFragmentManager)
        }

        return textView
    }

    private fun createNovelContentFragment(chapter: Chapter, volumeId: String): Fragment {
        val fragment = NovelContentFragment()
        val bundle = Bundle()
        bundle.putString("volumeId", volumeId)
        bundle.putString("chapterId", chapter._id)

        fragment.arguments = bundle

        return fragment
    }

    private fun createVolumeTitleTextView(volume: Volume): TextView {
        val textView = TextView(requireContext())
        textView.textSize = 20f
        textView.setTextColor(Color.parseColor("#000000"))
        textView.text = volume.sectionName
        textView.setPadding(App.instance.dpToPixel(5f), App.instance.dpToPixel(5f), App.instance.dpToPixel(5f), App.instance.dpToPixel(5f))

        return textView
    }
}