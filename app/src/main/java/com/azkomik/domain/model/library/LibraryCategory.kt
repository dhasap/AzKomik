package com.azkomik.domain.model.library

import androidx.compose.ui.graphics.Color

data class LibraryCategory(
    val id: Long = 0,
    val name: String,
    val order: Int = 0,
    val isDefault: Boolean = false,
    val mangaIds: List<String> = emptyList()
)

enum class LibrarySortType {
    ALPHABETICAL,
    LAST_READ,
    LAST_MANGA_UPDATE,
    UNREAD_COUNT,
    TOTAL_CHAPTERS,
    LATEST_CHAPTER,
    DATE_FETCHED,
    DATE_ADDED
}

enum class LibraryDisplayMode {
    COMPACT_GRID,
    COMFORTABLE_GRID,
    LIST
}

data class LibraryFilter(
    val downloaded: Boolean = false,
    val unread: Boolean = false,
    val started: Boolean = false,
    val bookmarked: Boolean = false,
    val completed: Boolean = false,
    val tracking: Set<Int> = emptySet() // Tracking service IDs
)

data class LibraryPreferences(
    val sortType: LibrarySortType = LibrarySortType.ALPHABETICAL,
    val sortAscending: Boolean = true,
    val displayMode: LibraryDisplayMode = LibraryDisplayMode.COMFORTABLE_GRID,
    val columnsPortrait: Int = 3,
    val columnsLandscape: Int = 4,
    val filterDownloaded: Boolean = false,
    val filterUnread: Boolean = false,
    val filterCompleted: Boolean = false,
    val filterTracked: Boolean = false,
    val showUnreadCount: Boolean = true,
    val showDownloadBadge: Boolean = true,
    val showLanguageFlag: Boolean = true,
    val showCount: Boolean = true,
    val showLastReadTimestamp: Boolean = true
)

data class LibraryManga(
    val mangaId: String,
    val categoryId: Long = 0,
    val dateAdded: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val lastRead: Long = 0,
    val lastUpdate: Long = 0
)
