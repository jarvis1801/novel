package com.jarvis.novel.util

import android.content.Context
import android.content.SharedPreferences
import com.jarvis.novel.data.NovelVersion

object SharedPreferenceUtil {
    private lateinit var mContext: Context

    private const val SPKEY = "493f5h695fh9n"
    private const val TYPE_CONTENT_FONT_SCALE = "CONTENT_FONT_SCALE"
    private const val TYPE_NOVEL_VERSION = "NOVEL_VERSION"
    private const val TYPE_ADD_NOVEL_LIST = "ADD_NOVEL_LIST"
    private const val TYPE_UPDATE_NOVEL_LIST = "UPDATE_NOVEL_LIST"
    private const val TYPE_IS_GET_FROM_API = "IS_GET_FROM_API"

    private const val TYPE_IS_SHOW_THUMBNAIL = "IS_SHOW_THUMBNAIL"

    fun init(context: Context) {
        mContext = context
    }

    private fun getSharedPreferences(): SharedPreferences {
        return mContext.getSharedPreferences(SPKEY, Context.MODE_PRIVATE)
    }

    private fun getSharedPreferencesEditor(): SharedPreferences.Editor {
        return getSharedPreferences().edit()
    }

    fun getFontScale(): Float {
        return getSharedPreferences().getFloat(TYPE_CONTENT_FONT_SCALE, 1f)
    }

    fun setFontScale(scaleSize: Float) {
        val editor = getSharedPreferencesEditor()
        editor.putFloat(TYPE_CONTENT_FONT_SCALE, scaleSize)
        editor.commit()
    }

    fun getIsShowThumbnail(): Boolean {
        return getSharedPreferences().getBoolean(TYPE_IS_SHOW_THUMBNAIL, true)
    }

    fun setIsShowThumbnail(isShowThumbnail: Boolean) {
        val editor = getSharedPreferencesEditor()
        editor.putBoolean(TYPE_IS_SHOW_THUMBNAIL, isShowThumbnail)
        editor.commit()
    }

    fun getNovelVersion(): Int {
        return getSharedPreferences().getInt(TYPE_NOVEL_VERSION, 0)
    }

    fun setNovelVersion(novelVersion: Int) {
        val editor = getSharedPreferencesEditor()
        editor.putInt(TYPE_NOVEL_VERSION, novelVersion)
        editor.commit()
    }

    fun getIsGetFromAPI(): Boolean {
        return getSharedPreferences().getBoolean(TYPE_IS_GET_FROM_API, true)
    }

    fun setIsGetFromAPI(isGetFromAPI: Boolean) {
        val editor = getSharedPreferencesEditor()
        editor.putBoolean(TYPE_IS_GET_FROM_API, isGetFromAPI)
        editor.commit()
    }

    fun getAddNovelList(): List<String>? {
        val list = getSharedPreferences().getString(TYPE_ADD_NOVEL_LIST, null) ?: return null
        return list.split(",")
    }

    fun setAddNovelList(novelVersionList: List<NovelVersion>) {
        val previousList = mutableListOf<String>()
        if (getAddNovelList() != null) {
            previousList.addAll(getAddNovelList()!!)
        }

        novelVersionList.forEach { it ->
            it.novelIdList.filterNot {
                it.type != "A"
            }.forEach {
                previousList.add(it.data)
            }
        }

        val uniqueList = previousList.distinct()

        val sb = StringBuilder()
        for (i in uniqueList.indices) {
            if (uniqueList[i].isNotEmpty()) {
                sb.append(uniqueList[i])
                if (i < uniqueList.size - 1) {
                    sb.append(",")
                }
            }
        }

        val editor = getSharedPreferencesEditor()
        editor.putString(TYPE_ADD_NOVEL_LIST, sb.toString())
        editor.commit()
    }

    fun setAddNovelListByStringArray(novelIdList: List<String>) {
        val sb = StringBuilder()
        for (i in novelIdList.indices) {
            if (novelIdList[i].isNotEmpty()) {
                sb.append(novelIdList[i])
                if (i < novelIdList.size - 1) {
                    sb.append(",")
                }
            }
        }

        val editor = getSharedPreferencesEditor()
        editor.putString(TYPE_ADD_NOVEL_LIST, if (novelIdList.isNotEmpty()) { sb.toString() } else { null })
        editor.commit()
    }

    fun getUpdateNovelList(): List<String>? {
        val list = getSharedPreferences().getString(TYPE_UPDATE_NOVEL_LIST, null) ?: return null
        return list.split(",")
    }

    fun setUpdateNovelList(novelVersionList: List<NovelVersion>) {
        val previousList = mutableListOf<String>()
        if (getUpdateNovelList() != null) {
            previousList.addAll(getUpdateNovelList()!!)
        }

        novelVersionList.forEach { it ->
            it.novelIdList.filterNot {
                it.type != "U"
            }.forEach {
                previousList.add(it.data)
            }
        }

        val uniqueList = previousList.distinct()

        val sb = StringBuilder()
        for (i in uniqueList.indices) {
            if (uniqueList[i].isNotEmpty()) {
                sb.append(uniqueList[i])
                if (i < uniqueList.size - 1) {
                    sb.append(",")
                }
            }
        }

        val editor = getSharedPreferencesEditor()
        editor.putString(TYPE_UPDATE_NOVEL_LIST, sb.toString())
        editor.commit()
    }

    fun setUpdateNovelListByStringArray(novelIdList: List<String>) {
        val sb = StringBuilder()
        for (i in novelIdList.indices) {
            if (novelIdList[i].isNotEmpty()) {
                sb.append(novelIdList[i])
                if (i < novelIdList.size - 1) {
                    sb.append(",")
                }
            }
        }

        val editor = getSharedPreferencesEditor()
        editor.putString(TYPE_UPDATE_NOVEL_LIST, if (novelIdList.isNotEmpty()) { sb.toString() } else { null })
        editor.commit()
    }
}