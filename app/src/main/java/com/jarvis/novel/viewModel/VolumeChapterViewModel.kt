package com.jarvis.novel.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.data.Novel

class VolumeChapterViewModel : ViewModel() {
    val novelLiveData: MutableLiveData<Novel?> = MutableLiveData()

    val mNovelId: MutableLiveData<String?> = MutableLiveData()
}