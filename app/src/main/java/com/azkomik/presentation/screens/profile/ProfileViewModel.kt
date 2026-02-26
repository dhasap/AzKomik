package com.azkomik.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.*
import com.azkomik.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User = User(),
    val stats: UserStats = UserStats(),
    val readingHistory: List<ReadingHistory> = emptyList(),
    val isNotificationsEnabled: Boolean = true,
    val isBiometricEnabled: Boolean = false,
    val cacheSize: String = "240MB",
    val theme: String = "AMOLED Dark",
    val downloadLocation: String = "Internal Storage",
    val syncServices: List<String> = listOf("MyAnimeList", "Anilist"),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val user = User(
                username = "OtakuReader",
                level = 42,
                chaptersRead = 1200,
                streak = 245,
                readingHours = 342,
                memberStatus = "Free Member"
            )
            
            val stats = UserStats(
                totalMangaRead = 156,
                totalChaptersRead = 1200,
                totalReadingTime = 342
            )
            
            _uiState.update {
                it.copy(
                    user = user,
                    stats = stats,
                    isLoading = false
                )
            }
        }
    }

    fun toggleNotifications() {
        _uiState.update { it.copy(isNotificationsEnabled = !it.isNotificationsEnabled) }
    }

    fun toggleBiometric() {
        _uiState.update { it.copy(isBiometricEnabled = !it.isBiometricEnabled) }
    }

    fun clearCache() {
        _uiState.update { it.copy(cacheSize = "0MB") }
    }

    fun logout() {
        // Implementation for logout
    }
}
