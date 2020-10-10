package com.jarvis.novel.data

import com.google.gson.annotations.SerializedName

data class Media(
    @SerializedName("content") val content : String?,
    @SerializedName("index") val index : Int,
    @SerializedName("_id") val _id : String,
    @SerializedName("type") val type : String,
    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("updatedAt") val updatedAt : String,
)