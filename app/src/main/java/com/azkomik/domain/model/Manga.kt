package com.azkomik.domain.model

enum class MangaStatus {
    ONGOING, COMPLETED, HIATUS, CANCELLED, UNKNOWN
}

data class Manga(
    val id: String,
    val title: String,
    val coverUrl: String,
    val author: String,
    val artist: String? = null,
    val description: String = "",
    val status: MangaStatus = MangaStatus.UNKNOWN,
    val genres: List<String> = emptyList(),
    val rating: Float = 0f,
    val sourceId: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val favoriteDate: Long? = null,
    val unreadCount: Int = 0,
    val isHot: Boolean = false,
    val isNew: Boolean = false
)

data class Chapter(
    val id: String,
    val mangaId: String,
    val number: Float,
    val title: String,
    val pageCount: Int = 0,
    val dateUpload: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isDownloaded: Boolean = false,
    val isBookmarked: Boolean = false,
    val lastPageRead: Int = 0,
    val sourceName: String = ""
)

data class MangaSource(
    val id: String,
    val name: String,
    val iconUrl: String? = null,
    val lang: String = "en",
    val isEnabled: Boolean = true
)

data class User(
    val id: String = "",
    val username: String = "OtakuReader",
    val avatarUrl: String? = null,
    val level: Int = 42,
    val chaptersRead: Int = 1200,
    val streak: Int = 245,
    val readingHours: Int = 342,
    val memberStatus: String = "Free Member"
)

data class UserStats(
    val totalMangaRead: Int = 0,
    val totalChaptersRead: Int = 0,
    val totalReadingTime: Int = 0
)

data class ReadingHistory(
    val mangaId: String,
    val chapterId: String,
    val lastRead: Long,
    val progress: Int
)

data class Page(
    val index: Int,
    val imageUrl: String
)

data class LatestManga(
    val manga: Manga,
    val chapter: Chapter,
    val sourceIconUrl: String? = null,
    val sourceName: String = ""
)
