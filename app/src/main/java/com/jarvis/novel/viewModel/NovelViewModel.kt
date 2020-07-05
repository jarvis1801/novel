package com.jarvis.novel.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.data.Novel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class NovelViewModel : ViewModel() {
    var novelListLiveData: MutableLiveData<List<Novel>?> = MutableLiveData()

    var addUpdateNovelListLiveData: MutableLiveData<List<Novel>?> = MutableLiveData()
    var mAddUpdateNovelList: MutableList<Novel> = mutableListOf()

    fun getNovelList() {
        ApiRepository().getNovelList(
            complete = {

            },
            next = {
                if (it.code().toString().startsWith("2") || it.code().toString().startsWith("3")) {
                    novelListLiveData.postValue(it.body())
                } else {
                    novelListLiveData.postValue(null)
                }
            },
            err = {
                novelListLiveData.postValue(null)
            }
        )
    }
    
    fun addUpdateNovelList(novelIdList: List<String>) {
        mAddUpdateNovelList.clear()

        Observable.fromIterable(novelIdList)
            .concatMap {
                id -> getNovelById(id)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<Novel>> {
                override fun onComplete() {
                    addUpdateNovelListLiveData.postValue(mAddUpdateNovelList)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Response<Novel>) {
                    val code = t.code().toString()
                    if (code.startsWith("2") || code.startsWith("3")) {
                        if (t.body() != null) {
                            mAddUpdateNovelList.add(t.body()!!)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                }

            })
    }

    private fun getNovelById(novelId: String): Observable<Response<Novel>> {
        return ApiRepository().getNovelByIdObservable(novelId)
    }
}