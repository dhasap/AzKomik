package com.azkomik.presentation.screens.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.azkomik.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Header with title
        item {
            TopAppBar(
                title = {
                    Text(
                        "PROFIL & PENGATURAN",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background
                )
            )
        }

        // Profile Header
        item {
            ProfileHeaderSection(
                user = uiState.user,
                onEditClick = { /* Edit profile */ }
            )
        }

        // Stats Grid
        item {
            StatsGridSection(stats = uiState.stats)
        }

        // UMUM Section
        item {
            SectionTitle("UMUM")
            SettingsMenuItem(
                icon = Icons.Outlined.DarkMode,
                iconTint = AppColors.Primary,
                title = "Tema Aplikasi",
                subtitle = uiState.theme,
                onClick = { /* Theme settings */ }
            )
            SettingsMenuItemWithSwitch(
                icon = Icons.Outlined.Notifications,
                iconTint = Color(0xFF3B82F6),
                title = "Notifikasi",
                subtitle = "Push & Email",
                checked = uiState.isNotificationsEnabled,
                onCheckedChange = { viewModel.toggleNotifications() }
            )
        }

        // PENYIMPANAN Section
        item {
            SectionTitle("PENYIMPANAN")
            SettingsMenuItemWithAction(
                icon = Icons.Outlined.DeleteSweep,
                iconTint = Color(0xFF22C55E),
                title = "Hapus Cache",
                subtitle = "${uiState.cacheSize} digunakan",
                actionText = "Clean",
                onClick = { viewModel.clearCache() }
            )
            SettingsMenuItem(
                icon = Icons.Outlined.Folder,
                iconTint = Color(0xFFA855F7),
                title = "Lokasi Unduhan",
                subtitle = uiState.downloadLocation,
                onClick = { /* Download location settings */ }
            )
        }

        // PELACAKAN Section
        item {
            SectionTitle("PELACAKAN")
            SettingsMenuItem(
                icon = Icons.Outlined.Sync,
                iconTint = Color(0xFF3B82F6),
                title = "Tracking Services",
                subtitle = "MyAnimeList, AniList, Kitsu & more",
                onClick = { navController.navigate(Screen.Tracking.createGlobalRoute()) }
            )
        }
        
        // DOWNLOAD Section
        item {
            SectionTitle("UNDUHAN")
            SettingsMenuItem(
                icon = Icons.Outlined.Download,
                iconTint = Color(0xFF22C55E),
                title = "Download Queue",
                subtitle = "Manage offline downloads",
                onClick = { navController.navigate(Screen.DownloadQueue.route) }
            )
        }
        
        // DATA Section
        item {
            SectionTitle("DATA")
            SettingsMenuItem(
                icon = Icons.Outlined.Backup,
                iconTint = Color(0xFFF97316),
                title = "Backup & Restore",
                subtitle = "Export and import your data",
                onClick = { navController.navigate(Screen.Backup.route) }
            )
            SettingsMenuItem(
                icon = Icons.Outlined.SwapHoriz,
                iconTint = Color(0xFF8B5CF6),
                title = "Migrate",
                subtitle = "Move manga to different source",
                onClick = { navController.navigate(Screen.Migration.route) }
            )
        }

        // KEAMANAN Section
        item {
            SectionTitle("KEAMANAN")
            SettingsMenuItemWithSwitch(
                icon = Icons.Outlined.Fingerprint,
                iconTint = Color(0xFFEF4444),
                title = "Kunci Aplikasi",
                subtitle = "Biometric / FaceID",
                checked = uiState.isBiometricEnabled,
                onCheckedChange = { viewModel.toggleBiometric() }
            )
        }

        // Log Out Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppColors.Accent
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(AppColors.Accent)
                )
            ) {
                Text("Log Out", color = AppColors.Accent)
            }
        }

        // Features Info
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.SurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TachiyomiJ2K Features",
                        style = MaterialTheme.typography.titleSmall,
                        color = AppColors.Primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This app includes advanced features inspired by TachiyomiJ2K:",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val features = listOf(
                        "Multiple reader modes (Webtoon, Paged, Vertical)",
                        "Download chapters for offline reading",
                        "Track progress on MyAnimeList, AniList, etc.",
                        "Migrate manga between sources",
                        "Backup and restore your library"
                    )
                    
                    features.forEach { feature ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AppColors.Secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // App Version
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AzKomik v2.0.4 (Build 420)",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ProfileHeaderSection(
    user: User,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with edit button
        Box {
            Surface(
                shape = CircleShape,
                color = AppColors.SurfaceVariant,
                modifier = Modifier.size(100.dp)
            ) {
                AsyncImage(
                    model = user.avatarUrl ?: "https://picsum.photos/seed/avatar/200/200",
                    contentDescription = user.username,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            // Edit button
            Surface(
                shape = CircleShape,
                color = AppColors.Primary,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .clickable(onClick = onEditClick)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Username
        Text(
            text = user.username,
            style = MaterialTheme.typography.headlineSmall,
            color = AppColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Level and Member Status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Level badge
            Surface(
                color = AppColors.Primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "LEVEL ${user.level}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.Primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            // Member status
            Text(
                text = user.memberStatus,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
    }
}

@Composable
fun StatsGridSection(stats: UserStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCardProfile(
            value = "${stats.totalChaptersRead / 1000}K",
            label = "CHAPTERS",
            modifier = Modifier.weight(1f)
        )
        StatCardProfile(
            value = stats.totalReadingTime.toString(),
            label = "STREAK",
            highlight = true,
            modifier = Modifier.weight(1f)
        )
        StatCardProfile(
            value = "${stats.totalReadingTime}h",
            label = "READING",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCardProfile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) AppColors.Surface else AppColors.Surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = if (highlight) AppColors.Primary else AppColors.TextPrimary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextSecondary
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = AppColors.TextMuted,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsMenuItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = iconTint.copy(alpha = 0.15f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = AppColors.TextMuted
        )
    }
}

@Composable
fun SettingsMenuItemWithSwitch(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = iconTint.copy(alpha = 0.15f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppColors.Primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = AppColors.SurfaceVariant
            )
        )
    }
}

@Composable
fun SettingsMenuItemWithAction(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    actionText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = iconTint.copy(alpha = 0.15f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
        
        TextButton(onClick = onClick) {
            Text(
                text = actionText,
                color = AppColors.Primary
            )
        }
    }
}

@Composable
fun SettingsMenuItemWithIcons(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    actionIcons: List<Pair<String, Color>>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = iconTint.copy(alpha = 0.15f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
        
        // Service icons
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            actionIcons.forEach { (text, color) ->
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = color
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
