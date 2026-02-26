package com.azkomik.presentation.screens.manga_detail

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.azkomik.domain.model.Chapter
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.MangaStatus
import com.azkomik.presentation.components.ChapterListItem
import com.azkomik.presentation.components.GenreChip
import com.azkomik.presentation.navigation.Screen
import com.azkomik.presentation.theme.AppColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MangaDetailScreen(
    mangaId: String,
    navController: NavController,
    viewModel: MangaDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mangaId) {
        viewModel.loadMangaDetail(mangaId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        navController.navigate(Screen.Tracking.createRoute(mangaId))
                    }) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = "Track",
                            tint = AppColors.TextPrimary
                        )
                    }
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = AppColors.TextPrimary
                        )
                    }
                    MangaMoreMenu(
                        mangaId = mangaId,
                        navController = navController
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleFavorite() },
                containerColor = if (uiState.isFavorite) AppColors.Primary else AppColors.SurfaceVariant,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (uiState.isFavorite) Color.White else AppColors.TextPrimary
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            item {
                MangaHeader(uiState.manga)
            }

            item {
                MangaInfoDetail(
                    manga = uiState.manga,
                    isBookmarked = uiState.isBookmarked,
                    isNotificationEnabled = uiState.isNotificationEnabled,
                    onBookmarkClick = viewModel::toggleBookmark,
                    onNotificationClick = viewModel::toggleNotification,
                    onReadClick = {
                        // Navigate to first unread chapter or first chapter
                        uiState.chapters.firstOrNull()?.let { chapter ->
                            navController.navigate(Screen.Reader.createRoute(chapter.id))
                        }
                    }
                )
            }

            item {
                DetailTabs(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = viewModel::selectTab
                )
            }

            when (uiState.selectedTab) {
                DetailTab.CHAPTERS -> {
                    item {
                        ChapterHeader(chapterCount = uiState.chapters.size)
                    }
                    items(uiState.chapters) { chapter ->
                        ChapterListItem(
                            chapter = chapter,
                            onClick = {
                                navController.navigate(Screen.Reader.createRoute(chapter.id))
                            },
                            onDownloadClick = { viewModel.downloadChapter(chapter.id) },
                            showNewBadge = System.currentTimeMillis() - chapter.dateUpload < 24 * 60 * 60 * 1000
                        )
                    }
                }
                DetailTab.INFO -> {
                    item {
                        MangaInfoTab(manga = uiState.manga)
                    }
                }
                DetailTab.COMMENTS -> {
                    item {
                        CommentsPlaceholder()
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun MangaHeader(manga: Manga?) {
    manga?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            AsyncImage(
                model = manga.coverUrl,
                contentDescription = manga.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                AppColors.Background.copy(alpha = 0.95f)
                            ),
                            startY = 100f
                        )
                    )
            )
        }
    }
}

@Composable
fun MangaInfoDetail(
    manga: Manga?,
    isBookmarked: Boolean,
    isNotificationEnabled: Boolean,
    onBookmarkClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onReadClick: () -> Unit
) {
    manga?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Status & Rating Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadgeDetail(status = manga.status)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${manga.rating}/5",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.Primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title
            Text(
                text = it.title,
                style = MaterialTheme.typography.headlineMedium,
                color = AppColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Author & Updated info
            Text(
                text = "${it.author} • ${it.genres.take(2).joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary
            )
            Text(
                text = "Updated 2h ago",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextMuted
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Read Button
                Button(
                    onClick = onReadClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mulai Baca")
                }
                
                // Bookmark Button
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isBookmarked) AppColors.Primary.copy(alpha = 0.2f) 
                            else AppColors.SurfaceVariant,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) AppColors.Primary else AppColors.TextPrimary
                    )
                }
                
                // Notification Button
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isNotificationEnabled) AppColors.Primary.copy(alpha = 0.2f)
                            else AppColors.SurfaceVariant,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isNotificationEnabled) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = if (isNotificationEnabled) AppColors.Primary else AppColors.TextPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatusBadgeDetail(status: MangaStatus) {
    val (text, color) = when (status) {
        MangaStatus.ONGOING -> "ONGOING" to AppColors.Ongoing
        MangaStatus.COMPLETED -> "COMPLETED" to AppColors.Completed
        else -> "ONGOING" to AppColors.TextMuted
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun DetailTabs(
    selectedTab: DetailTab,
    onTabSelected: (DetailTab) -> Unit
) {
    val tabs = listOf(
        DetailTab.CHAPTERS to "Bab",
        DetailTab.INFO to "Info",
        DetailTab.COMMENTS to "Komentar"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { (tab, label) ->
            val isSelected = tab == selectedTab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) AppColors.Primary else AppColors.TextSecondary
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(3.dp)
                            .background(AppColors.Primary, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterHeader(chapterCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$chapterCount Bab",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.TextPrimary
        )
        Row {
            IconButton(onClick = { /* Sort */ }) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort",
                    tint = AppColors.TextSecondary
                )
            }
            IconButton(onClick = { /* Search chapter */ }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AppColors.TextSecondary
                )
            }
        }
    }
}

@Composable
fun MangaInfoTab(manga: Manga?) {
    manga?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Synopsis
            Text(
                text = "Synopsis",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it.description,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Genres
            Text(
                text = "Genres",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRowDetail(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                it.genres.forEach { genre ->
                    GenreChip(
                        genre = genre,
                        isSelected = false,
                        onClick = { }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Additional Info
            InfoRow(label = "Author", value = it.author)
            InfoRow(label = "Artist", value = it.artist ?: "-")
            InfoRow(label = "Status", value = it.status.name)
            InfoRow(
                label = "Last Updated",
                value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(Date(it.lastUpdated))
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextPrimary
        )
    }
}

@Composable
fun CommentsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = null,
                tint = AppColors.TextMuted,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No comments yet",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextMuted
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowDetail(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}


@Composable
fun MangaMoreMenu(
    mangaId: String,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }
    
    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More",
            tint = AppColors.TextPrimary
        )
    }
    
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Migrate to other source") },
            onClick = {
                expanded = false
                navController.navigate(Screen.Migration.route)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = AppColors.TextPrimary
                )
            }
        )
        
        DropdownMenuItem(
            text = { Text("Share") },
            onClick = {
                expanded = false
                // Share functionality
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = AppColors.TextPrimary
                )
            }
        )
        
        DropdownMenuItem(
            text = { Text("Add to category") },
            onClick = {
                expanded = false
                // Add to category functionality
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = AppColors.TextPrimary
                )
            }
        )
        
        Divider(color = AppColors.SurfaceVariant)
        
        DropdownMenuItem(
            text = { Text("Refresh") },
            onClick = {
                expanded = false
                // Refresh functionality
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = AppColors.TextPrimary
                )
            }
        )
    }
}
