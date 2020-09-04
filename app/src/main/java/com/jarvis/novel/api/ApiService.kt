package com.jarvis.novel.api

import android.util.Log
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Novel
import com.jarvis.novel.data.NovelVersion
import com.jarvis.novel.data.Volume
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface MasterService {
    @GET("api/novelList")
    fun getNovelList(): Observable<Response<List<Novel>>>

    @GET("api/novel/{novelId}")
    fun getNovelById(@Path("novelId") novelId: String): Observable<Response<Novel>>

    @GET("api/volumeList/{novelId}")
    fun getVolumeList(@Path("novelId") novelId: String): Observable<Response<List<Volume>>>

    @GET("api/novelVersionList")
    fun getNovelVersionList(): Observable<Response<List<NovelVersion>?>>

    companion object Factory {
        fun retrofitService(): MasterService = Retrofit.Builder()
            .baseUrl(App.context.getString(R.string.base_url))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getOkHttpClient())
            .build()
            .create(MasterService::class.java)

        private fun getOkHttpClient() : OkHttpClient {
            val timeout: Long = 10

            return OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                        Log.d("OkHttp", "log: $message")
                    }).setLevel(
                        HttpLoggingInterceptor.Level.BODY
                    )
                )
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build()
        }
    }

}