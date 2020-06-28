package com.jarvis.novel.core

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class App : Application() {

    fun addFragment(fragment: Fragment, containerLayoutId: Int, tag: String? = null, type: String = "", addToBackStack: Boolean = false
                    , fm: FragmentManager) {
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction()

        if (type == "add") {
            fragmentTransaction.add(containerLayoutId, fragment, tag)
        } else {
            fragmentTransaction.replace(containerLayoutId, fragment, tag)
        }

        if (addToBackStack) {
            fragmentTransaction.addToBackStack("")
        }

        fragmentTransaction.commit()
    }
}