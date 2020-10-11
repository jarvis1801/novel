package com.jarvis.novel.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.R
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.data.Manga
import com.jarvis.novel.data.MangaVolume
import com.jarvis.novel.data.Novel
import com.jarvis.novel.util.GlideHelper
import com.jarvis.novel.util.SharedPreferenceUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class MangaViewModel : ViewModel() {
    var mangaListLiveData: MutableLiveData<List<Manga>?> = MutableLiveData()

    var addUpdateMangaListLiveData: MutableLiveData<List<Manga>?> = MutableLiveData()
    var mAddUpdateMangaList: MutableList<Manga> = mutableListOf()

    var isShowThumbnail: MutableLiveData<Boolean?> = MutableLiveData()

    var mHideLoadingDialog: MutableLiveData<Boolean?> = MutableLiveData()

    fun reset() {
        mangaListLiveData.postValue(null)
        addUpdateMangaListLiveData.postValue(null)
        mAddUpdateMangaList.clear()
        isShowThumbnail.postValue(null)
        mHideLoadingDialog.postValue(null)
    }

    fun getMangaList(context: Context) {
        ApiRepository().getMangaList(
            complete = {

            },
            next = {
                if (it.code().toString().startsWith("2") || it.code().toString().startsWith("3")) {
                    mangaListLiveData.postValue(it.body())
                    prefetchImage(context, it.body())
                } else {
                    mangaListLiveData.postValue(null)
                }
            },
            err = {
                mangaListLiveData.postValue(null)
            }
        )
    }

    fun addUpdateMangaList(novelIdList: List<String>) {
        mAddUpdateMangaList.clear()

        var sortedList = novelIdList

        Observable.fromIterable(novelIdList)
            .concatMap {
                    id -> getMangaById(id)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<Manga>> {
                override fun onComplete() {
                    SharedPreferenceUtil.setAddNovelListByStringArray(sortedList)
                    addUpdateMangaListLiveData.postValue(mAddUpdateMangaList)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Response<Manga>) {
                    val code = t.code().toString()
                    if (code.startsWith("2") || code.startsWith("3")) {
                        if (t.body() != null) {
                            sortedList = sortedList.filter {
                                it == t.body()!!._id
                            }
                            mAddUpdateMangaList.add(t.body()!!)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    addUpdateMangaListLiveData.postValue(mAddUpdateMangaList)
                }

            })
    }

    private fun getMangaById(mangaId: String): Observable<Response<Manga>> {
        return ApiRepository().getMangaByIdObservable(mangaId)
    }

    private fun prefetchImage(context: Context, mangaList: List<Manga>?) {
        mangaList?.forEach {
            it.thumbnailMain?.content?.let { string ->
                var isLast = false

                if (mangaList[mangaList.size - 1]._id == it._id) {
                    isLast = true
                }

                GlideHelper().preloadImage(
                    context,
                    "${context.getString(R.string.base_url)}file/$string",
                    string,
                    isLast
                )
            }

            it.thumbnailSection?.content?.let { string ->
                var isLast = false

                if (mangaList[mangaList.size - 1]._id == it._id) {
                    isLast = true
                }

                GlideHelper().preloadImage(
                    context,
                    "${context.getString(R.string.base_url)}file/$string",
                    string,
                    isLast
                ) {
                    mHideLoadingDialog.postValue(true)
                }
            }

        }
    }
}