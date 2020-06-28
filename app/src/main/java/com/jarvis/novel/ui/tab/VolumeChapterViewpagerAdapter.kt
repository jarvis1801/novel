package com.jarvis.novel.ui.tab

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class VolumeChapterViewpagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return
    }

    @NonNull
    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
    }

}