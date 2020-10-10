package com.jarvis.novel.ui.base

import android.content.ClipData.newIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jarvis.novel.database.AppDatabase
import com.jarvis.novel.util.SharedPreferenceUtil
import io.reactivex.disposables.CompositeDisposable


abstract class BaseActivity: FragmentActivity() {
    private var mAppDatabase: AppDatabase ?= null

    protected val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAppDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database")
            .addMigrations(MIGRATION_2_3)
            .build()

        SharedPreferenceUtil.init(applicationContext)
    }

    fun getDatabase(): AppDatabase {
        return mAppDatabase!!
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE manga ADD COLUMN `index` INTEGER")
        }

    }

    inline fun <reified T : Any> Context.launchActivity(
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {}) {

        val intent = newIntent<T>(this)
        intent.init()
        startActivity(intent, options)
    }

    inline fun <reified T : Any> newIntent(context: Context): Intent =
        Intent(context, T::class.java)

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}