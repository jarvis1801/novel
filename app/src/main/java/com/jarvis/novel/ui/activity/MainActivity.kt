package com.jarvis.novel.ui.activity

import android.os.Bundle
import android.util.Log
import com.jarvis.novel.R
import com.jarvis.novel.api.ApiRepository
import com.jarvis.novel.core.App
import com.jarvis.novel.data.NovelVersion
import com.jarvis.novel.ui.base.BaseActivity
import com.jarvis.novel.ui.fragment.NovelListFragment
import com.jarvis.novel.util.SharedPreferenceUtil

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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
        App.instance.addFragment(fragment = NovelListFragment(), containerLayoutId = R.id.fragment_container, fm = supportFragmentManager, isShowAnimation = false, addToBackStack = false)
    }
}