package com.jarvis.novel.ui.recyclerview

import android.util.Log
import com.drakeet.multitype.MultiTypeAdapter
import com.jarvis.novel.util.ItemTouchHelperAdapter
import java.util.*

class CustomMultiTypeAdapter(val callback: () -> Unit) : MultiTypeAdapter(), ItemTouchHelperAdapter {
    override fun onMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition)
        callback()
    }

    override fun onSwipe(position: Int) {
        TODO("Not yet implemented")
    }

}