package com.azkomik.domain.model.download

data class Download(
    val mangaId: String,
    val chapterId: String,
    val mangaTitle: String,
    val chapterName: String,
    val sourceId: Long,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val progress: Int = 0,
    val totalPages: Int = 0,
    val downloadedPages: Int = 0,
    val error: String? = null,
    val queuePosition: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    ERROR,
    CANCELLED
}

data class DownloadPreferences(
    val onlyOverWifi: Boolean = true,
    val saveChaptersAsCbz: Boolean = false,
    val splitPagesByMeta: Boolean = false,
    val autoDownloadNewChapters: Boolean = false,
    val autoDownloadWhileReading: Boolean = false,
    val deleteChaptersAfterReading: Boolean = false,
    val downloadAhead: Int = 0, // 0 = disabled, 1+ = number of chapters
    val downloadNewChaptersForAll: Boolean = false
)

data class DownloadQueue(
    val items: List<Download> = emptyList(),
    val isRunning: Boolean = false,
    val paused: Boolean = false
)

data class DownloadedChapter(
    val chapterId: String,
    val mangaId: String,
    val mangaTitle: String,
    val chapterNumber: Float,
    val chapterName: String,
    val sourceId: Long,
    val downloadedAt: Long = System.currentTimeMillis(),
    val pages: List<String> = emptyList()
)
