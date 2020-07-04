package com.jarvis.novel.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.data.Novel
import com.jarvis.novel.data.Volume

class VolumeChapterViewModel : ViewModel() {
    val volumeListLiveData: MutableLiveData<List<Volume>?> = MutableLiveData()

    val novelLiveData: MutableLiveData<Novel?> = MutableLiveData()

    val mNovelId: MutableLiveData<String?> = MutableLiveData()

    fun getVolumeList() {
        if (mNovelId.value == null) {
            return
        }

        ApiRepository().getVolumeList(mNovelId.value!!,
            complete = {

            },
            next = {
                if (it.code().toString().startsWith("2") || it.code().toString().startsWith("3")) {
                    volumeListLiveData.postValue(it.body())
                } else {
                    volumeListLiveData.postValue(null)
                }
            },
            err = {
                volumeListLiveData.postValue(null)
            }
        )
    }
}