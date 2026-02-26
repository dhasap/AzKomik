package com.azkomik.presentation.screens.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.Page
import com.azkomik.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReaderUiState(
    val pages: List<Page> = emptyList(),
    val currentPage: Int = 0,
    val zoomScale: Float = 1f,
    val mangaTitle: String = "",
    val chapterNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    fun loadChapter(chapterId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Mock data - in real app, fetch from repository
            val mockPages = createMockPages()
            
            _uiState.update {
                it.copy(
                    pages = mockPages,
                    mangaTitle = "Solo Leveling",
                    chapterNumber = "102",
                    currentPage = 11, // Start at page 12 (0-indexed)
                    isLoading = false
                )
            }
        }
    }

    fun setCurrentPage(page: Int) {
        _uiState.update { 
            it.copy(currentPage = page.coerceIn(0, it.pages.size - 1)) 
        }
    }

    fun setZoomScale(scale: Float) {
        _uiState.update { it.copy(zoomScale = scale) }
    }

    fun goToPreviousPage() {
        _uiState.update { state ->
            val newPage = (state.currentPage - 1).coerceAtLeast(0)
            state.copy(currentPage = newPage)
        }
    }

    fun goToNextPage() {
        _uiState.update { state ->
            val newPage = (state.currentPage + 1).coerceAtMost(state.pages.size - 1)
            state.copy(currentPage = newPage)
        }
    }

    fun goToPage(pageIndex: Int) {
        _uiState.update { state ->
            state.copy(currentPage = pageIndex.coerceIn(0, state.pages.size - 1))
        }
    }

    private fun createMockPages(): List<Page> {
        return (1..45).map { index ->
            Page(
                index = index - 1,
                imageUrl = "https://picsum.photos/seed/chapter102_page${index}/800/1200"
            )
        }
    }
}
