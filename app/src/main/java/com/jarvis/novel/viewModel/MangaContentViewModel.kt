package com.jarvis.novel.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.data.MangaChapter
import com.jarvis.novel.data.MangaVolume

class MangaContentViewModel : ViewModel() {
    val chapter: MutableLiveData<MangaChapter?> = MutableLiveData(null)

    val volume: MutableLiveData<MangaVolume?> = MutableLiveData(null)

    val isShowOverlay: MutableLiveData<Boolean?> = MutableLiveData(false)

    fun reset() {
        chapter.postValue(null)
        volume.postValue(null)
    }
}