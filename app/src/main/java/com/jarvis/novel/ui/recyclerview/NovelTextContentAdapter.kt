package com.jarvis.novel.ui.recyclerview

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.jarvis.novel.R
import com.jarvis.novel.data.Paragraph
import com.jarvis.novel.util.SharedPreferenceUtil
import kotlinx.android.synthetic.main.item_novel_text_content.view.*

class NovelTextContentAdapter(private val mContext: Context) : RecyclerView.Adapter<NovelTextContentAdapter.ViewHolder>() {
    var mData: List<Paragraph> = arrayListOf()
    lateinit var viewHolder: ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        viewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_novel_text_content, parent, false))
        return viewHolder
    }

    fun updateList(data: List<Paragraph>) {
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
        private val txtContent = itemView.txt_content
        fun bind(item: Paragraph) {
            txtContent.text = item.data
            txtContent.textSize = mContext.resources.getDimension(R.dimen.txt_size_normal) * SharedPreferenceUtil.getFontScale()
        }
    }
}