package com.jarvis.novel.util

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceUtil {
    private lateinit var mContext: Context

    private const val SPKEY = "493f5h695fh9n"
    private const val TYPE_CONTENT_FONT_SCALE = "CONTENT_FONT_SCALE"

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
}