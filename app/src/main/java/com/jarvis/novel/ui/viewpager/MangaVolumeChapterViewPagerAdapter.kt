package com.jarvis.novel.ui.viewpager

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jarvis.novel.R
import com.jarvis.novel.ui.fragment.MangaVolumeIndexFragment
import com.jarvis.novel.ui.fragment.MangaVolumeInfoFragment

class MangaVolumeChapterViewPagerAdapter(private val mangaId: String, context: Context, fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    private val fragmentList = arrayOf(
        MangaVolumeIndexFragment(),
        MangaVolumeInfoFragment()
    )

    val fragmentTitleList = arrayOf(
        context.getString(R.string.manga_chapter_tab_index),
        context.getString(R.string.manga_chapter_tab_info)
    )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = fragmentList[position]
        val bundle = Bundle()
        bundle.putString("mangaId", mangaId)

        fragment.arguments = bundle
        return fragment
    }
}