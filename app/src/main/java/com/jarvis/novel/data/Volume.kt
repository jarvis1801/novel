package com.jarvis.novel.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.jarvis.novel.database.DataConverter

@Entity
@TypeConverters(DataConverter::class)
data class Volume(
    val chapterList: List<Chapter>,
    @PrimaryKey
    val _id: String,
    val sectionName: String,
    val index: Int,
    val novelId: String,
    val createdAt: String,
    val updatedAt: String
)