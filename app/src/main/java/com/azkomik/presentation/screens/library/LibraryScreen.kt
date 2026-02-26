package com.azkomik.presentation.screens.library

import android.annotation.SuppressLint
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
    var showFilterMenu by remember { mutableStateOf(false) }
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
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = AppColors.TextPrimary
                        )
                    }
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
                selectedFilter = uiState.selectedFilter,
                onFilterChange = viewModel::setFilter
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredManga) { manga ->
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

    if (showFilterMenu) {
        FilterMenu(
            currentFilter = uiState.selectedFilter,
            onFilterSelected = {
                viewModel.setFilter(it)
                showFilterMenu = false
            },
            onDismiss = { showFilterMenu = false }
        )
    }

    if (showSortMenu) {
        SortMenu(
            currentSort = uiState.sortBy,
            onSortSelected = {
                viewModel.setSort(it)
                showSortMenu = false
            },
            onDismiss = { showSortMenu = false }
        )
    }
}

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
fun FilterMenu(
    currentFilter: String,
    onFilterSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter by", color = AppColors.TextPrimary) },
        text = {
            Column {
                listOf("All", "Reading", "Completed").forEach { filter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(filter, color = AppColors.TextPrimary)
                        if (currentFilter == filter) {
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
                listOf("Title", "Author", "Rating", "Recent").forEach { sort ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(sort, color = AppColors.TextPrimary)
                        if (currentSort == sort) {
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
