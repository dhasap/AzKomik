package com.azkomik.presentation.screens.explore

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic

import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.azkomik.domain.model.LatestManga
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.MangaSource
import com.azkomik.presentation.components.MangaCard
import com.azkomik.presentation.navigation.Screen
import com.azkomik.presentation.theme.AppColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExploreScreen(
    navController: NavController,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Bar
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SearchBarExplore(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Sumber Ekstensi
        item {
            Column {
                Text(
                    text = "SUMBER EKSTENSI",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.sources) { source ->
                        SourceItem(
                            source = source,
                            onClick = { /* Navigate to source */ }
                        )
                    }
                    item {
                        AddSourceButton(onClick = { /* Add source */ })
                    }
                }
            }
        }

        // Genre Populer
        item {
            Column {
                Text(
                    text = "Genre Populer",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                FlowRowExplore(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.popularGenres.forEach { genre ->
                        val isSelected = uiState.selectedGenre == genre
                        GenreChipExplore(
                            genre = genre,
                            isSelected = isSelected,
                            onClick = { viewModel.selectPopularGenre(genre) }
                        )
                    }
                }
            }
        }

        // Rekomendasi Minggu Ini
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rekomendasi Minggu Ini",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "Lihat Semua",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.Primary
                    )
                }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.recommendedManga) { manga ->
                        MangaCard(
                            manga = manga,
                            onClick = {
                                navController.navigate(Screen.MangaDetail.createRoute(manga.id))
                            },
                            showRating = true,
                            showBadge = true
                        )
                    }
                }
            }
        }

        // Terbaru dari Sumber
        item {
            Column {
                Text(
                    text = "Terbaru dari Sumber",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        items(uiState.latestFromSources) { latest ->
            LatestMangaItem(
                latest = latest,
                onClick = {
                    navController.navigate(Screen.MangaDetail.createRoute(latest.manga.id))
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SearchBarExplore(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = AppColors.SurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = AppColors.TextMuted
            )
            Spacer(modifier = Modifier.width(12.dp))
            androidx.compose.material3.TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { 
                    Text(
                        "Search titles, authors...",
                        color = AppColors.TextMuted
                    ) 
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = AppColors.TextPrimary,
                    unfocusedTextColor = AppColors.TextPrimary
                ),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Search",
                tint = AppColors.TextMuted
            )
        }
    }
}

@Composable
fun SourceItem(
    source: MangaSource,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = AppColors.SurfaceVariant,
            modifier = Modifier.size(56.dp)
        ) {
            AsyncImage(
                model = source.iconUrl ?: "https://picsum.photos/seed/${source.id}/100/100",
                contentDescription = source.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = source.name,
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.TextSecondary
        )
    }
}

@Composable
fun AddSourceButton(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = AppColors.SurfaceVariant,
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Source",
                    tint = AppColors.TextSecondary
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Add",
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.TextSecondary
        )
    }
}

@Composable
fun GenreChipExplore(
    genre: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) AppColors.Primary else AppColors.Surface,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, AppColors.Border) else null,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color.White else AppColors.TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun LatestMangaItem(
    latest: LatestManga,
    onClick: () -> Unit
) {
    val timeAgo = getTimeAgo(latest.chapter.dateUpload)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Source icon
        Surface(
            shape = CircleShape,
            color = AppColors.SurfaceVariant,
            modifier = Modifier.size(48.dp)
        ) {
            AsyncImage(
                model = latest.sourceIconUrl ?: "https://picsum.photos/seed/${latest.sourceName}/100/100",
                contentDescription = latest.sourceName,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = latest.manga.title,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = latest.sourceName,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted,
                    modifier = Modifier
                        .background(AppColors.SurfaceVariant, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted
                )
            }
            Text(
                text = "Ch. ${latest.chapter.number.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.Primary
            )
        }
        
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = AppColors.TextMuted
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowExplore(
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
