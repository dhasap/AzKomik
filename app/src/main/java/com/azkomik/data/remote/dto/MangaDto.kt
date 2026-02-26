package com.azkomik.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MangaListResponse(
    val data: List<MangaDto>,
    val hasNextPage: Boolean
)

@Serializable
data class MangaDto(
    val id: String,
    val title: String,
    val cover: String,
    val author: String,
    val artist: String? = null,
    val description: String = "",
    val status: String = "unknown",
    val genres: List<String> = emptyList(),
    val rating: Float = 0f,
    val sourceId: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Serializable
data class MangaDetailResponse(
    val manga: MangaDto,
    val chapters: List<ChapterDto>
)

@Serializable
data class ChapterListResponse(
    val data: List<ChapterDto>
)

@Serializable
data class ChapterDto(
    val id: String,
    val mangaId: String,
    val number: Float,
    val title: String,
    val pageCount: Int = 0,
    val dateUpload: Long = System.currentTimeMillis()
)

@Serializable
data class PageListResponse(
    val pages: List<PageDto>
)

@Serializable
data class PageDto(
    val index: Int,
    val imageUrl: String
)
