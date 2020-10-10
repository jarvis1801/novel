package com.jarvis.novel.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jarvis.novel.data.*
import com.jarvis.novel.database.dao.*

@Database(entities = [Novel::class, Volume::class, NovelVersion::class, Manga::class, MangaVolume::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun novelDao(): NovelDao
    abstract fun novelVolumeDao(): VolumeDao
    abstract fun novelVersion(): NovelVersionDao
    abstract fun mangaDao(): MangaDao
    abstract fun mangaVolumeDao(): MangaVolumeDao
}