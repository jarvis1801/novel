package com.jarvis.novel.ui.base

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.jarvis.novel.database.AppDatabase
import com.jarvis.novel.util.SharedPreferenceUtil


abstract class BaseActivity: FragmentActivity() {
    private var mAppDatabase: AppDatabase ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAppDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database").build()

        SharedPreferenceUtil.init(applicationContext)
    }

    fun getDatabase(): AppDatabase {
        return mAppDatabase!!
    }
}