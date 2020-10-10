package com.jarvis.novel.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.jarvis.novel.database.dataConverter.MediaConverter
import java.io.Serializable

@Entity
@TypeConverters(MediaConverter::class)
data class Manga(
    @SerializedName("thumbnailMain") val thumbnailMain : Media?,
    @SerializedName("thumbnailSection") val thumbnailSection : Media?,
    @SerializedName("isEnd") val isEnd : Boolean,
    @PrimaryKey @SerializedName("_id") val _id : String,
    @SerializedName("name") val name : String,
    @SerializedName("author") val author : String,
    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("updatedAt") val updatedAt : String,
    var index: Int?
) : Serializable