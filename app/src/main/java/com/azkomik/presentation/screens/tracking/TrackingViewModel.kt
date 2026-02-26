package com.azkomik.presentation.screens.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.tracking.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackingUiState(
    val services: List<TrackingService> = emptyList(),
    val selectedService: TrackingService? = null,
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val trackedManga: List<Track> = emptyList(),
    val searchResults: List<TrackSearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val preferences: TrackerPreferences = TrackerPreferences()
)

@HiltViewModel
class TrackingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    init {
        loadTrackingServices()
    }

    private fun loadTrackingServices() {
        viewModelScope.launch {
            val services = listOf(
                TrackingService(
                    id = TrackingServices.MYANIMELIST,
                    name = "MyAnimeList",
                    iconUrl = "https://myanimelist.net/img/sp/icon/apple-touch-icon-256.png",
                    isLoggedIn = false,
                    scoreFormat = ScoreFormat.POINT_10
                ),
                TrackingService(
                    id = TrackingServices.ANILIST,
                    name = "AniList",
                    iconUrl = "https://anilist.co/img/icons/android-chrome-512x512.png",
                    isLoggedIn = false,
                    scoreFormat = ScoreFormat.POINT_100
                ),
                TrackingService(
                    id = TrackingServices.KITSU,
                    name = "Kitsu",
                    iconUrl = "https://kitsu.io/favicon-32x32.png",
                    isLoggedIn = false,
                    scoreFormat = ScoreFormat.POINT_10
                ),
                TrackingService(
                    id = TrackingServices.MANGAUPDATES,
                    name = "MangaUpdates",
                    iconUrl = null,
                    isLoggedIn = false,
                    supportsScore = false,
                    scoreFormat = ScoreFormat.POINT_10
                ),
                TrackingService(
                    id = TrackingServices.SHIKIMORI,
                    name = "Shikimori",
                    iconUrl = null,
                    isLoggedIn = false,
                    scoreFormat = ScoreFormat.POINT_10
                ),
                TrackingService(
                    id = TrackingServices.BANGUMI,
                    name = "Bangumi",
                    iconUrl = null,
                    isLoggedIn = false,
                    scoreFormat = ScoreFormat.POINT_10
                )
            )
            
            _uiState.update { it.copy(services = services) }
        }
    }

    fun selectService(service: TrackingService) {
        _uiState.update { 
            it.copy(
                selectedService = service,
                isLoggedIn = service.isLoggedIn
            )
        }
    }

    fun login(serviceId: Int, username: String, token: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                val updatedServices = state.services.map { service ->
                    if (service.id == serviceId) {
                        service.copy(isLoggedIn = true)
                    } else service
                }
                state.copy(
                    services = updatedServices,
                    isLoggedIn = true,
                    userName = username,
                    selectedService = state.selectedService?.copy(isLoggedIn = true)
                )
            }
        }
    }

    fun logout(serviceId: Int) {
        viewModelScope.launch {
            _uiState.update { state ->
                val updatedServices = state.services.map { service ->
                    if (service.id == serviceId) {
                        service.copy(isLoggedIn = false)
                    } else service
                }
                state.copy(
                    services = updatedServices,
                    isLoggedIn = false,
                    userName = "",
                    selectedService = state.selectedService?.copy(isLoggedIn = false)
                )
            }
        }
    }

    fun searchManga(query: String, serviceId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, searchResults = emptyList()) }
            
            // Mock search results
            val results = listOf(
                TrackSearchResult(
                    trackingId = serviceId,
                    remoteId = 1,
                    title = "Solo Leveling",
                    totalChapters = 179,
                    publishingStatus = "Finished",
                    publishingType = "Manga",
                    coverUrl = "https://picsum.photos/seed/solo/200/300"
                ),
                TrackSearchResult(
                    trackingId = serviceId,
                    remoteId = 2,
                    title = "Solo Leveling: Ragnarok",
                    totalChapters = 20,
                    publishingStatus = "Publishing",
                    publishingType = "Manhwa",
                    coverUrl = "https://picsum.photos/seed/solorag/200/300"
                )
            )
            
            _uiState.update { 
                it.copy(
                    isSearching = false,
                    searchResults = results
                )
            }
        }
    }

    fun addTracking(
        mangaId: String,
        track: TrackSearchResult,
        status: Int = TrackStatus.READING,
        score: Double = 0.0
    ) {
        viewModelScope.launch {
            val newTrack = Track(
                mangaId = mangaId,
                syncId = track.trackingId,
                remoteId = track.remoteId,
                title = track.title,
                totalChapters = track.totalChapters,
                status = status,
                score = score
            )
            
            _uiState.update { state ->
                state.copy(
                    trackedManga = state.trackedManga + newTrack
                )
            }
        }
    }

    fun updateTrackProgress(
        mangaId: String,
        chaptersRead: Float,
        status: Int? = null,
        score: Double? = null
    ) {
        viewModelScope.launch {
            _uiState.update { state ->
                val updatedTracks = state.trackedManga.map { track ->
                    if (track.mangaId == mangaId) {
                        track.copy(
                            lastChapterRead = chaptersRead,
                            status = status ?: track.status,
                            score = score ?: track.score
                        )
                    } else track
                }
                state.copy(trackedManga = updatedTracks)
            }
        }
    }

    fun removeTracking(mangaId: String, syncId: Int) {
        _uiState.update { state ->
            state.copy(
                trackedManga = state.trackedManga.filter { 
                    !(it.mangaId == mangaId && it.syncId == syncId)
                }
            )
        }
    }

    fun updatePreferences(preferences: TrackerPreferences) {
        _uiState.update { it.copy(preferences = preferences) }
    }

    fun autoTrack(manga: Manga, chapterRead: Float) {
        if (!_uiState.value.preferences.autoTrack) return
        
        viewModelScope.launch {
            // Auto search and track logic would go here
            // For now, just update existing tracks
            updateTrackProgress(manga.id, chapterRead)
        }
    }

    fun getTrackingForManga(mangaId: String): List<Track> {
        return _uiState.value.trackedManga.filter { it.mangaId == mangaId }
    }
}
