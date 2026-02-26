package com.azkomik.presentation.screens.migration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.migration.*
import com.azkomik.domain.model.source.Source
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MigrationUiState(
    val availableSources: List<Source> = emptyList(),
    val selectedSource: Source? = null,
    val targetSource: Source? = null,
    val mangaToMigrate: List<Manga> = emptyList(),
    val selectedManga: List<Manga> = emptyList(),
    val migrationItems: List<MigrationItem> = emptyList(),
    val currentStep: MigrationStep = MigrationStep.SELECT_SOURCE,
    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val preferences: MigrationPreferences = MigrationPreferences(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val completedCount: Int = 0,
    val totalCount: Int = 0
)

enum class MigrationStep {
    SELECT_SOURCE,
    SELECT_MANGA,
    SEARCH_TARGET,
    CONFIRM_MIGRATION,
    MIGRATING,
    COMPLETED
}

@HiltViewModel
class MigrationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MigrationUiState())
    val uiState: StateFlow<MigrationUiState> = _uiState.asStateFlow()

    init {
        loadSources()
    }

    private fun loadSources() {
        viewModelScope.launch {
            val sources = listOf(
                Source(
                    id = 1,
                    name = "Shinigami",
                    lang = "id",
                    iconUrl = "https://picsum.photos/seed/shinigami/100/100",
                    isEnabled = true,
                    mangaCount = 45
                ),
                Source(
                    id = 2,
                    name = "Komiku",
                    lang = "id",
                    iconUrl = "https://picsum.photos/seed/komiku/100/100",
                    isEnabled = true,
                    mangaCount = 38
                ),
                Source(
                    id = 3,
                    name = "MangaDex",
                    lang = "en",
                    iconUrl = "https://picsum.photos/seed/mangadex/100/100",
                    isEnabled = true,
                    mangaCount = 1200
                ),
                Source(
                    id = 4,
                    name = "MangaKakalot",
                    lang = "en",
                    iconUrl = "https://picsum.photos/seed/mangakakalot/100/100",
                    isEnabled = true,
                    mangaCount = 850
                ),
                Source(
                    id = 5,
                    name = "Kiryuu",
                    lang = "id",
                    iconUrl = "https://picsum.photos/seed/kiryuu/100/100",
                    isEnabled = true,
                    mangaCount = 32
                )
            )
            
            _uiState.update { it.copy(availableSources = sources) }
        }
    }

    fun selectSource(source: Source) {
        _uiState.update { 
            it.copy(
                selectedSource = source,
                currentStep = MigrationStep.SELECT_MANGA
            )
        }
        loadMangaFromSource(source.id)
    }

    private fun loadMangaFromSource(sourceId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Mock manga data from selected source
            val manga = listOf(
                Manga(
                    id = "mg_1",
                    title = "Solo Leveling",
                    coverUrl = "https://picsum.photos/seed/solo/400/600",
                    author = "Chu-Gong",
                    description = "10 years ago, after the Gate opened...",
                    sourceId = sourceId.toString()
                ),
                Manga(
                    id = "mg_2",
                    title = "One Piece",
                    coverUrl = "https://picsum.photos/seed/onepiece/400/600",
                    author = "Eiichiro Oda",
                    description = "Gol D. Roger was known as the Pirate King...",
                    sourceId = sourceId.toString()
                ),
                Manga(
                    id = "mg_3",
                    title = "Jujutsu Kaisen",
                    coverUrl = "https://picsum.photos/seed/jjk/400/600",
                    author = "Gege Akutami",
                    description = "Yuuji is a genius at track and field...",
                    sourceId = sourceId.toString()
                ),
                Manga(
                    id = "mg_4",
                    title = "Chainsaw Man",
                    coverUrl = "https://picsum.photos/seed/csm/400/600",
                    author = "Tatsuki Fujimoto",
                    description = "Denji has a simple dream—to live a happy life...",
                    sourceId = sourceId.toString()
                ),
                Manga(
                    id = "mg_5",
                    title = "Spy x Family",
                    coverUrl = "https://picsum.photos/seed/spy/400/600",
                    author = "Tatsuya Endo",
                    description = "The master spy codenamed Twilight...",
                    sourceId = sourceId.toString()
                )
            )
            
            _uiState.update { 
                it.copy(
                    mangaToMigrate = manga,
                    isLoading = false
                )
            }
        }
    }

    fun toggleMangaSelection(manga: Manga) {
        _uiState.update { state ->
            val currentSelected = state.selectedManga
            val newSelected = if (currentSelected.contains(manga)) {
                currentSelected - manga
            } else {
                currentSelected + manga
            }
            state.copy(selectedManga = newSelected)
        }
    }

    fun selectAllManga() {
        _uiState.update { state ->
            state.copy(selectedManga = state.mangaToMigrate)
        }
    }

    fun deselectAllManga() {
        _uiState.update { it.copy(selectedManga = emptyList()) }
    }

    fun proceedToTargetSelection() {
        _uiState.update { it.copy(currentStep = MigrationStep.SEARCH_TARGET) }
    }

    fun selectTargetSource(source: Source) {
        _uiState.update { 
            it.copy(
                targetSource = source,
                currentStep = MigrationStep.CONFIRM_MIGRATION
            )
        }
        createMigrationItems()
    }

    private fun createMigrationItems() {
        val state = _uiState.value
        val items = state.selectedManga.map { manga ->
            MigrationItem(
                id = manga.id,
                manga = manga,
                currentSource = state.selectedSource!!,
                targetSource = state.targetSource,
                status = MigrationStatus.PENDING
            )
        }
        
        _uiState.update { 
            it.copy(
                migrationItems = items,
                totalCount = items.size,
                completedCount = 0
            )
        }
    }

    fun searchAlternatives(item: MigrationItem) {
        viewModelScope.launch {
            _uiState.update { state ->
                val updatedItems = state.migrationItems.map {
                    if (it.id == item.id) {
                        it.copy(status = MigrationStatus.SEARCHING)
                    } else it
                }
                state.copy(migrationItems = updatedItems)
            }
            
            // Mock search results
            kotlinx.coroutines.delay(1500)
            
            val foundManga = listOf(
                Manga(
                    id = "new_1",
                    title = item.manga.title,
                    coverUrl = item.manga.coverUrl,
                    author = item.manga.author,
                    description = item.manga.description,
                    sourceId = _uiState.value.targetSource?.id.toString()
                ),
                Manga(
                    id = "new_2",
                    title = "${item.manga.title} (Alternative)",
                    coverUrl = item.manga.coverUrl,
                    author = item.manga.author,
                    description = item.manga.description,
                    sourceId = _uiState.value.targetSource?.id.toString()
                )
            )
            
            _uiState.update { state ->
                val updatedItems = state.migrationItems.map {
                    if (it.id == item.id) {
                        it.copy(
                            status = MigrationStatus.CHOOSING,
                            foundManga = foundManga,
                            selectedMatch = foundManga.firstOrNull()
                        )
                    } else it
                }
                state.copy(migrationItems = updatedItems)
            }
        }
    }

    fun selectAlternative(item: MigrationItem, manga: Manga) {
        _uiState.update { state ->
            val updatedItems = state.migrationItems.map {
                if (it.id == item.id) {
                    it.copy(selectedMatch = manga)
                } else it
            }
            state.copy(migrationItems = updatedItems)
        }
    }

    fun confirmAndMigrate() {
        _uiState.update { it.copy(currentStep = MigrationStep.MIGRATING) }
        startMigration()
    }

    private fun startMigration() {
        viewModelScope.launch {
            val items = _uiState.value.migrationItems
            
            items.forEachIndexed { index, item ->
                // Update status to migrating
                _uiState.update { state ->
                    val updatedItems = state.migrationItems.map {
                        if (it.id == item.id) {
                            it.copy(status = MigrationStatus.MIGRATING)
                        } else it
                    }
                    state.copy(migrationItems = updatedItems)
                }
                
                // Simulate migration process
                kotlinx.coroutines.delay(2000)
                
                // Mark as completed
                _uiState.update { state ->
                    val updatedItems = state.migrationItems.map {
                        if (it.id == item.id) {
                            it.copy(status = MigrationStatus.COMPLETED)
                        } else it
                    }
                    state.copy(
                        migrationItems = updatedItems,
                        completedCount = index + 1
                    )
                }
            }
            
            _uiState.update { 
                it.copy(
                    currentStep = MigrationStep.COMPLETED,
                    successMessage = "Successfully migrated ${items.size} manga"
                )
            }
        }
    }

    fun skipMigration(item: MigrationItem) {
        _uiState.update { state ->
            val updatedItems = state.migrationItems.map {
                if (it.id == item.id) {
                    it.copy(status = MigrationStatus.CANCELLED)
                } else it
            }
            state.copy(migrationItems = updatedItems)
        }
    }

    fun updatePreferences(preferences: MigrationPreferences) {
        _uiState.update { it.copy(preferences = preferences) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun goBack() {
        val currentStep = _uiState.value.currentStep
        val previousStep = when (currentStep) {
            MigrationStep.SELECT_MANGA -> MigrationStep.SELECT_SOURCE
            MigrationStep.SEARCH_TARGET -> MigrationStep.SELECT_MANGA
            MigrationStep.CONFIRM_MIGRATION -> MigrationStep.SEARCH_TARGET
            else -> currentStep
        }
        _uiState.update { it.copy(currentStep = previousStep) }
    }

    fun resetMigration() {
        _uiState.update {
            it.copy(
                selectedSource = null,
                targetSource = null,
                selectedManga = emptyList(),
                migrationItems = emptyList(),
                currentStep = MigrationStep.SELECT_SOURCE,
                completedCount = 0,
                totalCount = 0,
                successMessage = null
            )
        }
    }
}
