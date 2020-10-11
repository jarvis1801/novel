package com.jarvis.novel.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jarvis.novel.data.Manga

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga ORDER BY `index` ASC")
    fun getAll(): LiveData<List<Manga>?>

    @Query("SELECT * FROM manga WHERE _id IN (:mangaIds)")
    fun loadAllByIds(mangaIds: IntArray): List<Manga>

    @Query("SELECT * FROM manga WHERE _id = :id")
    fun findById(id: String): LiveData<Manga>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllNotReplace(mangaList: List<Manga>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReplace(mangaList: List<Manga>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOneReplace(manga: Manga)

    @Delete
    fun delete(manga: Manga)

    @Query("DELETE FROM manga")
    fun deleteAll()
}