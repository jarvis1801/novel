package com.jarvis.novel.ui.activity

import android.os.Bundle
import com.jarvis.novel.R
import com.jarvis.novel.ui.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
    }
}