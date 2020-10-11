package com.jarvis.novel.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.jarvis.novel.R
import com.jarvis.novel.data.Manga
import kotlinx.android.synthetic.main.item_manga.view.*
import com.jarvis.novel.core.App
import com.jarvis.novel.util.GlideHelper

class MangaProvider(val onClick: (manga: Manga) -> Unit) : ItemViewDelegate<Manga, MangaProvider.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): MangaProvider.ViewHolder {
        val root = LayoutInflater.from(context).inflate(R.layout.item_manga, parent, false)

        mContext = context

        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: MangaProvider.ViewHolder, item: Manga) {
        holder.bind(item)
    }

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        private val container = itemView.container
        private val img_thumbnail = itemView.img_thumbnail
        private val txt_title = itemView.txt_title

        fun bind(manga: Manga) {
            container.setOnClickListener {
                onClick(manga)
            }
            // todo
            manga.thumbnailMain?.let {
                if (App.instance.isShowThumbnail) {
                    GlideHelper().loadImage(
                        mContext,
                        "${mContext.getString(R.string.base_url)}file/${manga.thumbnailMain.content}",
                        img_thumbnail,
                        manga.thumbnailMain.content!!
                    )
//                    Glide.with(mContext)
//                        .load("${mContext.getString(R.string.base_url)}file/${manga.thumbnailMain.content}")
//                        .diskCacheStrategy(DiskCacheStrategy.DATA)
//                        .into(img_thumbnail)
                } else {
                    App.instance.loadPlaceHolder(mContext, img_thumbnail)
                }
            } ?: run {
                App.instance.loadPlaceHolder(mContext, img_thumbnail)
            }

            txt_title.text = manga.name
        }
    }
}