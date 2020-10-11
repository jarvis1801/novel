package com.jarvis.novel.database.dataConverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jarvis.novel.data.Chapter
import com.jarvis.novel.data.Media
import java.lang.reflect.Type


class MediaConverter {
    @TypeConverter
    fun fromMedia(media: Media?): String? {
        if (media == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<Media?>() {}.type
        return gson.toJson(media, type)
    }

    @TypeConverter
    fun toMedia(media: String?): Media? {
        if (media == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<Media?>() {}.type
        return gson.fromJson<Media>(media, type)
    }
}