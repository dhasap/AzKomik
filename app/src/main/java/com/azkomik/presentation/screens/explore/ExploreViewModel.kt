package com.azkomik.presentation.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.LatestManga
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.MangaSource
import com.azkomik.domain.model.MangaStatus
import com.azkomik.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val searchQuery: String = "",
    val searchResults: List<Manga> = emptyList(),
    val popularManga: List<Manga> = emptyList(),
    val recommendedManga: List<Manga> = emptyList(),
    val genres: List<String> = emptyList(),
    val selectedGenre: String? = null,
    val sources: List<MangaSource> = emptyList(),
    val latestFromSources: List<LatestManga> = emptyList(),
    val popularGenres: List<String> = emptyList(),
    val selectedFilter: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val mockManga = createMockMangaList()
            val recommended = createMockRecommended()
            val sources = createMockSources()
            val latest = createMockLatestFromSources()
            
            _uiState.update {
                it.copy(
                    popularManga = mockManga,
                    searchResults = mockManga,
                    recommendedManga = recommended,
                    genres = listOf(
                        "Action", "Adventure", "Comedy", "Drama", "Fantasy",
                        "Horror", "Mystery", "Romance", "Sci-Fi", "Slice of Life",
                        "Sports", "Supernatural", "Thriller"
                    ),
                    popularGenres = listOf("Isekai", "Action", "Romance", "Manhwa", "Adventure", "Fantasy"),
                    sources = sources,
                    latestFromSources = latest,
                    isLoading = false
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isNotBlank() && query.length >= 2) {
            searchManga(query)
        } else if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = it.popularManga) }
        }
    }

    private fun searchManga(query: String) {
        viewModelScope.launch {
            val results = _uiState.value.popularManga.filter {
                it.title.contains(query, ignoreCase = true)
            }
            _uiState.update { it.copy(searchResults = results) }
        }
    }

    fun selectGenre(genre: String?) {
        _uiState.update {
            val newSelected = if (it.selectedGenre == genre) null else genre
            val filtered = if (newSelected != null) {
                it.popularManga.filter { manga ->
                    manga.genres.contains(newSelected)
                }
            } else {
                it.popularManga
            }
            it.copy(
                selectedGenre = newSelected,
                searchResults = filtered
            )
        }
    }

    fun selectPopularGenre(genre: String) {
        _uiState.update {
            it.copy(selectedGenre = genre)
        }
    }

    private fun createMockMangaList() = listOf(
        Manga(
            id = "explore_1",
            title = "Berserk",
            coverUrl = "https://picsum.photos/seed/berserk/400/600",
            author = "Kentaro Miura",
            rating = 4.9f,
            genres = listOf("Action", "Fantasy", "Horror")
        ),
        Manga(
            id = "explore_2",
            title = "Vinland Saga",
            coverUrl = "https://picsum.photos/seed/vinland/400/600",
            author = "Makoto Yukimura",
            rating = 4.8f,
            genres = listOf("Action", "Adventure", "Historical")
        ),
        Manga(
            id = "explore_3",
            title = "Kingdom",
            coverUrl = "https://picsum.photos/seed/kingdom/400/600",
            author = "Yasuhisa Hara",
            rating = 4.7f,
            genres = listOf("Action", "Historical")
        ),
        Manga(
            id = "explore_4",
            title = "Blue Lock",
            coverUrl = "https://picsum.photos/seed/bluelock/400/600",
            author = "Muneyuki Kaneshiro",
            rating = 4.6f,
            genres = listOf("Sports", "Drama")
        ),
        Manga(
            id = "explore_5",
            title = "Kaguya-sama: Love is War",
            coverUrl = "https://picsum.photos/seed/kaguya/400/600",
            author = "Aka Akasaka",
            rating = 4.8f,
            genres = listOf("Comedy", "Romance")
        ),
        Manga(
            id = "explore_6",
            title = "Spy x Family",
            coverUrl = "https://picsum.photos/seed/spyxfamily/400/600",
            author = "Tatsuya Endo",
            rating = 4.7f,
            genres = listOf("Comedy", "Action")
        )
    )

    private fun createMockRecommended() = listOf(
        Manga(
            id = "rec_1",
            title = "Solo Leveling",
            coverUrl = "https://picsum.photos/seed/solo/400/600",
            author = "Chu-Gong",
            rating = 4.9f,
            genres = listOf("Action", "Fantasy"),
            isHot = true
        ),
        Manga(
            id = "rec_2",
            title = "One Piece",
            coverUrl = "https://picsum.photos/seed/onepiece/400/600",
            author = "Eiichiro Oda",
            rating = 5.0f,
            genres = listOf("Adventure", "Comedy"),
            isNew = true
        ),
        Manga(
            id = "rec_3",
            title = "Mushoku Tensei",
            coverUrl = "https://picsum.photos/seed/mushoku/400/600",
            author = "Rifujin na Magonote",
            rating = 4.7f,
            genres = listOf("Isekai", "Magic")
        )
    )

    private fun createMockSources() = listOf(
        MangaSource("1", "Shinigami", "https://picsum.photos/seed/shinigami/100/100", isEnabled = true),
        MangaSource("2", "Komiku", "https://picsum.photos/seed/komiku/100/100", isEnabled = true),
        MangaSource("3", "Kiryuu", "https://picsum.photos/seed/kiryuu/100/100", isEnabled = true),
        MangaSource("4", "WestManga", "https://picsum.photos/seed/westmanga/100/100", isEnabled = true)
    )

    private fun createMockLatestFromSources(): List<LatestManga> {
        val sources = createMockSources()
        val mangaList = listOf(
            Manga(
                id = "latest_1",
                title = "Return of the Mount Hua Sect",
                coverUrl = "https://picsum.photos/seed/mounthua/400/600",
                author = "Biga"
            ),
            Manga(
                id = "latest_2",
                title = "The Beginning After The End",
                coverUrl = "https://picsum.photos/seed/tbate/400/600",
                author = "TurtleMe"
            ),
            Manga(
                id = "latest_3",
                title = "Omniscient Reader's Viewpoint",
                coverUrl = "https://picsum.photos/seed/orv/400/600",
                author = "Sing-Shong"
            )
        )
        
        return mangaList.mapIndexed { index, manga ->
            LatestManga(
                manga = manga,
                chapter = com.azkomik.domain.model.Chapter(
                    id = "ch_$index",
                    mangaId = manga.id,
                    number = (120 + index * 50).toFloat(),
                    title = "Chapter ${120 + index * 50}",
                    dateUpload = System.currentTimeMillis() - (index * 3600000L)
                ),
                sourceName = sources[index % sources.size].name,
                sourceIconUrl = sources[index % sources.size].iconUrl
            )
        }
    }
}
