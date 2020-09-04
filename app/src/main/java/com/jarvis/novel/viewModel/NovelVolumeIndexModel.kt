package com.jarvis.novel.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.data.Volume

class NovelVolumeIndexModel : ViewModel() {
    val volumeListLiveData: MutableLiveData<List<Volume>?> = MutableLiveData()

    val mNovelId: MutableLiveData<String?> = MutableLiveData()

    val mUpdateEndChapter: MutableLiveData<String?> = MutableLiveData()

    fun getVolumeList(novelId: String, updateVolumeCallback: () -> Unit? = {}) {
        if (mNovelId.value == null) {
            return
        }

        ApiRepository().getVolumeList(novelId,
            complete = {

            },
            next = {
                if (it.code().toString().startsWith("2") || it.code().toString().startsWith("3") || !it.body().isNullOrEmpty()) {
                    volumeListLiveData.postValue(it.body())
                    updateVolumeCallback()
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