package com.azkomik.data.repository

import com.azkomik.data.local.dao.ChapterDao
import com.azkomik.data.local.dao.MangaDao
import com.azkomik.data.local.entity.ChapterEntity
import com.azkomik.data.local.entity.MangaEntity
import com.azkomik.data.remote.api.MangaApiService
import com.azkomik.domain.model.Chapter
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.MangaStatus
import com.azkomik.domain.model.Page
import com.azkomik.domain.repository.MangaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRepositoryImpl @Inject constructor(
    private val mangaDao: MangaDao,
    private val chapterDao: ChapterDao,
    private val apiService: MangaApiService
) : MangaRepository {

    override fun getFavoriteManga(): Flow<List<Manga>> {
        return mangaDao.getFavoriteManga().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getRecentManga(): Flow<List<Manga>> {
        return mangaDao.getRecentManga().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getMangaById(mangaId: String): Manga? {
        return mangaDao.getMangaById(mangaId)?.toDomain()
    }

    override suspend fun toggleFavorite(mangaId: String) {
        val manga = mangaDao.getMangaById(mangaId)
        manga?.let {
            val newFavoriteStatus = !it.isFavorite
            val date = if (newFavoriteStatus) System.currentTimeMillis() else null
            mangaDao.updateFavoriteStatus(mangaId, newFavoriteStatus, date)
        }
    }

    override suspend fun getPopularManga(page: Int): Result<List<Manga>> {
        return try {
            val response = apiService.getPopularManga(page)
            if (response.isSuccessful) {
                val mangaList = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(mangaList)
            } else {
                Result.failure(Exception("Failed to fetch popular manga"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchManga(query: String, page: Int): Result<List<Manga>> {
        return try {
            val response = apiService.searchManga(query, page)
            if (response.isSuccessful) {
                val mangaList = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(mangaList)
            } else {
                Result.failure(Exception("Failed to search manga"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMangaDetail(mangaId: String): Result<Pair<Manga, List<Chapter>>> {
        return try {
            val response = apiService.getMangaDetail(mangaId)
            if (response.isSuccessful) {
                val body = response.body()
                val manga = body?.manga?.toDomain()
                val chapters = body?.chapters?.map { it.toDomain() } ?: emptyList()

                if (manga != null) {
                    mangaDao.insertManga(manga.toEntity())
                    chapterDao.insertChapters(chapters.map { it.toEntity() })
                    Result.success(Pair(manga, chapters))
                } else {
                    Result.failure(Exception("Manga not found"))
                }
            } else {
                Result.failure(Exception("Failed to fetch manga detail"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChapterPages(chapterId: String): Result<List<Page>> {
        return try {
            val response = apiService.getChapterPages(chapterId)
            if (response.isSuccessful) {
                val pages = response.body()?.pages?.map {
                    Page(index = it.index, imageUrl = it.imageUrl)
                } ?: emptyList()
                Result.success(pages)
            } else {
                Result.failure(Exception("Failed to fetch chapter pages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLatestUpdates(page: Int): Result<List<Manga>> {
        return try {
            val response = apiService.getLatestUpdates(page)
            if (response.isSuccessful) {
                val mangaList = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(mangaList)
            } else {
                Result.failure(Exception("Failed to fetch latest updates"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getChaptersByMangaId(mangaId: String): Flow<List<Chapter>> {
        return chapterDao.getChaptersByMangaId(mangaId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun markChapterAsRead(chapterId: String, page: Int) {
        chapterDao.markAsRead(chapterId, page)
    }

    override suspend fun downloadChapter(chapterId: String) {
        val chapter = chapterDao.getChapterById(chapterId)
        chapter?.let {
            chapterDao.updateDownloadStatus(chapterId, !it.isDownloaded)
        }
    }

    private fun MangaEntity.toDomain() = Manga(
        id = id,
        title = title,
        coverUrl = coverUrl,
        author = author,
        artist = artist,
        description = description,
        status = MangaStatus.valueOf(status.uppercase()),
        genres = genres.split(",").filter { it.isNotBlank() }.map { it.trim() },
        rating = rating,
        sourceId = sourceId,
        lastUpdated = lastUpdated,
        isFavorite = isFavorite,
        favoriteDate = favoriteDate,
        unreadCount = unreadCount
    )

    private fun Manga.toEntity() = MangaEntity(
        id = id,
        title = title,
        coverUrl = coverUrl,
        author = author,
        artist = artist,
        description = description,
        status = status.name.lowercase(),
        genres = genres.joinToString(","),
        rating = rating,
        sourceId = sourceId,
        lastUpdated = lastUpdated,
        isFavorite = isFavorite,
        favoriteDate = favoriteDate,
        unreadCount = unreadCount
    )

    private fun com.azkomik.data.remote.dto.MangaDto.toDomain() = Manga(
        id = id,
        title = title,
        coverUrl = cover,
        author = author,
        artist = artist,
        description = description,
        status = runCatching { MangaStatus.valueOf(status.uppercase()) }.getOrDefault(MangaStatus.UNKNOWN),
        genres = genres,
        rating = rating,
        sourceId = sourceId,
        lastUpdated = lastUpdated
    )

    private fun ChapterEntity.toDomain() = Chapter(
        id = id,
        mangaId = mangaId,
        number = number,
        title = title,
        pageCount = pageCount,
        dateUpload = dateUpload,
        isRead = isRead,
        isDownloaded = isDownloaded,
        isBookmarked = isBookmarked,
        lastPageRead = lastPageRead
    )

    private fun Chapter.toEntity() = ChapterEntity(
        id = id,
        mangaId = mangaId,
        number = number,
        title = title,
        pageCount = pageCount,
        dateUpload = dateUpload,
        isRead = isRead,
        isDownloaded = isDownloaded,
        isBookmarked = isBookmarked,
        lastPageRead = lastPageRead
    )

    private fun com.azkomik.data.remote.dto.ChapterDto.toDomain() = Chapter(
        id = id,
        mangaId = mangaId,
        number = number,
        title = title,
        pageCount = pageCount,
        dateUpload = dateUpload
    )
}
