package com.azkomik.presentation.screens.updates

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

data class UpdatesUiState(
    val recentUpdates: List<Pair<Manga, Chapter>> = emptyList(),
    val filteredUpdates: List<Pair<Manga, Chapter>> = emptyList(),
    val selectedFilter: UpdateFilter = UpdateFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class UpdateFilter {
    ALL, FOLLOWING, UNREAD
}

@HiltViewModel
class UpdatesViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdatesUiState())
    val uiState: StateFlow<UpdatesUiState> = _uiState.asStateFlow()

    init {
        loadUpdates()
    }

    private fun loadUpdates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val mockUpdates = createMockUpdates()
            _uiState.update {
                it.copy(
                    recentUpdates = mockUpdates,
                    filteredUpdates = mockUpdates,
                    isLoading = false
                )
            }
        }
    }

    fun setFilter(filter: UpdateFilter) {
        _uiState.update { state ->
            val filtered = when (filter) {
                UpdateFilter.ALL -> state.recentUpdates
                UpdateFilter.FOLLOWING -> state.recentUpdates.filter { it.first.isFavorite }
                UpdateFilter.UNREAD -> state.recentUpdates.filter { !it.second.isRead }
            }
            state.copy(
                selectedFilter = filter,
                filteredUpdates = filtered
            )
        }
    }

    fun markAsRead(mangaId: String, chapterId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                val updatedUpdates = state.recentUpdates.map { (manga, chapter) ->
                    if (chapter.id == chapterId) {
                        manga to chapter.copy(isRead = true)
                    } else {
                        manga to chapter
                    }
                }
                state.copy(
                    recentUpdates = updatedUpdates,
                    filteredUpdates = applyFilter(updatedUpdates, state.selectedFilter)
                )
            }
        }
    }

    fun downloadChapter(chapterId: String) {
        // Implementation for downloading chapter
    }

    private fun applyFilter(
        updates: List<Pair<Manga, Chapter>>,
        filter: UpdateFilter
    ): List<Pair<Manga, Chapter>> {
        return when (filter) {
            UpdateFilter.ALL -> updates
            UpdateFilter.FOLLOWING -> updates.filter { it.first.isFavorite }
            UpdateFilter.UNREAD -> updates.filter { !it.second.isRead }
        }
    }

    private fun createMockUpdates(): List<Pair<Manga, Chapter>> {
        val mangaList = listOf(
            Manga(
                id = "upd_1",
                title = "Solo Leveling",
                coverUrl = "https://picsum.photos/seed/solo/400/600",
                author = "Chu-Gong",
                status = MangaStatus.ONGOING,
                isFavorite = true
            ),
            Manga(
                id = "upd_2",
                title = "One Piece",
                coverUrl = "https://picsum.photos/seed/onepiece/400/600",
                author = "Eiichiro Oda",
                status = MangaStatus.ONGOING,
                isFavorite = true
            ),
            Manga(
                id = "upd_3",
                title = "Chainsaw Man",
                coverUrl = "https://picsum.photos/seed/csm/400/600",
                author = "Tatsuki Fujimoto",
                status = MangaStatus.COMPLETED,
                isFavorite = false
            ),
            Manga(
                id = "upd_4",
                title = "Jujutsu Kaisen",
                coverUrl = "https://picsum.photos/seed/jjk/400/600",
                author = "Gege Akutami",
                status = MangaStatus.ONGOING,
                isFavorite = true
            ),
            Manga(
                id = "upd_5",
                title = "Blue Lock",
                coverUrl = "https://picsum.photos/seed/bluelock/400/600",
                author = "Muneyuki Kaneshiro",
                status = MangaStatus.ONGOING,
                isFavorite = false
            )
        )

        return mangaList.mapIndexed { index, manga ->
            val chapter = Chapter(
                id = "chapter_upd_$index",
                mangaId = manga.id,
                number = (189 - index * 20).toFloat(),
                title = "The Beginning",
                dateUpload = System.currentTimeMillis() - (index * 900000L), // 15 minutes apart
                isRead = index >= 3 // First 3 are unread
            )
            Pair(manga, chapter)
        }
    }
}
