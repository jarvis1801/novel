package com.jarvis.novel.database.dataConverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jarvis.novel.data.NovelVersionNovelIdList
import java.lang.reflect.Type

class NovelVersionNovelIdListConverter {
    @TypeConverter
    fun fromNovelVersionNovelIdList(novelIdList: List<NovelVersionNovelIdList>?): String? {
        if (novelIdList == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<NovelVersionNovelIdList?>?>() {}.type
        return gson.toJson(novelIdList, type)
    }

    @TypeConverter
    fun toNovelVersionNovelIdList(novelIdList: String?): List<NovelVersionNovelIdList>? {
        if (novelIdList == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<NovelVersionNovelIdList?>?>() {}.type
        return gson.fromJson<List<NovelVersionNovelIdList>>(novelIdList, type)
    }
}