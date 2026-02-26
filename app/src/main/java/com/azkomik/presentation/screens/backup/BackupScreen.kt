package com.azkomik.presentation.screens.backup

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azkomik.presentation.screens.backup.components.CreateBackupDialog
import com.azkomik.presentation.screens.backup.components.RestoreBackupDialog
import com.azkomik.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    onNavigateBack: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAutoBackupSettings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore", color = AppColors.TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success/Error messages
            uiState.successMessage?.let { message ->
                item {
                    SuccessBanner(message = message)
                }
            }
            
            uiState.error?.let { error ->
                item {
                    ErrorBanner(
                        message = error,
                        onDismiss = viewModel::clearError
                    )
                }
            }

            // Create Backup Card
            item {
                CreateBackupCard(
                    onCreateBackup = { viewModel.showCreateDialog() },
                    lastBackup = uiState.lastBackup
                )
            }

            // Restore Backup Card
            item {
                RestoreBackupCard(
                    onRestoreBackup = { viewModel.showRestoreDialog() }
                )
            }

            // Auto Backup Settings
            item {
                AutoBackupCard(
                    enabled = uiState.autoBackupEnabled,
                    frequency = uiState.autoBackupFrequency,
                    onToggle = { viewModel.toggleAutoBackup(it) },
                    onChangeFrequency = { viewModel.setAutoBackupFrequency(it) },
                    onExpandSettings = { showAutoBackupSettings = !showAutoBackupSettings }
                )
            }

            // Backup History
            if (uiState.backupHistory.isNotEmpty()) {
                item {
                    Text(
                        text = "BACKUP HISTORY",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(
                    items = uiState.backupHistory,
                    key = { it.uri }
                ) { backup ->
                    BackupHistoryItem(
                        backup = backup,
                        isLastBackup = backup.uri == uiState.lastBackup?.uri,
                        onDelete = { viewModel.deleteBackup(backup) },
                        onRestore = { viewModel.restoreBackup(backup.uri) }
                    )
                }
            }

            // Info Card
            item {
                BackupInfoCard()
            }
        }
    }

    // Create Backup Dialog
    if (uiState.showCreateDialog) {
        CreateBackupDialog(
            options = uiState.backupOptions,
            isLoading = uiState.isLoading,
            progress = uiState.backupProgress,
            onDismiss = viewModel::hideCreateDialog,
            onCreate = { location ->
                viewModel.createBackup(location)
            },
            onUpdateOptions = viewModel::updateBackupOptions
        )
    }

    // Restore Backup Dialog
    if (uiState.showRestoreDialog) {
        RestoreBackupDialog(
            isLoading = uiState.isLoading,
            progress = uiState.restoreProgress,
            onDismiss = viewModel::hideRestoreDialog,
            onRestore = { uri ->
                viewModel.restoreBackup(uri)
            }
        )
    }
}

@Composable
fun CreateBackupCard(
    onCreateBackup: () -> Unit,
    lastBackup: BackupInfo?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.Primary.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Create Backup",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TextPrimary
                    )
                    if (lastBackup != null) {
                        Text(
                            text = "Last backup: ${lastBackup.formattedDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    } else {
                        Text(
                            text = "No backup found",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextMuted
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onCreateBackup,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Backup Now")
            }
        }
    }
}

@Composable
fun RestoreBackupCard(
    onRestoreBackup: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Restore Backup",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "Restore from local backup file",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onRestoreBackup,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF3B82F6)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF3B82F6))
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FileOpen,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Backup File")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoBackupCard(
    enabled: Boolean,
    frequency: BackupFrequency,
    onToggle: (Boolean) -> Unit,
    onChangeFrequency: (BackupFrequency) -> Unit,
    onExpandSettings: () -> Unit
) {
    var showFrequencyDropdown by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF22C55E).copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "Auto Backup",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.TextPrimary
                        )
                        if (enabled) {
                            Text(
                                text = frequency.displayName(),
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.Secondary
                            )
                        }
                    }
                }
                
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppColors.Primary
                    )
                )
            }
            
            AnimatedVisibility(visible = enabled) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = AppColors.SurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Backup Frequency",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BackupFrequency.values().forEach { freq ->
                            FilterChip(
                                selected = frequency == freq,
                                onClick = { onChangeFrequency(freq) },
                                label = { Text(freq.displayName()) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AppColors.Primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackupHistoryItem(
    backup: BackupInfo,
    isLastBackup: Boolean,
    onDelete: () -> Unit,
    onRestore: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLastBackup) 
                AppColors.Primary.copy(alpha = 0.1f) else AppColors.Surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.InsertDriveFile,
                contentDescription = null,
                tint = AppColors.TextSecondary,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = backup.fileName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = backup.formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
                Text(
                    text = "${backup.fileSize} • ${backup.mangaCount} manga • ${backup.categoryCount} categories",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted
                )
            }
            
            Row {
                IconButton(onClick = onRestore) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Restore",
                        tint = Color(0xFF3B82F6)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = AppColors.Accent
                    )
                }
            }
        }
    }
}

@Composable
fun BackupInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "What is backed up?",
                style = MaterialTheme.typography.titleSmall,
                color = AppColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val items = listOf(
                "Library (favorites, categories)",
                "Chapter read status",
                "Tracking settings and credentials",
                "Reading history",
                "App settings and preferences"
            )
            
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AppColors.Secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Note: Downloaded chapters are not included in backup. Only reading progress is saved.",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextMuted
            )
        }
    }
}

@Composable
fun SuccessBanner(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = AppColors.Secondary.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AppColors.Secondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.Secondary
            )
        }
    }
}

@Composable
fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = AppColors.Accent.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = AppColors.Accent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.Accent
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = AppColors.Accent
                )
            }
        }
    }
}
