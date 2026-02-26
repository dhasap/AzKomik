package com.azkomik.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Explore : Screen("explore")
    object Library : Screen("library")
    object Updates : Screen("updates")
    object Profile : Screen("profile")
    object MangaDetail : Screen("manga/{mangaId}") {
        fun createRoute(mangaId: String) = "manga/$mangaId"
    }
    object Reader : Screen("reader/{chapterId}") {
        fun createRoute(chapterId: String) = "reader/$chapterId"
    }
    object Settings : Screen("settings")
    object SourceManager : Screen("sources")
    object Tracking : Screen("tracking/{mangaId}") {
        fun createRoute(mangaId: String) = "tracking/$mangaId"
        fun createGlobalRoute() = "tracking/global"
    }
    object DownloadQueue : Screen("downloads")
    object Backup : Screen("backup")
    object Migration : Screen("migration")
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        Screen.Home.route, 
        "Beranda", 
        Icons.Outlined.Home,
        Icons.Filled.Home
    ),
    BottomNavItem(
        Screen.Explore.route, 
        "Jelajahi", 
        Icons.Outlined.Explore,
        Icons.Filled.Explore
    ),
    BottomNavItem(
        Screen.Library.route, 
        "Koleksi", 
        Icons.Outlined.CollectionsBookmark,
        Icons.Filled.CollectionsBookmark
    ),
    BottomNavItem(
        Screen.Updates.route, 
        "Riwayat", 
        Icons.Outlined.History,
        Icons.Filled.History
    ),
    BottomNavItem(
        Screen.Profile.route, 
        "Akun", 
        Icons.Outlined.Person,
        Icons.Filled.Person
    )
)
