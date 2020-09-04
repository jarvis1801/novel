package com.jarvis.novel.ui.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.jarvis.novel.database.AppDatabase
import com.jarvis.novel.ui.activity.MainActivity

abstract class BaseFragment : Fragment(), LifecycleObserver {

    private var isBlockObserve: Boolean = false

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
}