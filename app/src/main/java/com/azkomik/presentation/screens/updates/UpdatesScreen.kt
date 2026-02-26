package com.azkomik.presentation.screens.updates

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.azkomik.domain.model.Chapter
import com.azkomik.domain.model.Manga
import com.azkomik.presentation.navigation.Screen
import com.azkomik.presentation.theme.AppColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UpdatesScreen(
    navController: NavController,
    viewModel: UpdatesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "New Releases",
                        style = MaterialTheme.typography.headlineSmall,
                        color = AppColors.TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AppColors.TextPrimary
                        )
                    }
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Filter Tabs
            item {
                UpdateFilterTabs(
                    selectedFilter = uiState.selectedFilter,
                    onFilterChange = viewModel::setFilter,
                    unreadCount = uiState.recentUpdates.count { !it.second.isRead },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(uiState.filteredUpdates) { (manga, chapter) ->
                UpdateListItemFull(
                    manga = manga,
                    chapter = chapter,
                    onClick = {
                        navController.navigate(Screen.Reader.createRoute(chapter.id))
                    },
                    onDownloadClick = { viewModel.downloadChapter(chapter.id) },
                    onMoreClick = { /* More options */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating action button to mark all as read
        FloatingActionButton(
            onClick = { /* Mark all as read */ },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = AppColors.Primary,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Mark all as read",
                tint = Color.White
            )
        }
    }
}

@Composable
fun UpdateFilterTabs(
    selectedFilter: UpdateFilter,
    onFilterChange: (UpdateFilter) -> Unit,
    unreadCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterTab(
            text = "All Updates",
            isSelected = selectedFilter == UpdateFilter.ALL,
            onClick = { onFilterChange(UpdateFilter.ALL) },
            modifier = Modifier.weight(1f)
        )
        FilterTab(
            text = "Following",
            isSelected = selectedFilter == UpdateFilter.FOLLOWING,
            onClick = { onFilterChange(UpdateFilter.FOLLOWING) },
            modifier = Modifier.weight(1f)
        )
        FilterTab(
            text = "Unread",
            isSelected = selectedFilter == UpdateFilter.UNREAD,
            onClick = { onFilterChange(UpdateFilter.UNREAD) },
            badge = unreadCount,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FilterTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: Int = 0
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) AppColors.Primary else AppColors.SurfaceVariant,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Color.White else AppColors.TextSecondary
            )
            if (badge > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AppColors.Unread, CircleShape)
                )
            }
        }
    }
}

@Composable
fun UpdateListItemFull(
    manga: Manga,
    chapter: Chapter,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    val timeAgo = getTimeAgo(chapter.dateUpload)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = manga.coverUrl,
                    contentDescription = manga.title,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                // Unread indicator
                if (!chapter.isRead) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(AppColors.Unread, CircleShape)
                            .align(Alignment.TopStart)
                            .offset(x = (-3).dp, y = (-3).dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = manga.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (!chapter.isRead) {
                        NewBadgeUpdate()
                    } else {
                        ReadBadgeUpdate()
                    }
                }
                Text(
                    text = "Chapter ${chapter.number.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = AppColors.TextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted
                    )
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = AppColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    if (chapter.isRead) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Read",
                            tint = AppColors.Secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (chapter.isDownloaded) 
                                Icons.Default.DownloadDone else Icons.Default.Download,
                            contentDescription = "Download",
                            tint = if (chapter.isDownloaded) AppColors.Secondary else AppColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewBadgeUpdate() {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = AppColors.New.copy(alpha = 0.2f)
    ) {
        Text(
            text = "NEW",
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.New,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ReadBadgeUpdate() {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = AppColors.SurfaceLight
    ) {
        Text(
            text = "READ",
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.TextMuted,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)
    
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes mins ago"
        hours < 24 -> "$hours hours ago"
        days == 1L -> "Yesterday"
        days < 7 -> "$days days ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}
