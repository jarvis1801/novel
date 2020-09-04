package com.jarvis.novel.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jarvis.novel.data.Volume

@Dao
interface VolumeDao {

    @Query("SELECT * FROM volume WHERE novelId = :id")
    fun findById(id: String): LiveData<List<Volume>?>

    @Query("SELECT * FROM volume WHERE _id = :id")
    fun findOneById(id: String): LiveData<Volume?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllNotReplace(volume: List<Volume>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReplace(volume: List<Volume>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOneReplace(volume: Volume)

    @Query("DELETE FROM volume")
    fun deleteAll()
}