package com.jarvis.novel.util

interface ItemTouchHelperAdapter {
    fun onMove(fromPosition: Int, toPosition: Int)
    fun onSwipe(position: Int)
}