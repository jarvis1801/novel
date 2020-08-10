package com.jarvis.novel.ui.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.jarvis.novel.database.AppDatabase
import com.jarvis.novel.util.SharedPreferenceUtil


abstract class BaseActivity: FragmentActivity() {
    private var mAppDatabase: AppDatabase ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAppDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).allowMainThreadQueries().build()

        SharedPreferenceUtil.init(this)
    }

    fun getDatabase(): AppDatabase {
        return mAppDatabase!!
    }
}