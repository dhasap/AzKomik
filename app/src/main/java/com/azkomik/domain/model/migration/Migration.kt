package com.azkomik.domain.model.migration

import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.source.Source

data class MigrationItem(
    val id: String,
    val manga: Manga,
    val currentSource: Source,
    val targetSource: Source? = null,
    val status: MigrationStatus = MigrationStatus.PENDING,
    val foundManga: List<Manga> = emptyList(),
    val selectedMatch: Manga? = null,
    val error: String? = null,
    val progress: Int = 0
)

enum class MigrationStatus {
    PENDING,
    SEARCHING,
    CHOOSING,
    CONFIRMING,
    MIGRATING,
    COMPLETED,
    ERROR,
    CANCELLED
}

data class MigrationPreferences(
    val useSmartSearch: Boolean = true,
    val extraSearchParams: Boolean = false,
    val migrateCategories: Boolean = true,
    val migrateChapters: Boolean = true,
    val migrateTracking: Boolean = true,
    val deleteDownloaded: Boolean = false
)

data class SourceMigration(
    val fromSource: Source,
    val toSource: Source,
    val mangaCount: Int = 0
)

object MigrationHelper {
    fun calculateMigrationConfidence(
        sourceManga: Manga,
        targetManga: Manga
    ): Float {
        var score = 0f
        
        // Exact title match
        if (sourceManga.title.equals(targetManga.title, ignoreCase = true)) {
            score += 0.5f
        } else if (sourceManga.title.contains(targetManga.title, ignoreCase = true) ||
                   targetManga.title.contains(sourceManga.title, ignoreCase = true)) {
            score += 0.3f
        }
        
        // Author match
        if (sourceManga.author.isNotBlank() && 
            sourceManga.author.equals(targetManga.author, ignoreCase = true)) {
            score += 0.3f
        }
        
        // Status match
        if (sourceManga.status == targetManga.status) {
            score += 0.1f
        }
        
        // Genre overlap
        val genreOverlap = sourceManga.genres.intersect(targetManga.genres.toSet()).size
        score += (genreOverlap.toFloat() / maxOf(sourceManga.genres.size, targetManga.genres.size, 1)) * 0.1f
        
        return score.coerceIn(0f, 1f)
    }
}
