package com.jarvis.novel.ui.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.jarvis.novel.database.AppDatabase
import com.jarvis.novel.ui.activity.MainActivity

abstract class BaseFragment : Fragment(), LifecycleObserver {

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    fun getDataBase(): AppDatabase {
        return (requireActivity() as MainActivity).getDatabase()
    }
}