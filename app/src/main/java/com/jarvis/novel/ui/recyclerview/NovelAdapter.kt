package com.jarvis.novel.ui.recyclerview

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Novel
import com.jarvis.novel.util.GlideHelper
import kotlinx.android.synthetic.main.item_novel.view.*

class NovelAdapter(val onItemClick: (item: Novel) -> Unit) : RecyclerView.Adapter<NovelAdapter.ViewHolder>() {
    private var mData: List<Novel> = arrayListOf()
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_novel, parent, false))
    }

    fun updateList(data: List<Novel>) {
        mData = data
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Novel) {
            when (App.instance.isShowThumbnail) {
                true -> {
                    item.thumbnailMain?.let {
                        GlideHelper().loadImage(
                            mContext,
                            "${mContext.getString(R.string.base_url)}file/${it.content}",
                            itemView.img_thumbnail,
                            it.content!!
                        )
//                        Glide.with(mContext)
//                            .load("${mContext.getString(R.string.base_url)}file/${it.content}")
//                            .diskCacheStrategy(DiskCacheStrategy.DATA)
//                            .into(itemView.img_thumbnail)
                    } ?: createPlaceholder()
                }
                false -> createPlaceholder()
            }


            itemView.img_thumbnail.setOnClickListener {
                onItemClick(item)
            }
            itemView.txt_title.text = item.name
        }

        private fun createPlaceholder() {
            itemView.img_thumbnail.setImageBitmap(App.instance.compressedBitmap(
                BitmapFactory.decodeResource(
                    mContext.resources,
                    R.drawable.placeholder
                ),
                App.instance.getScreenWidth() / 3
            ))
        }
    }

}