package com.azkomik.presentation.screens.backup.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.azkomik.presentation.screens.backup.BackupOptions
import com.azkomik.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBackupDialog(
    options: BackupOptions,
    isLoading: Boolean,
    progress: Float,
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
    onUpdateOptions: (BackupOptions) -> Unit
) {
    var selectedLocation by remember { mutableStateOf("local") }
    
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Create Backup") },
        text = {
            Column {
                if (isLoading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(
                            progress = progress,
                            color = AppColors.Primary,
                            trackColor = AppColors.SurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            text = "Creating backup...",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    Text(
                        text = "Select what to include:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    BackupOptionItem(
                        title = "Library",
                        description = "Favorites and categories",
                        checked = options.includeLibrary,
                        onCheckedChange = {
                            onUpdateOptions(options.copy(includeLibrary = it))
                        }
                    )
                    
                    BackupOptionItem(
                        title = "Chapters",
                        description = "Read and bookmark status",
                        checked = options.includeChapters,
                        onCheckedChange = {
                            onUpdateOptions(options.copy(includeChapters = it))
                        }
                    )
                    
                    BackupOptionItem(
                        title = "Tracking",
                        description = "Tracking services data",
                        checked = options.includeTracking,
                        onCheckedChange = {
                            onUpdateOptions(options.copy(includeTracking = it))
                        }
                    )
                    
                    BackupOptionItem(
                        title = "History",
                        description = "Reading history",
                        checked = options.includeHistory,
                        onCheckedChange = {
                            onUpdateOptions(options.copy(includeHistory = it))
                        }
                    )
                    
                    BackupOptionItem(
                        title = "Settings",
                        description = "App preferences",
                        checked = options.includeSettings,
                        onCheckedChange = {
                            onUpdateOptions(options.copy(includeSettings = it))
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Save location:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LocationChip(
                            label = "Local Storage",
                            selected = selectedLocation == "local",
                            onClick = { selectedLocation = "local" }
                        )
                        LocationChip(
                            label = "Google Drive",
                            selected = selectedLocation == "drive",
                            onClick = { selectedLocation = "drive" }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate("content://backups/") },
                enabled = !isLoading && (options.includeLibrary || options.includeChapters || 
                    options.includeTracking || options.includeHistory || options.includeSettings),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        },
        containerColor = AppColors.Surface
    )
}

@Composable
fun RestoreBackupDialog(
    isLoading: Boolean,
    progress: Float,
    onDismiss: () -> Unit,
    onRestore: (String) -> Unit
) {
    var selectedFile by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Restore Backup") },
        text = {
            Column {
                if (isLoading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(
                            progress = progress,
                            color = Color(0xFF3B82F6),
                            trackColor = AppColors.SurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            text = "Restoring backup...",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Don't close the app",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextMuted
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AppColors.Accent,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Warning",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.Accent,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Restoring a backup will replace your current library, settings, and tracking data. This action cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = { selectedFile = "content://backup_file.proto.gz" },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileOpen,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedFile == null) "Select backup file" else "Change file")
                    }
                    
                    if (selectedFile != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AppColors.SurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.InsertDriveFile,
                                    contentDescription = null,
                                    tint = AppColors.TextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "backup_file.proto.gz",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedFile?.let { onRestore(it) } },
                enabled = !isLoading && selectedFile != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                )
            ) {
                Text("Restore")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        },
        containerColor = AppColors.Surface
    )
}

@Composable
fun BackupOptionItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = AppColors.Primary)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AppColors.Primary,
            selectedLabelColor = Color.White
        )
    )
}
