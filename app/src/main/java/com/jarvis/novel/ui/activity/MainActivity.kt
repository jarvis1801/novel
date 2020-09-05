package com.jarvis.novel.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.jarvis.novel.R
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.core.App
import com.jarvis.novel.data.NovelVersion
import com.jarvis.novel.ui.base.BaseActivity
import com.jarvis.novel.ui.fragment.NovelListFragment
import com.jarvis.novel.util.SharedPreferenceUtil
import com.jarvis.novel.viewModel.NetworkViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference
import com.jarvis.novel.util.ConnectivityMonitor


class MainActivity : BaseActivity() {
    private val networkViewModel: NetworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.mainActivityContext = WeakReference(this)

        showLoadingDialog()

        initNetworkManager()

        ApiRepository().getNovelVersionList(complete = {

            },
            next = {
                if (it.code() == 200)
                    if (it.body() != null)
                        updateConfig(it.body()!!)
            },
            err = {
                toMainFragment()
            })
    }

    private fun initNetworkManager() {
        ConnectivityMonitor(this, this) { isConnected ->
//            val networkStatus = networkViewModel.networkStatus.value
//            Log.d("chris", networkStatus.toString())
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

    private fun updateConfig(novelVersionList: List<NovelVersion>) {
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
        App.instance.addFragment(fragment = NovelListFragment(), containerLayoutId = R.id.fragment_container, fm = supportFragmentManager, type = "replace", isShowAnimation = false, addToBackStack = false, tag = "main_page")
    }

    fun showLoadingDialog() {
        loading_dialog.visibility = View.VISIBLE
    }

    fun hideLoadingDialog() {
        loading_dialog.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (loading_dialog.isVisible) {
            return
        }
        super.onBackPressed()
    }
}