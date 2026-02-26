package com.azkomik.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.azkomik.data.local.dao.ChapterDao
import com.azkomik.data.local.dao.MangaDao
import com.azkomik.data.local.entity.ChapterEntity
import com.azkomik.data.local.entity.MangaEntity
import com.azkomik.data.local.entity.ReadingHistoryEntity

@Database(
    entities = [MangaEntity::class, ChapterEntity::class, ReadingHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao
}
