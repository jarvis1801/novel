package com.jarvis.novel.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jarvis.novel.data.Novel
import com.jarvis.novel.data.NovelVersion
import com.jarvis.novel.data.Volume
import com.jarvis.novel.database.dao.NovelDao
import com.jarvis.novel.database.dao.NovelVersionDao
import com.jarvis.novel.database.dao.VolumeDao

@Database(entities = [Novel::class, Volume::class, NovelVersion::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): NovelDao
    abstract fun volumeDao(): VolumeDao
    abstract fun novelVersion(): NovelVersionDao
}