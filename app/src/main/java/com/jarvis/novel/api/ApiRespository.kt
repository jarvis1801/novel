package com.jarvis.novel.api


import com.jarvis.novel.data.*
import com.jarvis.novel.util.SharedPreferenceUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class ApiRepository {
    private var mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    private var apiService: MasterService = MasterService.retrofitService()


    fun dispose() {
        mCompositeDisposable.dispose()
    }

    fun getNovelList(complete: () -> Unit, next: (t: Response<List<Novel>>) -> Unit, err: (e: Throwable) -> Unit) {
        apiService.getNovelList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<Novel>>> {
                override fun onComplete() {
                    complete()
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<Novel>>) {
                    next(t)
                }

                override fun onError(e: Throwable) {
                    err(e)
                }
            })
    }

    fun getVolumeList(novelId: String, complete: () -> Unit, next: (t: Response<List<Volume>>) -> Unit, err: (e: Throwable) -> Unit) {
        apiService.getVolumeList(novelId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<Volume>>> {
                override fun onComplete() {
                    complete()
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<Volume>>) {
                    next(t)
                }

                override fun onError(e: Throwable) {
                    err(e)
                }
            })
    }

    fun getNovelVersionList(complete: () -> Unit, next: (t: Response<List<NovelVersion>?>) -> Unit, err: (e: Throwable) -> Unit) {
        if (!SharedPreferenceUtil.getIsGetFromAPI()) {
            err(Exception("Disable get api"))
            return
        }
        apiService.getNovelVersionList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<NovelVersion>?>> {
                override fun onComplete() {
                    complete()
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<NovelVersion>?>) {
                    next(t)
                }

                override fun onError(e: Throwable) {
                    err(e)
                }
            })
    }

    fun getNovelByIdObservable(novelId: String): Observable<Response<Novel>> {
        return apiService.getNovelById(novelId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getMangaList(complete: () -> Unit, next: (t: Response<List<Manga>>) -> Unit, err: (e: Throwable) -> Unit) {
        apiService.getMangaList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<Manga>>> {
                override fun onComplete() {
                    complete()
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<Manga>>) {
                    next(t)
                }

                override fun onError(e: Throwable) {
                    err(e)
                }
            })
    }

    fun getMangaVolumeList(mangaId: String, complete: () -> Unit, next: (t: Response<List<MangaVolume>>) -> Unit, err: (e: Throwable) -> Unit) {
        apiService.getMangaVolumeList(mangaId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<MangaVolume>>> {
                override fun onComplete() {
                    complete()
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<MangaVolume>>) {
                    next(t)
                }

                override fun onError(e: Throwable) {
                    err(e)
                }
            })
    }

    fun getMangaByIdObservable(mangaId: String): Observable<Response<Manga>> {
        return apiService.getMangaById(mangaId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}