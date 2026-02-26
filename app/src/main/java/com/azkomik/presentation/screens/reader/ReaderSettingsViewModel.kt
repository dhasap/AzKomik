package com.azkomik.presentation.screens.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.reader.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReaderSettingsUiState(
    val settings: ReaderSettings = ReaderSettings(),
    val availableFilters: List<PageFilterType> = PageFilterType.values().toList(),
    val currentFilter: PageFilterType = PageFilterType.ALL,
    val availableSorts: List<PageSortType> = PageSortType.values().toList(),
    val currentSort: PageSortType = PageSortType.SOURCE_ORDER
)

@HiltViewModel
class ReaderSettingsViewModel @Inject constructor() : ViewModel() {

    private val _settings = MutableStateFlow(ReaderSettings())
    private val _currentFilter = MutableStateFlow(PageFilterType.ALL)
    private val _currentSort = MutableStateFlow(PageSortType.SOURCE_ORDER)

    val uiState: StateFlow<ReaderSettingsUiState> = combine(
        _settings,
        _currentFilter,
        _currentSort
    ) { settings, filter, sort ->
        ReaderSettingsUiState(
            settings = settings,
            currentFilter = filter,
            currentSort = sort
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ReaderSettingsUiState()
    )

    fun setReadingMode(mode: ReadingMode) {
        _settings.update { it.copy(readingMode = mode) }
    }

    fun setScaleType(type: ScaleType) {
        _settings.update { it.copy(scaleType = type) }
    }

    fun setNavigationMode(mode: NavigationMode) {
        _settings.update { it.copy(navigationMode = mode) }
    }

    fun setPageFilter(filter: PageFilterType) {
        _currentFilter.value = filter
    }

    fun setPageSort(sort: PageSortType) {
        _currentSort.value = sort
    }

    fun toggleCropBorders() {
        _settings.update { it.copy(cropBorders = !it.cropBorders) }
    }

    fun toggleWebtoonCropBorders() {
        _settings.update { it.copy(webtoonCropBorders = !it.webtoonCropBorders) }
    }

    fun togglePageTransitions() {
        _settings.update { it.copy(pageTransitions = !it.pageTransitions) }
    }

    fun toggleDualPageSplit() {
        _settings.update { it.copy(dualPageSplit = !it.dualPageSplit) }
    }

    fun setBrightness(value: Float) {
        _settings.update { it.copy(customBrightness = value) }
    }

    fun toggleAutomaticBrightness() {
        _settings.update { it.copy(automaticBrightness = !it.automaticBrightness) }
    }

    fun toggleGrayscale() {
        _settings.update { it.copy(grayscale = !it.grayscale) }
    }

    fun toggleInvertedColors() {
        _settings.update { it.copy(invertedColors = !it.invertedColors) }
    }

    fun applyColorFilter(filter: ColorFilter) {
        _settings.update { it.copy(colorFilter = filter) }
    }
}
