package com.jarvis.novel.ui.activity

import android.os.Bundle
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.ui.base.BaseActivity
import com.jarvis.novel.ui.fragment.NovelListFragment

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.instance.addFragment(fragment = NovelListFragment(), containerLayoutId = R.id.fragment_container, fm = supportFragmentManager, isShowAnimation = false, addToBackStack = false)
    }
}