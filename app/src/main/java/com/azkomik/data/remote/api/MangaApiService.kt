package com.azkomik.data.remote.api

import com.azkomik.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MangaApiService {
    @GET("manga/popular")
    suspend fun getPopularManga(
        @Query("page") page: Int = 1
    ): Response<MangaListResponse>

    @GET("manga/search")
    suspend fun searchManga(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Response<MangaListResponse>

    @GET("manga/{id}")
    suspend fun getMangaDetail(
        @Path("id") mangaId: String
    ): Response<MangaDetailResponse>

    @GET("manga/{id}/chapters")
    suspend fun getChapters(
        @Path("id") mangaId: String
    ): Response<ChapterListResponse>

    @GET("chapter/{id}/pages")
    suspend fun getChapterPages(
        @Path("id") chapterId: String
    ): Response<PageListResponse>

    @GET("manga/latest")
    suspend fun getLatestUpdates(
        @Query("page") page: Int = 1
    ): Response<MangaListResponse>
}
