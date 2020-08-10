package com.jarvis.novel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "novel")
data class Novel(
    val isEnd: Boolean,
    @PrimaryKey
    val _id: String,
    val name: String,
    val author: String,
    val createdAt: String,
    val updatedAt: String,
    val thumbnailMain: String,
    val thumbnailSection: String,
    var versionNumber: Int = 0
)