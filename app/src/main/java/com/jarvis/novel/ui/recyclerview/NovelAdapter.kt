package com.jarvis.novel.ui.recyclerview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Novel
import kotlinx.android.synthetic.main.item_novel.view.*

class NovelAdapter(val onItemClick: (item: Novel) -> Unit) : RecyclerView.Adapter<NovelAdapter.ViewHolder>() {
    var mData: List<Novel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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
            if (item.thumbnailMain.isNotEmpty()) {
                val bitmap = App.instance.base64ToBitmap(item.thumbnailMain)
                if (bitmap != null) {
                    itemView.img_thumbnail.setImageBitmap(bitmap)
                }
            } else {
                itemView.img_thumbnail.setImageResource(0)
            }
            itemView.img_thumbnail.setOnClickListener {
                onItemClick(item)
            }
            itemView.txt_title.text = item.name
        }
    }
}