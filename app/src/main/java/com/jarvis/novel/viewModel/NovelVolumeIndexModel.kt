package com.jarvis.novel.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.data.Volume

class NovelVolumeIndexModel : ViewModel() {
    val volumeListLiveData: MutableLiveData<List<Volume>?> = MutableLiveData()

    val mNovelId: MutableLiveData<String?> = MutableLiveData()

    fun getVolumeList() {
        if (mNovelId.value == null) {
            return
        }

        ApiRepository().getVolumeList(mNovelId.value!!,
            complete = {

            },
            next = {
                if (it.code().toString().startsWith("2") || it.code().toString().startsWith("3") || !it.body().isNullOrEmpty()) {
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