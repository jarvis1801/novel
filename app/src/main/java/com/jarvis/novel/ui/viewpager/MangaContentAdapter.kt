package com.jarvis.novel.ui.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jarvis.novel.R
import com.jarvis.novel.data.Media
import com.jarvis.novel.util.GlideHelper
import kotlinx.android.synthetic.main.item_manga_content.view.*

class MangaContentAdapter(var items: List<Media> = arrayListOf(), val mContext: Context) : RecyclerView.Adapter<MangaContentAdapter.ViewHolder>() {
//    private lateinit var mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaContentAdapter.ViewHolder {
//        mContext = parent.context
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_manga_content, parent, false))
    }

    override fun onBindViewHolder(holder: MangaContentAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img_content = itemView.img_content

        fun bind(media: Media) {
            media.content?.let {
                GlideHelper().loadImage(
                    mContext,
                    "${mContext.getString(R.string.base_url)}file/$it",
                    img_content,
                    it
                )
//                Glide.with(mContext)
//                    .load("${mContext.getString(R.string.base_url)}file/$it")
//                    .diskCacheStrategy(DiskCacheStrategy.DATA)
//                    .into(img_content)
            }
        }
    }

}