package com.jarvis.novel.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jarvis.novel.data.MangaVolume

@Dao
interface MangaVolumeDao {

    @Query("SELECT * FROM mangaVolume WHERE manga = :id")
    fun findById(id: String): LiveData<List<MangaVolume>?>

    @Query("SELECT * FROM mangaVolume WHERE _id = :id")
    fun findOneById(id: String): LiveData<MangaVolume?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllNotReplace(mangaVolume: List<MangaVolume>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReplace(mangaVolume: List<MangaVolume>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOneReplace(mangaVolume: MangaVolume)

    @Query("DELETE FROM mangaVolume")
    fun deleteAll()
}