package com.jarvis.novel.database.dataConverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jarvis.novel.data.Chapter
import com.jarvis.novel.data.MangaChapter
import java.lang.reflect.Type


class MangaChapterListDataConverter {
    @TypeConverter
    fun fromChapterList(mangaChapter: List<MangaChapter?>?): String? {
        if (mangaChapter == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<MangaChapter?>?>() {}.type
        return gson.toJson(mangaChapter, type)
    }

    @TypeConverter
    fun toChapterList(mangaChapter: String?): List<MangaChapter>? {
        if (mangaChapter == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<MangaChapter?>?>() {}.type
        return gson.fromJson<List<MangaChapter>>(mangaChapter, type)
    }
}