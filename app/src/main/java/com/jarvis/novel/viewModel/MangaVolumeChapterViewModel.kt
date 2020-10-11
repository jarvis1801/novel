package com.jarvis.novel.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.data.Manga

class MangaVolumeChapterViewModel : ViewModel() {
    val mangaLiveData: MutableLiveData<Manga?> = MutableLiveData()

    val mMangaId: MutableLiveData<String?> = MutableLiveData()
}