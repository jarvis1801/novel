package com.jarvis.novel.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.data.Novel

class NovelViewModel : ViewModel() {
    var novelListLiveData: MutableLiveData<List<Novel>?> = MutableLiveData()

    fun getNovelList() {
        ApiRepository().getNovelList(
            complete = {

            },
            next = {
                if (it.code().toString().startsWith("2") || it.code().toString().startsWith("3")) {
                    novelListLiveData.postValue(it.body())
                } else {
                    novelListLiveData.postValue(null)
                }
            },
            err = {
                novelListLiveData.postValue(null)
            }
        )
    }
}