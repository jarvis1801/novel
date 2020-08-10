package com.jarvis.novel.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.data.Chapter

class NovelContentViewModel : ViewModel() {
    val chapterLiveData = MutableLiveData<Chapter>()
}