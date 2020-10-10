package com.jarvis.novel.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.jarvis.novel.database.dataConverter.MediaConverter

@Entity(tableName = "novel")
@TypeConverters(MediaConverter::class)
data class Novel(
    val isEnd: Boolean,
    @PrimaryKey
    val _id: String,
    val name: String,
    val author: String,
    val createdAt: String,
    val updatedAt: String,
    val thumbnailMain: Media?,
    val thumbnailSection: Media?,
    var versionNumber: Int = 0
)