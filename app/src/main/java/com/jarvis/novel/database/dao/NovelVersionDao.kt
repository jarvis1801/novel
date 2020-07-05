package com.jarvis.novel.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jarvis.novel.data.NovelVersion

@Dao
interface NovelVersionDao {
    @Query("SELECT * FROM novelVersion")
    fun getList() : List<NovelVersion>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllNotReplace(novelVersionList: List<NovelVersion>)

    @Query("DELETE FROM novelVersion")
    fun deleteAll()
}