package com.azkomik.presentation.screens.library

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.azkomik.domain.model.library.LibrarySortType
import com.azkomik.presentation.components.MangaCard
import com.azkomik.presentation.navigation.Screen
import com.azkomik.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Library",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
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
        Column(
            modifier = Modifier.padding(padding)
        ) {
            FilterChips(
                selectedFilter = uiState.selectedCategory.toString(),
                onFilterChange = { /* Handle filter */ }
            )
            val mangaList = viewModel.getFilteredAndSortedManga()
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mangaList) { manga ->
                    MangaCard(
                        manga = manga,
                        onClick = {
                            navController.navigate(Screen.MangaDetail.createRoute(manga.id))
                        },
                        showRating = true
                    )
                }
            }
        }
    }

    if (showSortMenu) {
        SortMenu(
            currentSort = uiState.sortType.name,
            onSortSelected = { sortName ->
                val sortType = when(sortName) {
                    "ALPHABETICAL" -> LibrarySortType.ALPHABETICAL
                    "LAST_READ" -> LibrarySortType.LAST_READ
                    "LAST_MANGA_UPDATE" -> LibrarySortType.LAST_MANGA_UPDATE
                    "UNREAD_COUNT" -> LibrarySortType.UNREAD_COUNT
                    "TOTAL_CHAPTERS" -> LibrarySortType.TOTAL_CHAPTERS
                    "LATEST_CHAPTER" -> LibrarySortType.LATEST_CHAPTER
                    "DATE_FETCHED" -> LibrarySortType.DATE_FETCHED
                    "DATE_ADDED" -> LibrarySortType.DATE_ADDED
                    else -> LibrarySortType.ALPHABETICAL
                }
                viewModel.setSortType(sortType)
                showSortMenu = false
            },
            onDismiss = { showSortMenu = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val filters = listOf("All", "Reading", "Completed")
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterChange(filter) },
                label = { Text(filter) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.Primary,
                    selectedLabelColor = AppColors.TextPrimary
                )
            )
        }
    }
}

@Composable
fun SortMenu(
    currentSort: String,
    onSortSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sort by", color = AppColors.TextPrimary) },
        text = {
            Column {
                listOf(
                    "ALPHABETICAL" to "Title",
                    "LAST_READ" to "Last Read",
                    "LAST_MANGA_UPDATE" to "Latest Update",
                    "UNREAD_COUNT" to "Unread Count",
                    "TOTAL_CHAPTERS" to "Total Chapters",
                    "LATEST_CHAPTER" to "Latest Chapter",
                    "DATE_FETCHED" to "Date Fetched",
                    "DATE_ADDED" to "Date Added"
                ).forEach { (key, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onSortSelected(key) },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = AppColors.TextPrimary)
                        if (currentSort == key) {
                            Text("✓", color = AppColors.Primary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AppColors.Primary)
            }
        }
    )
}
