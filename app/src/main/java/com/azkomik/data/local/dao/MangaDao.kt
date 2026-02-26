package com.azkomik.data.local.dao

import androidx.room.*
import com.azkomik.data.local.entity.MangaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga WHERE isFavorite = 1 ORDER BY favoriteDate DESC")
    fun getFavoriteManga(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE id = :mangaId")
    suspend fun getMangaById(mangaId: String): MangaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(manga: MangaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllManga(mangaList: List<MangaEntity>)

    @Query("UPDATE manga SET isFavorite = :isFavorite, favoriteDate = :date WHERE id = :mangaId")
    suspend fun updateFavoriteStatus(mangaId: String, isFavorite: Boolean, date: Long?)

    @Query("SELECT * FROM manga ORDER BY lastUpdated DESC LIMIT 20")
    fun getRecentManga(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE title LIKE '%' || :query || '%'")
    suspend fun searchManga(query: String): List<MangaEntity>

    @Delete
    suspend fun deleteManga(manga: MangaEntity)
}
