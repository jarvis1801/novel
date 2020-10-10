package com.jarvis.novel.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.jarvis.novel.database.dataConverter.ChapterListDataConverter
import com.jarvis.novel.database.dataConverter.MangaChapterListDataConverter
import java.io.Serializable

@Entity
@TypeConverters(MangaChapterListDataConverter::class)
data class MangaVolume(
    @SerializedName("chapterList") var chapterList : List<MangaChapter>,
    @SerializedName("isStickyHeader") val isStickyHeader : Boolean,
    @PrimaryKey @SerializedName("_id") val _id : String,
    @SerializedName("sectionName") val sectionName : String,
    @SerializedName("index") val index : Int,
    @SerializedName("type") val type : String,
    @SerializedName("manga") val manga : String,
    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("updatedAt") val updatedAt : String,
) : Serializable