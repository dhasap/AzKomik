package com.azkomik.domain.repository

import com.azkomik.domain.model.Chapter
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.Page
import kotlinx.coroutines.flow.Flow

interface MangaRepository {
    // Local
    fun getFavoriteManga(): Flow<List<Manga>>
    fun getRecentManga(): Flow<List<Manga>>
    suspend fun getMangaById(mangaId: String): Manga?
    suspend fun toggleFavorite(mangaId: String)

    // Remote
    suspend fun getPopularManga(page: Int = 1): Result<List<Manga>>
    suspend fun searchManga(query: String, page: Int = 1): Result<List<Manga>>
    suspend fun getMangaDetail(mangaId: String): Result<Pair<Manga, List<Chapter>>>
    suspend fun getChapterPages(chapterId: String): Result<List<Page>>
    suspend fun getLatestUpdates(page: Int = 1): Result<List<Manga>>

    // Chapters
    fun getChaptersByMangaId(mangaId: String): Flow<List<Chapter>>
    suspend fun markChapterAsRead(chapterId: String, page: Int)
    suspend fun downloadChapter(chapterId: String)
}
