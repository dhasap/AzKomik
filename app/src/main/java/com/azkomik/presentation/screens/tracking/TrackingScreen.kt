package com.azkomik.presentation.screens.tracking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.azkomik.domain.model.tracking.*
import com.azkomik.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    viewModel: TrackingViewModel = hiltViewModel(),
    mangaId: String? = null, // If provided, show tracking for specific manga
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLoginDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (mangaId != null) "Track Manga" else "Tracking Services",
                        color = AppColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                actions = {
                    if (mangaId == null) {
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
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
        if (mangaId != null) {
            // Manga-specific tracking view
            MangaTrackingView(
                mangaId = mangaId,
                tracks = uiState.trackedManga.filter { it.mangaId == mangaId },
                services = uiState.services,
                onAddTracking = { showSearchDialog = true },
                onRemoveTracking = { track ->
                    viewModel.removeTracking(mangaId, track.syncId)
                },
                onUpdateProgress = { track, chapters, status, score ->
                    viewModel.updateTrackProgress(mangaId, chapters, status, score)
                },
                modifier = Modifier.padding(padding)
            )
        } else {
            // Main tracking services view
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "CONNECTED SERVICES",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(uiState.services) { service ->
                    TrackingServiceCard(
                        service = service,
                        onClick = {
                            if (service.isLoggedIn) {
                                viewModel.selectService(service)
                            } else {
                                showLoginDialog = true
                                viewModel.selectService(service)
                            }
                        },
                        onLogin = {
                            showLoginDialog = true
                            viewModel.selectService(service)
                        },
                        onLogout = {
                            viewModel.logout(service.id)
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "ABOUT TRACKING",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                item {
                    TrackingInfoCard()
                }
            }
        }
    }

    // Login Dialog
    if (showLoginDialog && uiState.selectedService != null) {
        TrackingLoginDialog(
            service = uiState.selectedService!!,
            onDismiss = { showLoginDialog = false },
            onLogin = { username, token ->
                viewModel.login(uiState.selectedService!!.id, username, token)
                showLoginDialog = false
            }
        )
    }

    // Search Dialog
    if (showSearchDialog && uiState.selectedService != null) {
        TrackingSearchDialog(
            service = uiState.selectedService!!,
            searchResults = uiState.searchResults,
            isSearching = uiState.isSearching,
            onDismiss = { showSearchDialog = false },
            onSearch = { query ->
                viewModel.searchManga(query, uiState.selectedService!!.id)
            },
            onSelectResult = { result ->
                mangaId?.let { id ->
                    viewModel.addTracking(id, result)
                    showSearchDialog = false
                }
            }
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        TrackingSettingsDialog(
            preferences = uiState.preferences,
            onDismiss = { showSettingsDialog = false },
            onUpdatePreferences = { prefs ->
                viewModel.updatePreferences(prefs)
                showSettingsDialog = false
            }
        )
    }
}

@Composable
fun TrackingServiceCard(
    service: TrackingService,
    onClick: () -> Unit,
    onLogin: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (service.isLoggedIn) 
                AppColors.Surface else AppColors.SurfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (service.isLoggedIn) {
            BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.3f))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Service icon
            Surface(
                shape = CircleShape,
                color = AppColors.SurfaceVariant,
                modifier = Modifier.size(56.dp)
            ) {
                AsyncImage(
                    model = service.iconUrl ?: "https://picsum.photos/seed/${service.id}/100/100",
                    contentDescription = service.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = if (service.isLoggedIn) "Connected" else "Not connected",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (service.isLoggedIn) AppColors.Secondary else AppColors.TextMuted
                )
                if (service.isLoggedIn) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AppColors.Secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Auto-sync enabled",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.Secondary
                        )
                    }
                }
            }
            
            // Login/Logout button
            if (service.isLoggedIn) {
                OutlinedButton(
                    onClick = onLogout,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.TextSecondary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AppColors.Border)
                    )
                ) {
                    Text("Logout")
                }
            } else {
                Button(
                    onClick = onLogin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary
                    )
                ) {
                    Text("Login")
                }
            }
        }
    }
}

@Composable
fun MangaTrackingView(
    mangaId: String,
    tracks: List<Track>,
    services: List<TrackingService>,
    onAddTracking: () -> Unit,
    onRemoveTracking: (Track) -> Unit,
    onUpdateProgress: (Track, Float, Int?, Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (tracks.isEmpty()) {
            // No tracking set up
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.TrackChanges,
                        contentDescription = null,
                        tint = AppColors.TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Not tracking this manga",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add tracking to sync your progress",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onAddTracking) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Tracking")
                    }
                }
            }
        } else {
            // Show active tracking
            Text(
                text = "ACTIVE TRACKING",
                style = MaterialTheme.typography.labelMedium,
                color = AppColors.TextMuted,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            tracks.forEach { track ->
                val service = services.find { it.id == track.syncId }
                service?.let {
                    ActiveTrackingCard(
                        track = track,
                        service = it,
                        onRemove = { onRemoveTracking(track) },
                        onUpdateProgress = { chapters, status, score ->
                            onUpdateProgress(track, chapters, status, score)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            // Add more tracking button
            if (tracks.size < services.count { it.isLoggedIn }) {
                OutlinedButton(
                    onClick = onAddTracking,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.Primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add another service")
                }
            }
        }
    }
}

@Composable
fun ActiveTrackingCard(
    track: Track,
    service: TrackingService,
    onRemove: () -> Unit,
    onUpdateProgress: (Float, Int?, Double?) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = service.iconUrl,
                    contentDescription = service.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove tracking",
                        tint = AppColors.Accent
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Status", TrackStatus.toString(track.status))
                StatItem("Chapters", "${track.lastChapterRead.toInt()}/${track.totalChapters}")
                if (service.supportsScore) {
                    StatItem("Score", if (track.score > 0) "${track.score}" else "-")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Edit button
            OutlinedButton(
                onClick = { showEditDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppColors.Primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Update Progress")
            }
        }
    }
    
    if (showEditDialog) {
        TrackEditDialog(
            track = track,
            service = service,
            onDismiss = { showEditDialog = false },
            onSave = { chapters, status, score ->
                onUpdateProgress(chapters, status, score)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.Primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.TextMuted
        )
    }
}

@Composable
fun TrackingInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "What is tracking?",
                style = MaterialTheme.typography.titleSmall,
                color = AppColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tracking allows you to sync your reading progress with external services like MyAnimeList, AniList, and more. Your progress will be automatically updated as you read.",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
    }
}
