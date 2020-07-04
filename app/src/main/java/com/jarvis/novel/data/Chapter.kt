package com.jarvis.novel.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Chapter(
    val title: String,
    @PrimaryKey
    val _id: String,
    val sectionName: String,
    val index: Int,
    val volumeId: String,
    @Embedded
    val paragraph: List<Paragraph>,
    val createdAt: String,
    val updatedAt: String
) : Serializable

data class Paragraph(
    val data: String,
    val type: String
)