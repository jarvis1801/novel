package com.jarvis.novel.ui.viewpager

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView
import com.jarvis.novel.R
import com.jarvis.novel.data.Media
import com.jarvis.novel.util.GlideHelper
import com.shopgun.android.zoomlayout.ZoomLayout


class MangaContentPageAdapter(var list: List<Media> = arrayListOf(), val context: Context, private val onClick1: () -> Unit?, private val onClick2: () -> Unit?, private val onClick3: () -> Unit?) : PagerAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    fun setItem(itemsList: List<Media>) {
        list = itemsList
        notifyDataSetChanged()
    }

    fun getItem() : List<Media> {
        return list ?: arrayListOf()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // 佈局
        val itemView: View = inflater
            .inflate(R.layout.item_manga_content, container, false)

        val img_content: ImageView = itemView.findViewById(R.id.img_content) as ImageView
        val zoomLayout: ZoomLayout = itemView.findViewById(R.id.container) as ZoomLayout
        zoomLayout.addOnTapListener { view, info ->
            info?.percentX?.let {
                if (it.toDouble() > 0 && it.toDouble() <= 0.25) {
                    onClick1()
                } else if (it.toDouble() > 0.25 && it.toDouble() < 0.75) {
                    onClick2()
                } else {
                    onClick3()
                }
            }
            false
        }

        // 佈局元件內容
        list[position].content?.let {
            GlideHelper().loadImage(
                context,
                "${context.getString(R.string.base_url)}file/$it",
                img_content,
                it
            )
        }

        // 加載
        container.addView(itemView)
        return itemView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ZoomLayout)
    }
}