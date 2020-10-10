package com.jarvis.novel.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.jarvis.novel.database.AppDatabase
import com.jarvis.novel.ui.activity.MainActivity
import io.reactivex.disposables.CompositeDisposable


abstract class BaseFragment : Fragment(), LifecycleObserver {

    private var isBlockObserve: Boolean = false

    protected val compositeDisposable = CompositeDisposable()

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    fun <T> LiveData<T>.observeWithoutOnResume(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        if (isBlockObserve()) {
            return
        }
        observe(lifecycleOwner, observer)
    }

    fun getDataBase(): AppDatabase {
        return (requireActivity() as MainActivity).getDatabase()
    }

    private fun isBlockObserve(): Boolean {
        return isBlockObserve
    }

    override fun onResume() {
        super.onResume()

        isBlockObserve = false
    }

    override fun onPause() {
        super.onPause()

        isBlockObserve = true
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()

        System.gc()
    }

    fun showLoadingDialog() {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.showLoadingDialog()
    }

    fun hideLoadingDialog() {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.hideLoadingDialog()
    }

    fun showLoadingPercent() {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.showLoadingPercent()
    }

    fun updateLoadingPercent(string: String?) {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.updateLoadingPercent(string)
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
}