package com.jarvis.novel.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.R
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.data.MangaVolume
import com.jarvis.novel.util.GlideHelper
import com.jarvis.novel.util.SharedPreferenceUtil
import java.io.File

class MangaVolumeIndexViewModel : ViewModel() {
    val mangaChapterListLiveData: MutableLiveData<List<MangaVolume>?> = MutableLiveData()

    val mMangaId: MutableLiveData<String?> = MutableLiveData()

    val mUpdateEndChapter: MutableLiveData<String?> = MutableLiveData()

    val mHideLoadingDialog: MutableLiveData<Boolean?> = MutableLiveData()

    val mUpdateDownloadSize: MutableLiveData<Int?> = MutableLiveData()

    fun getVolumeList(context: Context, novelId: String, updateVolumeCallback: () -> Unit? = {}) {
        if (mMangaId.value == null) {
            return
        }

        ApiRepository().getMangaVolumeList(novelId,
            complete = {

            },
            next = {
                if (it.code().toString().startsWith("2") || it.code().toString().startsWith("3") || !it.body().isNullOrEmpty()) {
                    mangaChapterListLiveData.postValue(it.body())
                    prefetchImage(context, it.body())
                    updateVolumeCallback()
                } else {
                    mangaChapterListLiveData.postValue(null)
                    mHideLoadingDialog.postValue(true)
                }
            },
            err = {
                mangaChapterListLiveData.postValue(null)
                mHideLoadingDialog.postValue(true)
            }
        )
    }

    fun reset() {
        mangaChapterListLiveData.postValue(null)
        mMangaId.postValue(null)
        mUpdateEndChapter.postValue(null)
        mHideLoadingDialog.postValue(null)
    }

    fun prefetchImage(context: Context, list: List<MangaVolume>?) {
        var imageSize = 0
        var completeImage = 0
        list?.forEach { mangaVolume ->
            mangaVolume.chapterList.forEach { mangaChapter ->
                imageSize += mangaChapter.content.size
            }
        }
        list?.forEach { mangaVolume ->
            mangaVolume.chapterList.forEach { mangaChapter ->
                mangaChapter.content.forEach {
                    var isLast = false

                    if (mangaVolume.chapterList[mangaVolume.chapterList.size - 1]._id == mangaChapter._id &&
                        mangaChapter.content[mangaChapter.content.size - 1]._id == it._id) {
                        isLast = true
                    }

                    if (SharedPreferenceUtil.getIsGetFromAPI()) {
                        it.content?.let { imagePath ->
                            val isFileExist = checkFileExist(imagePath)

                            if (isLast || !isFileExist) {
                                GlideHelper().preloadImage(
                                    context,
                                    "${context.getString(R.string.base_url)}file/${imagePath}",
                                    imagePath,
                                    isLast,
                                    finishCallback = {
                                        mHideLoadingDialog.postValue(true)
                                    },
                                    downloadCallback = {
                                        completeImage += 1
                                        val value = (completeImage.toDouble().div(imageSize.toDouble()).times(100.toDouble())).toInt()
                                        mUpdateDownloadSize.postValue(value)
                                    }
                                )
                            } else {
                                Log.d("chris", "exist $imagePath")
                            }
                        } ?: run {
                            if (isLast) {
                                mHideLoadingDialog.postValue(true)
                            }
                        }
                    } else {
                        mHideLoadingDialog.postValue(true)
                    }
                }
            }
        }
    }

    private fun checkFileExist(imagePath: String): Boolean {
        val file = File("${GlideHelper.CACHE_DIR}$imagePath.0")
        if (file.exists()) {
            return true
        }
        return false
    }
}