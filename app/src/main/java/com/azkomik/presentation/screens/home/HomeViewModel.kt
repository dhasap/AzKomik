package com.azkomik.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.MangaStatus
import com.azkomik.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val featuredManga: Manga? = null,
    val favorites: List<Manga> = emptyList(),
    val allManga: List<Manga> = emptyList(),
    val filteredManga: List<Manga> = emptyList(),
    val selectedFilter: String = "Semua",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val featured = createMockFeaturedManga()
            val mockFavorites = createMockFavorites()
            val mockAllManga = createMockAllManga()

            _uiState.update { state ->
                state.copy(
                    featuredManga = featured,
                    favorites = mockFavorites,
                    allManga = mockAllManga,
                    filteredManga = applyFilter(mockAllManga, state.selectedFilter),
                    isLoading = false
                )
            }
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredManga = applyFilter(state.allManga, filter)
            )
        }
    }

    private fun applyFilter(mangaList: List<Manga>, filter: String): List<Manga> {
        return when (filter) {
            "Belum Dibaca" -> mangaList.filter { it.unreadCount > 0 }
            "Sedang Baca" -> mangaList.filter { it.status == MangaStatus.ONGOING }
            "Selesai" -> mangaList.filter { it.status == MangaStatus.COMPLETED }
            else -> mangaList
        }
    }

    private fun createMockFeaturedManga() = Manga(
        id = "solo_leveling",
        title = "Solo Leveling",
        coverUrl = "https://picsum.photos/seed/solo/400/600",
        author = "Chu-Gong",
        description = "10 years ago, after \"the Gate\" that connected the real world with the monster world opened...",
        status = MangaStatus.ONGOING,
        genres = listOf("Fantasy", "Action", "Adventure"),
        rating = 4.8f,
        unreadCount = 5
    )

    private fun createMockFavorites() = listOf(
        Manga(
            id = "1",
            title = "One Piece",
            coverUrl = "https://picsum.photos/seed/onepiece/400/600",
            author = "Eiichiro Oda",
            rating = 4.9f,
            status = MangaStatus.ONGOING,
            genres = listOf("Action", "Adventure", "Comedy"),
            unreadCount = 3
        ),
        Manga(
            id = "2",
            title = "Jujutsu Kaisen",
            coverUrl = "https://picsum.photos/seed/jjk/400/600",
            author = "Gege Akutami",
            rating = 4.7f,
            status = MangaStatus.ONGOING,
            genres = listOf("Action", "Supernatural"),
            unreadCount = 1
        ),
        Manga(
            id = "3",
            title = "Chainsaw Man",
            coverUrl = "https://picsum.photos/seed/csm/400/600",
            author = "Tatsuki Fujimoto",
            rating = 4.8f,
            status = MangaStatus.COMPLETED,
            genres = listOf("Action", "Horror"),
            unreadCount = 0
        )
    )

    private fun createMockAllManga() = listOf(
        Manga(
            id = "4",
            title = "Spy x Family",
            coverUrl = "https://picsum.photos/seed/spyxfamily/400/600",
            author = "Tatsuya Endo",
            rating = 4.6f,
            status = MangaStatus.ONGOING,
            genres = listOf("Comedy", "Slice of Life")
        ),
        Manga(
            id = "5",
            title = "Tokyo Revengers",
            coverUrl = "https://picsum.photos/seed/tokyorev/400/600",
            author = "Ken Wakui",
            rating = 4.5f,
            status = MangaStatus.COMPLETED,
            genres = listOf("Action", "Drama", "Supernatural")
        ),
        Manga(
            id = "6",
            title = "Demon Slayer",
            coverUrl = "https://picsum.photos/seed/demonslayer/400/600",
            author = "Koyoharu Gotouge",
            rating = 4.9f,
            status = MangaStatus.COMPLETED,
            genres = listOf("Action", "Historical", "Supernatural")
        ),
        Manga(
            id = "7",
            title = "My Hero Academia",
            coverUrl = "https://picsum.photos/seed/mha/400/600",
            author = "Kohei Horikoshi",
            rating = 4.4f,
            status = MangaStatus.ONGOING,
            genres = listOf("Action", "Superhero")
        ),
        Manga(
            id = "8",
            title = "Black Clover",
            coverUrl = "https://picsum.photos/seed/blackclover/400/600",
            author = "Yuki Tabata",
            rating = 4.3f,
            status = MangaStatus.ONGOING,
            genres = listOf("Action", "Fantasy")
        ),
        Manga(
            id = "9",
            title = "Attack on Titan",
            coverUrl = "https://picsum.photos/seed/aot/400/600",
            author = "Hajime Isayama",
            rating = 4.9f,
            status = MangaStatus.COMPLETED,
            genres = listOf("Action", "Drama", "Fantasy")
        )
    )
}
