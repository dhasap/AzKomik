package com.azkomik.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.azkomik.presentation.components.BottomNavigationBar
import com.azkomik.presentation.screens.backup.BackupScreen
import com.azkomik.presentation.screens.download.DownloadQueueScreen
import com.azkomik.presentation.screens.migration.MigrationScreen
import com.azkomik.presentation.screens.explore.ExploreScreen
import com.azkomik.presentation.screens.home.HomeScreen
import com.azkomik.presentation.screens.library.LibraryScreen
import com.azkomik.presentation.screens.manga_detail.MangaDetailScreen
import com.azkomik.presentation.screens.profile.ProfileScreen
import com.azkomik.presentation.screens.reader.ReaderScreen
import com.azkomik.presentation.screens.tracking.TrackingScreen
import com.azkomik.presentation.screens.updates.UpdatesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    items = bottomNavItems
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Explore.route) {
                ExploreScreen(navController = navController)
            }
            composable(Screen.Library.route) {
                LibraryScreen(navController = navController)
            }
            composable(Screen.Updates.route) {
                UpdatesScreen(navController = navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable(
                route = Screen.MangaDetail.route,
                arguments = listOf(navArgument("mangaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mangaId = backStackEntry.arguments?.getString("mangaId") ?: ""
                MangaDetailScreen(
                    mangaId = mangaId,
                    navController = navController
                )
            }
            composable(
                route = Screen.Reader.route,
                arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
            ) { backStackEntry ->
                val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                ReaderScreen(
                    chapterId = chapterId,
                    navController = navController
                )
            }
            composable(
                route = Screen.Tracking.route,
                arguments = listOf(navArgument("mangaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mangaId = backStackEntry.arguments?.getString("mangaId") ?: ""
                TrackingScreen(
                    mangaId = mangaId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.DownloadQueue.route) {
                DownloadQueueScreen()
            }
            composable(Screen.Backup.route) {
                BackupScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Migration.route) {
                MigrationScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
