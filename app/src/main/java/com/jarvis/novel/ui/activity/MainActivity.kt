package com.jarvis.novel.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.google.zxing.integration.android.IntentIntegrator
import com.jarvis.novel.R
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.core.App
import com.jarvis.novel.data.MangaChapter
import com.jarvis.novel.data.NovelVersion
import com.jarvis.novel.ui.base.BaseActivity
import com.jarvis.novel.ui.fragment.MangaListFragment
import com.jarvis.novel.ui.fragment.NovelListFragment
import com.jarvis.novel.util.ConnectivityMonitor
import com.jarvis.novel.util.SharedPreferenceUtil
import com.jarvis.novel.viewModel.MangaViewModel
import com.jarvis.novel.viewModel.NetworkViewModel
import com.jarvis.novel.viewModel.NovelViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference


class MainActivity : BaseActivity() {
    private val networkViewModel: NetworkViewModel by viewModels()
    private val mangaListModel: MangaViewModel by viewModels()
    private val novelListModel: NovelViewModel by viewModels()
    private var inited = false

    private var integrator: IntentIntegrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.mainActivityContext = WeakReference(this)
        App.instance.isShowThumbnail = SharedPreferenceUtil.getIsShowThumbnail()

        showLoadingDialog()

        initView()
        initNetworkManager()

        ApiRepository().getNovelVersionList(
            complete = {},
            next = {
                if (it.code() == 200)
                    if (it.body() != null)
                        updateNovel(it.body()!!)
            },
            err = {
                toMainFragment()
            })

    }



    private fun initView() {
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_novel -> {
                    App.instance.addFragment(fragment = NovelListFragment(), containerLayoutId = R.id.fragment_container, fm = supportFragmentManager, type = "replace", isShowAnimation = false, addToBackStack = false, tag = "main_page")
                }
                R.id.tab_manga -> {
                    App.instance.addFragment(fragment = MangaListFragment(), containerLayoutId = R.id.fragment_container, fm = supportFragmentManager, type = "replace", isShowAnimation = false, addToBackStack = false, tag = "main_page")
                }
                R.id.tab_scan_qrcode -> {
                    integrator?.let {
                        it.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        it.setPrompt("")
                        it.setCameraId(0)
                        it.setBeepEnabled(false)
                        it.setBarcodeImageEnabled(true)
                        it.initiateScan()
                    } ?: run {
                        integrator = IntentIntegrator(this)
                        integrator!!.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        integrator!!.setPrompt("")
                        integrator!!.setCameraId(0)
                        integrator!!.setBeepEnabled(false)
                        integrator!!.setBarcodeImageEnabled(true)
                        integrator!!.initiateScan()
                    }
                }
            }
            true
        }

        switch_show_thumbnail.isChecked = SharedPreferenceUtil.getIsShowThumbnail()
        switch_show_thumbnail.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferenceUtil.setIsShowThumbnail(isChecked)
            App.instance.isShowThumbnail = isChecked
            mangaListModel.isShowThumbnail.postValue(isChecked)
            novelListModel.isShowThumbnail.postValue(isChecked)
        }

        switch_is_get_api.isChecked = SharedPreferenceUtil.getIsGetFromAPI()
        switch_is_get_api.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferenceUtil.setIsGetFromAPI(isChecked)
        }
    }

    private fun initNetworkManager() {
        ConnectivityMonitor(this, this) { isConnected ->
//            val networkStatus = networkViewModel.networkStatus.value
            if (isConnected) {
//                if (networkStatus == NetworkViewModel.NO_NETWORK || networkStatus == NetworkViewModel.UNKNOWN) {
                    networkViewModel.networkStatus.postValue(NetworkViewModel.ON_CONNECT)
//                }
            } else {
//                if (networkStatus == NetworkViewModel.ON_CONNECT || networkStatus == NetworkViewModel.UNKNOWN) {
                    networkViewModel.networkStatus.postValue(NetworkViewModel.NO_NETWORK)
//                }
            }
        }
    }

    private fun updateNovel(novelVersionList: List<NovelVersion>) {
        if (novelVersionList.isNotEmpty()) {
            val currentVersion = SharedPreferenceUtil.getNovelVersion()
            val listVersion = novelVersionList[0].version

            if (currentVersion < listVersion && currentVersion != 0) {
                novelVersionList.filter {
                    it.version < currentVersion
                }

                SharedPreferenceUtil.setUpdateNovelList(novelVersionList)
                SharedPreferenceUtil.setAddNovelList(novelVersionList)
                SharedPreferenceUtil.setNovelVersion(listVersion)
            }
            SharedPreferenceUtil.setNovelVersion(listVersion)
        }
        toMainFragment()
    }

    private fun toMainFragment() {
        inited = true
        App.instance.addFragment(fragment = MangaListFragment(), containerLayoutId = R.id.fragment_container, fm = supportFragmentManager, type = "replace", isShowAnimation = false, addToBackStack = false, tag = "main_page")
    }

    fun showLoadingDialog() {
        loading_dialog.visibility = View.VISIBLE
    }

    fun hideLoadingDialog() {
        loading_dialog.visibility = View.GONE
        txt_percent.visibility = View.GONE
        txt_percent.text = ""
    }

    fun showLoadingPercent() {
        txt_percent.visibility = View.VISIBLE
    }

    fun updateLoadingPercent(string: String?) {
        txt_percent.text = string ?: ""
    }

    override fun onBackPressed() {
        if (loading_dialog.isVisible) {
            return
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
            } else {
                showLoadingDialog()

                val resultArray = arrayListOf<String>()
                result.contents.split("&").let { list ->
                    list.forEach {
                        it.split("=").let { list2 ->
                            if (list2.size == 2) {
                                resultArray.add(list2[1])
                            }
                        }
                    }
                }

                val mangaVolumeId = resultArray[0]
                val mangaChapterId = resultArray[1]
                val lastPosition = resultArray[2].toInt()
                getDatabase().mangaVolumeDao().findOneById(mangaVolumeId).observeOnce(this, {
                    it?.let { volume ->
                        val chapter = volume.chapterList.find { chapter ->
                            chapter._id == mangaChapterId
                        }
                        chapter?.let { chapterObj ->

                            val index = volume.chapterList.indexOf(chapterObj)
                            val chapterList = mutableListOf<MangaChapter>()
                            chapterList.addAll(volume.chapterList.toCollection(ArrayList()))

                            chapterObj.lastPosition = lastPosition

                            chapterList[index] = chapterObj

                            volume.chapterList = chapterList

                            val observable = Observable.fromCallable {
                                getDatabase().mangaVolumeDao().insertOneReplace(volume)
                            }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                                    hideLoadingDialog()
                                }, {
                                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                                    hideLoadingDialog()
                                })

                            compositeDisposable.add(observable)
                        }
                    }
                })
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}