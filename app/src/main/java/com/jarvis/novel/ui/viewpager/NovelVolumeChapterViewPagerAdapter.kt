package com.jarvis.novel.ui.viewpager


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jarvis.novel.R
import com.jarvis.novel.ui.fragment.NovelVolumeIndexFragment
import com.jarvis.novel.ui.fragment.NovelVolumeInfoFragment

class NovelVolumeChapterViewPagerAdapter(private val novelId: String, context: Context, fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    companion object;
    private val fragmentList = arrayOf(
        NovelVolumeIndexFragment(),
        NovelVolumeInfoFragment()
    )

    val fragmentTitleList = arrayOf(
        context.getString(R.string.novel_volume_tab_index),
        context.getString(R.string.novel_volume_tab_info)
    )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = fragmentList[position]
        val bundle = Bundle()
        bundle.putString("novelId", novelId)

        fragment.arguments = bundle
        return fragment
    }
}