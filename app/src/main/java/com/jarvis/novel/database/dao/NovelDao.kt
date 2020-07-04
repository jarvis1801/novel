package com.jarvis.novel.database.dao

import androidx.room.*
import com.jarvis.novel.data.Novel

@Dao
interface NovelDao {
    @Query("SELECT * FROM novel")
    fun getAll(): List<Novel>

    @Query("SELECT * FROM novel WHERE _id IN (:novelIds)")
    fun loadAllByIds(novelIds: IntArray): List<Novel>

    @Query("SELECT * FROM novel WHERE _id = :id")
    fun findById(id: String): Novel

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllNotReplace(novels: List<Novel>)

    @Delete
    fun delete(novel: Novel)

    @Query("DELETE FROM novel")
    fun deleteAll();
}