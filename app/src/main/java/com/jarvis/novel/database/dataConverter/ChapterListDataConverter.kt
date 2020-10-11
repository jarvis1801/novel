package com.jarvis.novel.database.dataConverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jarvis.novel.data.Chapter
import java.lang.reflect.Type


class ChapterListDataConverter {
    @TypeConverter
    fun fromChapterList(chapter: List<Chapter?>?): String? {
        if (chapter == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Chapter?>?>() {}.type
        return gson.toJson(chapter, type)
    }

    @TypeConverter
    fun toChapterList(chapter: String?): List<Chapter>? {
        if (chapter == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Chapter?>?>() {}.type
        return gson.fromJson<List<Chapter>>(chapter, type)
    }
}