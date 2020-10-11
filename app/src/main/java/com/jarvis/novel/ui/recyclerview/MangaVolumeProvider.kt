package com.jarvis.novel.ui.recyclerview

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.*
import com.jarvis.novel.ui.activity.MainActivity
import com.jarvis.novel.ui.fragment.MangaContentFragment
import kotlinx.android.synthetic.main.item_novel_volume.view.*

class MangaVolumeProvider : ItemViewDelegate<MangaVolume, MangaVolumeProvider.ViewHolder>() {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): MangaVolumeProvider.ViewHolder {
        val root = LayoutInflater.from(context).inflate(R.layout.item_manga_chapter, parent, false)

        mContext = context

        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: MangaVolumeProvider.ViewHolder, item: MangaVolume) {
        holder.bind(item)
    }

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        private val mainContainer: LinearLayout = itemView.mainContainer

        fun bind(item: MangaVolume) {
            mainContainer.removeAllViews()
            item.let { volume ->
                val volumeTitleTextView = createVolumeTitleTextView(volume)
                mainContainer.addView(volumeTitleTextView)

                val chapterTagContainer = FlexboxLayout(mContext)
                chapterTagContainer.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                chapterTagContainer.flexWrap = FlexWrap.WRAP

                mainContainer.addView(chapterTagContainer)

                volume.chapterList.forEach {
                    val chapterTag = createChapterTag(it, volume._id)
                    chapterTagContainer.addView(chapterTag)
                }
            }
        }

        private fun createVolumeTitleTextView(volume: MangaVolume): TextView {
            val textView = TextView(mContext)
            textView.textSize = 20f
            textView.setTextColor(Color.parseColor("#000000"))
            textView.text = volume.sectionName
            textView.setPadding(App.instance.dpToPixel(5f), App.instance.dpToPixel(5f), App.instance.dpToPixel(5f), App.instance.dpToPixel(5f))

            return textView
        }

        private fun createChapterTag(chapter: MangaChapter, volumeId: String) : TextView {
            val textView = TextView(mContext)

            textView.textSize = App.instance.pixelToDp(mContext.resources.getDimension(R.dimen.txt_size_normal).toInt()).toFloat()
            if (chapter.isRead) {
                textView.setTextColor(Color.parseColor("#FFFFFF"))
                textView.background = ResourcesCompat.getDrawable(mContext.resources, R.drawable.bg_chapter_tag_read, null)
            } else {
                textView.setTextColor(Color.parseColor("#000000"))
                textView.background = ResourcesCompat.getDrawable(mContext.resources, R.drawable.bg_chapter_tag, null)
            }
            textView.text = chapter.sectionName
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(App.instance.dpToPixel(4f), App.instance.dpToPixel(4f), App.instance.dpToPixel(4f), App.instance.dpToPixel(4f))
            }
            textView.setPadding(App.instance.dpToPixel(10f), App.instance.dpToPixel(2f), App.instance.dpToPixel(10f), App.instance.dpToPixel(2f))
            textView.gravity = Gravity.CENTER

            val activity = mContext as MainActivity

            textView.setOnClickListener {
                App.instance.addFragment(createMangaContentFragment(chapter, volumeId), R.id.fragment_container, type = "add", addToBackStack = true, fm = activity.supportFragmentManager, tag = "novel_content_page")
            }
            textView.setOnLongClickListener {
                App.instance.addFragment(createMangaContentFragment(chapter, volumeId, true), R.id.fragment_container, type = "add", addToBackStack = true, fm = activity.supportFragmentManager, tag = "novel_content_page")
                true
            }

            return textView
        }

        private fun createMangaContentFragment(chapter: MangaChapter, volumeId: String, isResetPage: Boolean = false): Fragment {
            val fragment = MangaContentFragment()
            val bundle = Bundle()
            bundle.putString("chapterId", chapter._id)
            bundle.putString("volumeId", volumeId)
            if (isResetPage) {
                bundle.putBoolean("resetPage", true)
            }

            fragment.arguments = bundle

            return fragment
        }
    }
}