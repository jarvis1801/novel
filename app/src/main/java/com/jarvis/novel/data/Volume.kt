package com.jarvis.novel.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.jarvis.novel.database.dataConverter.ChapterListDataConverter

@Entity
@TypeConverters(ChapterListDataConverter::class)
data class Volume(
    var chapterList: List<Chapter>,
    @PrimaryKey
    val _id: String,
    val sectionName: String,
    val index: Int,
    val novelId: String,
    val createdAt: String,
    val updatedAt: String,
    var versionNumber: Int = 0
)