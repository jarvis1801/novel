package com.jarvis.novel.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jarvis.novel.data.Chapter
import java.lang.reflect.Type


class DataConverter {
    @TypeConverter
    fun fromChapterList(countryLang: List<Chapter?>?): String? {
        if (countryLang == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Chapter?>?>() {}.type
        return gson.toJson(countryLang, type)
    }

    @TypeConverter
    fun toChapterList(countryLangString: String?): List<Chapter>? {
        if (countryLangString == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Chapter?>?>() {}.type
        return gson.fromJson<List<Chapter>>(countryLangString, type)
    }
}