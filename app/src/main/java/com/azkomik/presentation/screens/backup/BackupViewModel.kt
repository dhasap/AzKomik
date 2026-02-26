package com.azkomik.presentation.screens.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.backup.*
import com.azkomik.domain.model.library.LibraryCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class BackupUiState(
    val isLoading: Boolean = false,
    val backupProgress: Float = 0f,
    val restoreProgress: Float = 0f,
    val lastBackup: BackupInfo? = null,
    val backupHistory: List<BackupInfo> = emptyList(),
    val autoBackupEnabled: Boolean = false,
    val autoBackupFrequency: BackupFrequency = BackupFrequency.DAILY,
    val autoBackupLocation: String = "",
    val error: String? = null,
    val successMessage: String? = null,
    val showCreateDialog: Boolean = false,
    val showRestoreDialog: Boolean = false,
    val backupOptions: BackupOptions = BackupOptions()
)

data class BackupInfo(
    val uri: String,
    val fileName: String,
    val fileSize: String,
    val timestamp: Long,
    val mangaCount: Int,
    val categoryCount: Int
) {
    val formattedDate: String
        get() = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            .format(Date(timestamp))
}

data class BackupOptions(
    val includeLibrary: Boolean = true,
    val includeCategories: Boolean = true,
    val includeChapters: Boolean = true,
    val includeTracking: Boolean = true,
    val includeHistory: Boolean = true,
    val includeSettings: Boolean = true,
    val includeExtensions: Boolean = false
)

enum class BackupFrequency {
    DAILY,
    WEEKLY,
    EVERY_2_DAYS;
    
    fun toDays(): Int = when (this) {
        DAILY -> 1
        EVERY_2_DAYS -> 2
        WEEKLY -> 7
    }
    
    fun displayName(): String = when (this) {
        DAILY -> "Daily"
        EVERY_2_DAYS -> "Every 2 days"
        WEEKLY -> "Weekly"
    }
}

@HiltViewModel
class BackupViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    init {
        loadBackupInfo()
    }

    private fun loadBackupInfo() {
        viewModelScope.launch {
            // Mock backup history
            val history = listOf(
                BackupInfo(
                    uri = "content://backups/backup_20240225.proto.gz",
                    fileName = "azkomik_backup_20240225.proto.gz",
                    fileSize = "2.4 MB",
                    timestamp = System.currentTimeMillis() - 86400000,
                    mangaCount = 156,
                    categoryCount = 5
                ),
                BackupInfo(
                    uri = "content://backups/backup_20240220.proto.gz",
                    fileName = "azkomik_backup_20240220.proto.gz",
                    fileSize = "2.1 MB",
                    timestamp = System.currentTimeMillis() - 518400000,
                    mangaCount = 142,
                    categoryCount = 4
                ),
                BackupInfo(
                    uri = "content://backups/backup_20240215.proto.gz",
                    fileName = "azkomik_backup_20240215.proto.gz",
                    fileSize = "1.9 MB",
                    timestamp = System.currentTimeMillis() - 950400000,
                    mangaCount = 128,
                    categoryCount = 4
                )
            )
            
            _uiState.update {
                it.copy(
                    lastBackup = history.firstOrNull(),
                    backupHistory = history,
                    autoBackupEnabled = true,
                    autoBackupFrequency = BackupFrequency.DAILY
                )
            }
        }
    }

    fun createBackup(location: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, backupProgress = 0f) }
            
            // Simulate backup process
            for (progress in 0..100 step 10) {
                kotlinx.coroutines.delay(200)
                _uiState.update { it.copy(backupProgress = progress / 100f) }
            }
            
            val newBackup = BackupInfo(
                uri = location,
                fileName = "azkomik_backup_${getCurrentDate()}.proto.gz",
                fileSize = "2.5 MB",
                timestamp = System.currentTimeMillis(),
                mangaCount = 160,
                categoryCount = 5
            )
            
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    backupProgress = 1f,
                    lastBackup = newBackup,
                    backupHistory = listOf(newBackup) + state.backupHistory,
                    successMessage = "Backup created successfully",
                    showCreateDialog = false
                )
            }
            
            // Clear success message after delay
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(successMessage = null) }
        }
    }

    fun restoreBackup(uri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, restoreProgress = 0f) }
            
            // Simulate restore process
            for (progress in 0..100 step 10) {
                kotlinx.coroutines.delay(300)
                _uiState.update { it.copy(restoreProgress = progress / 100f) }
            }
            
            _uiState.update {
                it.copy(
                    isLoading = false,
                    restoreProgress = 1f,
                    successMessage = "Backup restored successfully. App will restart.",
                    showRestoreDialog = false
                )
            }
            
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(successMessage = null) }
        }
    }

    fun deleteBackup(backup: BackupInfo) {
        _uiState.update { state ->
            state.copy(
                backupHistory = state.backupHistory.filter { it.uri != backup.uri },
                lastBackup = if (state.lastBackup?.uri == backup.uri) {
                    state.backupHistory.drop(1).firstOrNull()
                } else state.lastBackup
            )
        }
    }

    fun toggleAutoBackup(enabled: Boolean) {
        _uiState.update { it.copy(autoBackupEnabled = enabled) }
    }

    fun setAutoBackupFrequency(frequency: BackupFrequency) {
        _uiState.update { it.copy(autoBackupFrequency = frequency) }
    }

    fun setAutoBackupLocation(location: String) {
        _uiState.update { it.copy(autoBackupLocation = location) }
    }

    fun updateBackupOptions(options: BackupOptions) {
        _uiState.update { it.copy(backupOptions = options) }
    }

    fun showCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun hideCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }

    fun showRestoreDialog() {
        _uiState.update { it.copy(showRestoreDialog = true) }
    }

    fun hideRestoreDialog() {
        _uiState.update { it.copy(showRestoreDialog = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            .format(Date())
    }

    fun validateBackupFile(uri: String): Boolean {
        // Validate backup file format and version
        return uri.endsWith(".proto.gz") || uri.endsWith(".json")
    }
}
