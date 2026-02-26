package com.azkomik.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.azkomik.presentation.components.*
import com.azkomik.presentation.navigation.Screen
import com.azkomik.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text(
                            "AzKomik",
                            style = MaterialTheme.typography.titleLarge,
                            color = AppColors.Primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Explore.route) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AppColors.TextPrimary
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                FeaturedBanner(
                    manga = uiState.featuredManga,
                    onContinueReading = {
                        navController.navigate(Screen.MangaDetail.createRoute(uiState.featuredManga?.id ?: ""))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    continueChapter = "Bab 102"
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Favorit Pilihan",
                    actionText = "Lihat Semua",
                    onActionClick = { navController.navigate(Screen.Library.route) }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.favorites) { manga ->
                        MangaCard(
                            manga = manga,
                            onClick = {
                                navController.navigate(Screen.MangaDetail.createRoute(manga.id))
                            },
                            showChapterBadge = "Ch. ${(100..200).random()}"
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(title = "Semua Koleksi")
                FilterTabs(
                    filters = listOf("Semua", "Belum Dibaca", "Sedang Baca", "Selesai"),
                    selectedFilter = uiState.selectedFilter,
                    onFilterChange = viewModel::setFilter,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(uiState.filteredManga.chunked(3)) { rowManga ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowManga.forEach { manga ->
                        MangaCard(
                            manga = manga,
                            onClick = {
                                navController.navigate(Screen.MangaDetail.createRoute(manga.id))
                            },
                            showRating = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(3 - rowManga.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
