package com.azkomik.domain.model.tracking

data class TrackingService(
    val id: Int,
    val name: String,
    val iconUrl: String? = null,
    val isLoggedIn: Boolean = false,
    val supportsReadingProgress: Boolean = true,
    val supportsScore: Boolean = true,
    val supportsStartDate: Boolean = true,
    val supportsFinishDate: Boolean = true,
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10
)

enum class ScoreFormat {
    POINT_100,
    POINT_10,
    POINT_10_DECIMAL,
    POINT_5,
    POINT_3,
    SMILEY
}

data class TrackSearchResult(
    val trackingId: Int,
    val remoteId: Long,
    val title: String,
    val totalChapters: Int = 0,
    val publishingStatus: String = "",
    val publishingType: String = "",
    val startDate: String = "",
    val coverUrl: String? = null,
    val summary: String = ""
)

data class Track(
    val id: Long? = null,
    val mangaId: String,
    val syncId: Int,
    val remoteId: Long,
    val libraryId: Long? = null,
    val title: String,
    val lastChapterRead: Float = 0f,
    val totalChapters: Int = 0,
    val status: Int = TrackStatus.READING,
    val score: Double = 0.0,
    val remoteUrl: String = "",
    val startDate: Long = 0,
    val finishDate: Long = 0
)

object TrackStatus {
    const val READING = 1
    const val REPEATING = 2
    const val COMPLETED = 3
    const val ON_HOLD = 4
    const val DROPPED = 5
    const val PLAN_TO_READ = 6
    
    fun toString(status: Int): String = when (status) {
        READING -> "Reading"
        REPEATING -> "Re-reading"
        COMPLETED -> "Completed"
        ON_HOLD -> "On Hold"
        DROPPED -> "Dropped"
        PLAN_TO_READ -> "Plan to read"
        else -> "Unknown"
    }
}

object TrackingServices {
    const val MYANIMELIST = 1
    const val ANILIST = 2
    const val KITSU = 3
    const val MANGAUPDATES = 4
    const val SHIKIMORI = 5
    const val BANGUMI = 6
    const val KOMGA = 7
}

data class TrackerPreferences(
    val autoTrack: Boolean = true,
    val autoUpdateProgress: Boolean = true,
    val updateChaptersRead: Boolean = true,
    val updateStatus: Boolean = true,
    val updateScore: Boolean = false
)
