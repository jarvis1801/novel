package com.jarvis.novel.ui.recyclerview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.util.SharedPreferenceUtil
import kotlinx.android.synthetic.main.item_novel_title_content.view.*

class NovelTitleContentProvider: ItemViewDelegate<String, NovelTitleContentProvider.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val root = LayoutInflater.from(context).inflate(R.layout.item_novel_title_content, parent, false)

        mContext = context

        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: String) {
        holder.bind(item, mContext)
    }

    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        private val txt_title: TextView = itemView.txt_title

        fun bind(title: String, context: Context) {
            txt_title.text = title
            txt_title.textSize = App.instance.pixelToDp(context.resources.getDimension(R.dimen.txt_size_normal).toInt()) * SharedPreferenceUtil.getFontScale()

            Handler(Looper.getMainLooper()).postDelayed({
                txt_title.isSelected = true
            }, 2000)
        }
    }
}