package com.jarvis.novel.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Paragraph
import com.jarvis.novel.util.SharedPreferenceUtil
import kotlinx.android.synthetic.main.item_novel_text_content.view.*

class NovelTextContentProvider: ItemViewDelegate<Paragraph, NovelTextContentProvider.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val root = LayoutInflater.from(context).inflate(R.layout.item_novel_text_content, parent, false)

        mContext = context

        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Paragraph) {
        holder.bind(item, mContext)
    }

    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        private val txt_content: TextView = itemView.txt_content

        fun bind(item: Paragraph, context: Context) {
            txt_content.text = item.data
            txt_content.textSize = App.instance.pixelToDp(context.resources.getDimension(R.dimen.txt_size_normal).toInt()) * SharedPreferenceUtil.getFontScale()
        }
    }
}