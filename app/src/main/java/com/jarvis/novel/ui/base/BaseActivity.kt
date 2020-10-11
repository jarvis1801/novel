package com.jarvis.novel.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jarvis.novel.R
import com.jarvis.novel.database.AppDatabase
import com.jarvis.novel.util.SharedPreferenceUtil
import io.reactivex.disposables.CompositeDisposable


abstract class BaseActivity: FragmentActivity() {
    private var mAppDatabase: AppDatabase ?= null

    protected val compositeDisposable = CompositeDisposable()

    private lateinit var loadingView: ViewGroup
    protected var fullLayout: FrameLayout? = null
    protected var subActivityContent: FrameLayout? = null

    override fun setContentView(layoutResID: Int) {
        val inflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        fullLayout = inflater.inflate(R.layout.activity_base, null) as FrameLayout
        loadingView = fullLayout!!.findViewById(R.id.loading_dialog) as FrameLayout
        subActivityContent = fullLayout!!.findViewById(R.id.content_frame) as FrameLayout

        inflater.inflate(layoutResID, subActivityContent, true)

        super.setContentView(fullLayout)
    }

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
        noinline init: Intent.() -> Unit = {}
    ) {

        val intent = newIntent<T>(this)
        intent.init()
        startActivity(intent, options)
        overridePendingTransition(R.anim.enter, R.anim.exit)
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

    override fun onBackPressed() {
        if (loadingView.isVisible) {
            return
        }

        super.onBackPressed()
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit)
    }

    fun showLoadingDialog() {
        loadingView.visibility = View.VISIBLE
    }

    fun hideLoadingDialog() {
        val txtPercent = loadingView.findViewById<TextView>(R.id.txt_percent)
        loadingView.visibility = View.GONE
        txtPercent?.visibility = View.GONE
        txtPercent?.text = ""
    }

    fun showLoadingPercent() {
        val txtPercent = loadingView.findViewById<TextView>(R.id.txt_percent)
        txtPercent?.visibility = View.VISIBLE
    }

    fun updateLoadingPercent(string: String?) {
        val txtPercent = loadingView.findViewById<TextView>(R.id.txt_percent)
        txtPercent?.text = string ?: ""
    }
}