package com.azkomik.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga")
data class MangaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val coverUrl: String,
    val author: String,
    val artist: String?,
    val description: String,
    val status: String,
    val genres: String,
    val rating: Float,
    val sourceId: String,
    val lastUpdated: Long,
    val isFavorite: Boolean,
    val favoriteDate: Long?,
    val unreadCount: Int
)

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val mangaId: String,
    val number: Float,
    val title: String,
    val pageCount: Int,
    val dateUpload: Long,
    val isRead: Boolean,
    val isDownloaded: Boolean,
    val isBookmarked: Boolean,
    val lastPageRead: Int
)

@Entity(tableName = "reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey val mangaId: String,
    val chapterId: String,
    val lastRead: Long,
    val progress: Int
)
