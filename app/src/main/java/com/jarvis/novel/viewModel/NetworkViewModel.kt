package com.jarvis.novel.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NetworkViewModel : ViewModel() {
    companion object {
        const val ON_CONNECT = 0
        const val NO_NETWORK = 1
        const val UNKNOWN = 2
    }
    val networkStatus = MutableLiveData<Int>(UNKNOWN)

}