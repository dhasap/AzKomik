package com.azkomik.presentation.screens.download

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azkomik.domain.model.download.Download
import com.azkomik.domain.model.download.DownloadStatus
import com.azkomik.presentation.theme.AppColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadQueueUiState(
    val downloads: List<Download> = emptyList(),
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val totalQueueSize: Int = 0,
    val totalProgress: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class DownloadQueueViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DownloadQueueUiState())
    val uiState: StateFlow<DownloadQueueUiState> = _uiState.asStateFlow()

    init {
        loadDownloads()
    }

    private fun loadDownloads() {
        viewModelScope.launch {
            // Mock data
            val downloads = listOf(
                Download(
                    mangaId = "1",
                    chapterId = "ch_1",
                    mangaTitle = "One Piece",
                    chapterName = "Chapter 1105",
                    sourceId = 1,
                    status = DownloadStatus.DOWNLOADING,
                    progress = 65,
                    totalPages = 20,
                    downloadedPages = 13,
                    queuePosition = 1
                ),
                Download(
                    mangaId = "1",
                    chapterId = "ch_2",
                    mangaTitle = "One Piece",
                    chapterName = "Chapter 1104",
                    sourceId = 1,
                    status = DownloadStatus.PENDING,
                    progress = 0,
                    totalPages = 18,
                    downloadedPages = 0,
                    queuePosition = 2
                ),
                Download(
                    mangaId = "2",
                    chapterId = "ch_3",
                    mangaTitle = "Jujutsu Kaisen",
                    chapterName = "Chapter 249",
                    sourceId = 2,
                    status = DownloadStatus.COMPLETED,
                    progress = 100,
                    totalPages = 22,
                    downloadedPages = 22,
                    queuePosition = 0
                ),
                Download(
                    mangaId = "3",
                    chapterId = "ch_4",
                    mangaTitle = "Solo Leveling",
                    chapterName = "Chapter 179",
                    sourceId = 3,
                    status = DownloadStatus.ERROR,
                    progress = 30,
                    totalPages = 25,
                    downloadedPages = 7,
                    queuePosition = 3,
                    error = "Network timeout"
                )
            )
            
            _uiState.update {
                it.copy(
                    downloads = downloads,
                    isRunning = true,
                    totalQueueSize = downloads.size
                )
            }
        }
    }

    fun togglePause() {
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun clearQueue() {
        _uiState.update { it.copy(downloads = emptyList()) }
    }

    fun removeDownload(chapterId: String) {
        _uiState.update { state ->
            state.copy(downloads = state.downloads.filter { it.chapterId != chapterId })
        }
    }

    fun retryDownload(chapterId: String) {
        // Retry logic
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadQueueScreen(
    viewModel: DownloadQueueViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pendingDownloads = uiState.downloads.filter { it.status != DownloadStatus.COMPLETED }
    val completedDownloads = uiState.downloads.filter { it.status == DownloadStatus.COMPLETED }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Downloads", color = AppColors.TextPrimary)
                        Text(
                            "${pendingDownloads.size} pending",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }
                },
                actions = {
                    if (uiState.downloads.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearQueue() }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear all",
                                tint = AppColors.TextPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background
                )
            )
        }
    ) { padding ->
        if (uiState.downloads.isEmpty()) {
            EmptyDownloadView()
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Active downloads
                if (pendingDownloads.isNotEmpty()) {
                    item {
                        Text(
                            text = "DOWNLOADING",
                            style = MaterialTheme.typography.labelMedium,
                            color = AppColors.TextMuted,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    items(pendingDownloads, key = { it.chapterId }) { download ->
                        DownloadItem(
                            download = download,
                            onPauseResume = { viewModel.togglePause() },
                            onCancel = { viewModel.removeDownload(download.chapterId) },
                            onRetry = { viewModel.retryDownload(download.chapterId) }
                        )
                    }
                }
                
                // Completed downloads
                if (completedDownloads.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "COMPLETED",
                            style = MaterialTheme.typography.labelMedium,
                            color = AppColors.TextMuted,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    items(completedDownloads, key = { it.chapterId }) { download ->
                        DownloadItem(
                            download = download,
                            onPauseResume = { },
                            onCancel = { viewModel.removeDownload(download.chapterId) },
                            onRetry = { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItem(
    download: Download,
    onPauseResume: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = download.mangaTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = download.chapterName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary
                    )
                }
                
                // Action buttons
                Row {
                    when (download.status) {
                        DownloadStatus.DOWNLOADING -> {
                            IconButton(onClick = onPauseResume) {
                                Icon(
                                    imageVector = Icons.Default.Pause,
                                    contentDescription = "Pause",
                                    tint = AppColors.Primary
                                )
                            }
                        }
                        DownloadStatus.PAUSED -> {
                            IconButton(onClick = onPauseResume) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Resume",
                                    tint = AppColors.Primary
                                )
                            }
                        }
                        DownloadStatus.ERROR -> {
                            IconButton(onClick = onRetry) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retry",
                                    tint = AppColors.Primary
                                )
                            }
                        }
                        else -> { }
                    }
                    
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = AppColors.TextSecondary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress
            if (download.status == DownloadStatus.DOWNLOADING || 
                download.status == DownloadStatus.PAUSED) {
                LinearProgressIndicator(
                    progress = download.progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = when (download.status) {
                        DownloadStatus.ERROR -> AppColors.Accent
                        else -> AppColors.Primary
                    },
                    trackColor = AppColors.SurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${download.downloadedPages}/${download.totalPages} pages",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted
                    )
                    Text(
                        text = "${download.progress}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted
                    )
                }
            } else if (download.status == DownloadStatus.ERROR) {
                Text(
                    text = download.error ?: "Error occurred",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.Accent
                )
            } else if (download.status == DownloadStatus.COMPLETED) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AppColors.Secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.Secondary
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyDownloadView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.DownloadDone,
                contentDescription = null,
                tint = AppColors.TextMuted,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No downloads",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Download chapters to read offline",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
    }
}
