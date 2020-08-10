package com.jarvis.novel.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.data.Novel
import com.jarvis.novel.data.Volume

class VolumeChapterViewModel : ViewModel() {
    val novelLiveData: MutableLiveData<Novel?> = MutableLiveData()

    val mNovelId: MutableLiveData<String?> = MutableLiveData()
}