package com.jarvis.novel.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.jarvis.novel.database.dataConverter.NovelVersionNovelIdListConverter

@Entity(tableName = "novelVersion")
@TypeConverters(NovelVersionNovelIdListConverter::class)
data class NovelVersion(
    @PrimaryKey
    val _id: String,
    val version: Int,
    val novelIdList: List<NovelVersionNovelIdList>,
    val createdAt: String
)

data class NovelVersionNovelIdList(
    val data: String,
    val type: String
)