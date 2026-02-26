package com.azkomik.domain.model.backup

data class Backup(
    val backupManga: List<BackupManga> = emptyList(),
    val backupCategories: List<BackupCategory> = emptyList(),
    val backupSources: List<BackupSource> = emptyList(),
    val backupTracks: List<BackupTracking> = emptyList(),
    val backupHistory: List<BackupHistory> = emptyList(),
    val backupPreferences: BackupPreferences? = null,
    val backupDate: Long = System.currentTimeMillis(),
    val backupVersion: String = "1.0"
)

data class BackupManga(
    val source: Long,
    val url: String,
    val title: String,
    val artist: String? = null,
    val author: String? = null,
    val description: String? = null,
    val genre: List<String> = emptyList(),
    val status: Int = 0,
    val thumbnailUrl: String? = null,
    val favorite: Boolean = true,
    val chapterFlags: Int = 0,
    val viewerFlags: Int = 0,
    val dateAdded: Long = 0,
    val categories: List<Long> = emptyList(),
    val chapters: List<BackupChapter> = emptyList(),
    val tracks: List<BackupTracking> = emptyList(),
    val history: List<BackupHistory> = emptyList()
)

data class BackupChapter(
    val url: String,
    val name: String,
    val scanlator: String? = null,
    val read: Boolean = false,
    val bookmark: Boolean = false,
    val lastPageRead: Int = 0,
    val dateFetch: Long = 0,
    val dateUpload: Long = 0,
    val chapterNumber: Float = 0f,
    val sourceOrder: Int = 0
)

data class BackupCategory(
    val name: String,
    val order: Int = 0
)

data class BackupSource(
    val name: String,
    val sourceId: Long
)

data class BackupTracking(
    val syncId: Int,
    val libraryId: Long,
    val mediaId: Long,
    val title: String = "",
    val lastChapterRead: Float = 0f,
    val totalChapters: Int = 0,
    val status: Int = 0,
    val score: Double = 0.0,
    val remoteUrl: String = "",
    val startDate: Long = 0,
    val finishDate: Long = 0
)

data class BackupHistory(
    val url: String,
    val lastRead: Long = 0,
    val readDuration: Long = 0
)

data class BackupPreferences(
    val readerSettings: Map<String, Any> = emptyMap(),
    val downloadSettings: Map<String, Any> = emptyMap(),
    val librarySettings: Map<String, Any> = emptyMap(),
    val trackingSettings: Map<String, Any> = emptyMap()
)

data class BackupSchedule(
    val enabled: Boolean = false,
    val frequencyDays: Int = 1,
    val maxBackups: Int = 5,
    val lastBackup: Long? = null,
    val autoBackupLocation: String = ""
)

data class AutoBackupResult(
    val success: Boolean,
    val uri: String? = null,
    val error: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
