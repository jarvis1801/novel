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
    val thumbnailMainBlob: ByteArray?,
    val thumbnailSectionBlob: ByteArray?,
    var versionNumber: Int = 0
) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Novel

        if (thumbnailMainBlob != null) {
            if (other.thumbnailMainBlob == null) return false
            if (!thumbnailMainBlob.contentEquals(other.thumbnailMainBlob)) return false
        } else if (other.thumbnailMainBlob != null) return false
        if (thumbnailSectionBlob != null) {
            if (other.thumbnailSectionBlob == null) return false
            if (!thumbnailSectionBlob.contentEquals(other.thumbnailSectionBlob)) return false
        } else if (other.thumbnailSectionBlob != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbnailMainBlob?.contentHashCode() ?: 0
        result = 31 * result + (thumbnailSectionBlob?.contentHashCode() ?: 0)
        return result
    }
}