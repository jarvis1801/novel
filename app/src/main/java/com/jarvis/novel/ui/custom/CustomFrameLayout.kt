package com.jarvis.novel.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.item_manga_content.view.*

class CustomFrameLayout(c: Context, attrs: AttributeSet) : FrameLayout(c, attrs) {
    private var mDisable = false
//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
////        super.onInterceptTouchEvent(ev)
//
//        when (ev?.action) {
//            MotionEvent.ACTION_MOVE -> {
//                Log.d("chris", "ACTION_MOVE")
//                mDisable = true
////                img_content?.dispatchDragEvent()
//                return true
//            }
//            MotionEvent.ACTION_DOWN -> {
//                Log.d("chris", "ACTION_DOWN")
//            }
//
//            MotionEvent.ACTION_UP -> {
//                Log.d("chris", "ACTION_UP")
//                mDisable = false
//            }
//        }
//
//
//        return false
//    }
}