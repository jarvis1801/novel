package com.jarvis.novel.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jarvis.novel.R
import com.jarvis.novel.ui.base.BaseFragment

class MangaVolumeInfoFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_manga_volume_info, container, false)

        return root
    }
}