package com.jarvis.novel.data

import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class MangaChapter(
    @Embedded @SerializedName("content") var content : List<Media>,
    @SerializedName("isStickyHeader") val isStickyHeader : Boolean,
    @SerializedName("_id") val _id : String,
    @SerializedName("sectionName") val sectionName : String,
    @SerializedName("index") val index : Int,
    @SerializedName("type") val type : String,
    @SerializedName("manga") val manga : String,
    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("updatedAt") val updatedAt : String,

    var isRead: Boolean = false,
    var lastPosition: Int = 0
) : Serializable