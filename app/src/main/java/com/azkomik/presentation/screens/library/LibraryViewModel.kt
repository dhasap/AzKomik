package com.azkomik.presentation.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.MangaStatus
import com.azkomik.domain.model.library.*
import com.azkomik.domain.model.tracking.Track
import com.azkomik.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryUiState(
    val categories: List<LibraryCategory> = emptyList(),
    val selectedCategory: Long = 0,
    val mangaInCategories: Map<Long, List<Manga>> = emptyMap(),
    val sortType: LibrarySortType = LibrarySortType.ALPHABETICAL,
    val sortAscending: Boolean = true,
    val displayMode: LibraryDisplayMode = LibraryDisplayMode.COMFORTABLE_GRID,
    val filter: LibraryFilter = LibraryFilter(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCategoryTabs: Boolean = true,
    val columnsPortrait: Int = 3,
    val columnsLandscape: Int = 4
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadLibrary()
    }

    private fun loadLibrary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val categories = createMockCategories()
            val mangaMap = createMockMangaByCategory()
            
            _uiState.update {
                it.copy(
                    categories = categories,
                    mangaInCategories = mangaMap,
                    isLoading = false
                )
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        _uiState.update { it.copy(selectedCategory = categoryId) }
    }

    fun setSortType(type: LibrarySortType) {
        _uiState.update { it.copy(sortType = type) }
    }
    
    fun setSort(type: LibrarySortType) = setSortType(type)

    fun toggleSortDirection() {
        _uiState.update { it.copy(sortAscending = !it.sortAscending) }
    }

    fun setDisplayMode(mode: LibraryDisplayMode) {
        _uiState.update { it.copy(displayMode = mode) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFilterDownloaded() {
        _uiState.update { 
            it.copy(filter = it.filter.copy(downloaded = !it.filter.downloaded))
        }
    }

    fun toggleFilterUnread() {
        _uiState.update { 
            it.copy(filter = it.filter.copy(unread = !it.filter.unread))
        }
    }

    fun toggleFilterCompleted() {
        _uiState.update { 
            it.copy(filter = it.filter.copy(completed = !it.filter.completed))
        }
    }

    fun addCategory(name: String) {
        val newCategory = LibraryCategory(
            id = System.currentTimeMillis(),
            name = name,
            order = _uiState.value.categories.size
        )
        _uiState.update {
            it.copy(categories = it.categories + newCategory)
        }
    }

    fun deleteCategory(categoryId: Long) {
        _uiState.update {
            it.copy(
                categories = it.categories.filter { cat -> cat.id != categoryId },
                selectedCategory = if (it.selectedCategory == categoryId) 0 else it.selectedCategory
            )
        }
    }

    fun reorderCategories(fromIndex: Int, toIndex: Int) {
        val currentCategories = _uiState.value.categories.toMutableList()
        val item = currentCategories.removeAt(fromIndex)
        currentCategories.add(toIndex, item)
        
        _uiState.update {
            it.copy(
                categories = currentCategories.mapIndexed { index, cat -> 
                    cat.copy(order = index)
                }
            )
        }
    }

    fun getFilteredAndSortedManga(): List<Manga> {
        val state = _uiState.value
        val mangaList = state.mangaInCategories[state.selectedCategory] ?: emptyList()
        
        return mangaList
            .filter { manga ->
                // Apply filters
                (state.filter.downloaded.not() || manga.id.isNotBlank()) && // Would check download status
                (state.filter.unread.not() || manga.unreadCount > 0) &&
                (state.filter.completed.not() || manga.status == MangaStatus.COMPLETED) &&
                (state.searchQuery.isBlank() || 
                    manga.title.contains(state.searchQuery, ignoreCase = true))
            }
            .let { filtered ->
                // Apply sorting
                val sorted = when (state.sortType) {
                    LibrarySortType.ALPHABETICAL -> filtered.sortedBy { it.title }
                    LibrarySortType.LAST_READ -> filtered.sortedByDescending { it.lastUpdated }
                    LibrarySortType.LAST_MANGA_UPDATE -> filtered.sortedByDescending { it.lastUpdated }
                    LibrarySortType.UNREAD_COUNT -> filtered.sortedByDescending { it.unreadCount }
                    LibrarySortType.TOTAL_CHAPTERS -> filtered.sortedByDescending { it.rating }
                    LibrarySortType.LATEST_CHAPTER -> filtered.sortedByDescending { it.lastUpdated }
                    LibrarySortType.DATE_FETCHED -> filtered.sortedByDescending { it.lastUpdated }
                    LibrarySortType.DATE_ADDED -> filtered.sortedByDescending { it.lastUpdated }
                }
                if (state.sortAscending) sorted else sorted.reversed()
            }
    }

    private fun createMockCategories(): List<LibraryCategory> {
        return listOf(
            LibraryCategory(id = 0, name = "Default", order = 0, isDefault = true),
            LibraryCategory(id = 1, name = "Reading", order = 1),
            LibraryCategory(id = 2, name = "Completed", order = 2),
            LibraryCategory(id = 3, name = "On Hold", order = 3),
            LibraryCategory(id = 4, name = "Plan to Read", order = 4)
        )
    }

    private fun createMockMangaByCategory(): Map<Long, List<Manga>> {
        return mapOf(
            0L to listOf(
                Manga(
                    id = "1",
                    title = "One Piece",
                    coverUrl = "https://picsum.photos/seed/onepiece/400/600",
                    author = "Eiichiro Oda",
                    rating = 4.9f,
                    status = MangaStatus.ONGOING,
                    genres = listOf("Action", "Adventure"),
                    unreadCount = 3,
                    isFavorite = true
                ),
                Manga(
                    id = "2",
                    title = "Jujutsu Kaisen",
                    coverUrl = "https://picsum.photos/seed/jjk/400/600",
                    author = "Gege Akutami",
                    rating = 4.7f,
                    status = MangaStatus.ONGOING,
                    genres = listOf("Action", "Supernatural"),
                    unreadCount = 1,
                    isFavorite = true
                ),
                Manga(
                    id = "3",
                    title = "Solo Leveling",
                    coverUrl = "https://picsum.photos/seed/solo/400/600",
                    author = "Chu-Gong",
                    rating = 4.8f,
                    status = MangaStatus.COMPLETED,
                    genres = listOf("Action", "Fantasy"),
                    unreadCount = 0,
                    isFavorite = true
                ),
                Manga(
                    id = "4",
                    title = "Chainsaw Man",
                    coverUrl = "https://picsum.photos/seed/csm/400/600",
                    author = "Tatsuki Fujimoto",
                    rating = 4.8f,
                    status = MangaStatus.COMPLETED,
                    genres = listOf("Action", "Horror"),
                    unreadCount = 0,
                    isFavorite = true
                )
            ),
            1L to listOf(
                Manga(
                    id = "1",
                    title = "One Piece",
                    coverUrl = "https://picsum.photos/seed/onepiece/400/600",
                    author = "Eiichiro Oda",
                    rating = 4.9f,
                    status = MangaStatus.ONGOING,
                    unreadCount = 3,
                    isFavorite = true
                ),
                Manga(
                    id = "2",
                    title = "Jujutsu Kaisen",
                    coverUrl = "https://picsum.photos/seed/jjk/400/600",
                    author = "Gege Akutami",
                    rating = 4.7f,
                    status = MangaStatus.ONGOING,
                    unreadCount = 1,
                    isFavorite = true
                )
            ),
            2L to listOf(
                Manga(
                    id = "3",
                    title = "Solo Leveling",
                    coverUrl = "https://picsum.photos/seed/solo/400/600",
                    author = "Chu-Gong",
                    rating = 4.8f,
                    status = MangaStatus.COMPLETED,
                    unreadCount = 0,
                    isFavorite = true
                )
            ),
            3L to emptyList(),
            4L to emptyList()
        )
    }
}
