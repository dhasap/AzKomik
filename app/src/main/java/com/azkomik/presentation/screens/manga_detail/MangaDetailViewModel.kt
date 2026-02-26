package com.azkomik.presentation.screens.manga_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.Chapter
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.MangaStatus
import com.azkomik.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MangaDetailUiState(
    val manga: Manga? = null,
    val chapters: List<Chapter> = emptyList(),
    val isFavorite: Boolean = false,
    val isBookmarked: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val selectedTab: DetailTab = DetailTab.CHAPTERS,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class DetailTab {
    CHAPTERS, INFO, COMMENTS
}

@HiltViewModel
class MangaDetailViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MangaDetailUiState())
    val uiState: StateFlow<MangaDetailUiState> = _uiState.asStateFlow()

    fun loadMangaDetail(mangaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Mock data - in real app, fetch from repository
            val manga = createMockManga(mangaId)
            val chapters = createMockChapters(mangaId)
            
            _uiState.update {
                it.copy(
                    manga = manga,
                    chapters = chapters,
                    isLoading = false
                )
            }
        }
    }

    fun toggleFavorite() {
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
    }

    fun toggleBookmark() {
        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
    }

    fun toggleNotification() {
        _uiState.update { it.copy(isNotificationEnabled = !it.isNotificationEnabled) }
    }

    fun selectTab(tab: DetailTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun downloadChapter(chapterId: String) {
        // Implementation for downloading chapter
    }

    private fun createMockManga(mangaId: String): Manga {
        return Manga(
            id = mangaId,
            title = "Solo Leveling",
            coverUrl = "https://picsum.photos/seed/solo/400/600",
            author = "Chu-Gong",
            artist = "Dubu (Redice Studio)",
            description = "10 years ago, after \"the Gate\" that connected the real world with the monster world opened, some of the ordinary, everyday people received the power to hunt monsters within the Gate. They are known as \"Hunters\". However, not all Hunters are powerful. My name is Sung Jin-Woo, an E-rank Hunter. I'm someone who has to risk his life in the lowliest of dungeons, the \"World's Weakest\". Having no skills whatsoever to display, I barely earned the required money by fighting in low-leveled dungeons... at least until I found a hidden dungeon with the hardest difficulty within the D-rank dungeons!",
            status = MangaStatus.ONGOING,
            genres = listOf("Action", "Fantasy", "Supernatural", "Adventure"),
            rating = 4.8f,
            lastUpdated = System.currentTimeMillis() - 7200000 // 2 hours ago
        )
    }

    private fun createMockChapters(mangaId: String): List<Chapter> {
        return (179 downTo 150).mapIndexed { index, number ->
            Chapter(
                id = "${mangaId}_ch_$number",
                mangaId = mangaId,
                number = number.toFloat(),
                title = when (number) {
                    179 -> "The Final Battle - Part 2"
                    178 -> "The Final Battle - Part 1"
                    177 -> "Preparing for War"
                    176 -> "Prologue"
                    175 -> "Aftermath"
                    174 -> "Dragon King's Fury"
                    else -> "Chapter $number"
                },
                dateUpload = System.currentTimeMillis() - (index * 86400000L), // Days apart
                isRead = index >= 3,
                pageCount = 45
            )
        }
    }
}
