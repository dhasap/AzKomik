package com.azkomik.data.local.dao

import androidx.room.*
import com.azkomik.data.local.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId ORDER BY number DESC")
    fun getChaptersByMangaId(mangaId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    suspend fun getChapterById(chapterId: String): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE isRead = 0 AND mangaId IN (SELECT id FROM manga WHERE isFavorite = 1)")
    fun getUnreadChapters(): Flow<List<ChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Query("UPDATE chapters SET isRead = 1, lastPageRead = :page WHERE id = :chapterId")
    suspend fun markAsRead(chapterId: String, page: Int)

    @Query("UPDATE chapters SET isDownloaded = :isDownloaded WHERE id = :chapterId")
    suspend fun updateDownloadStatus(chapterId: String, isDownloaded: Boolean)
}
