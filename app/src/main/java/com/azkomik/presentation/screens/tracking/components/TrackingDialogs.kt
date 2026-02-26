package com.azkomik.presentation.screens.tracking.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.azkomik.domain.model.tracking.*
import com.azkomik.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingLoginDialog(
    service: TrackingService,
    onDismiss: () -> Unit,
    onLogin: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var showToken by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = service.iconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Login to ${service.name}")
            }
        },
        text = {
            Column {
                Text(
                    text = "Enter your credentials to connect ${service.name}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username or Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("API Token / Password") },
                    singleLine = true,
                    visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        IconButton(onClick = { showToken = !showToken }) {
                            Icon(
                                imageVector = if (showToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showToken) "Hide token" else "Show token"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (service.id == TrackingServices.MYANIMELIST) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note: MyAnimeList requires a personal API token. Get one from your account settings.",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isLoading = true
                    onLogin(username, token)
                },
                enabled = username.isNotBlank() && token.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Login")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = AppColors.Surface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingSearchDialog(
    service: TrackingService,
    searchResults: List<TrackSearchResult>,
    isSearching: Boolean,
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit,
    onSelectResult: (TrackSearchResult) -> Unit
) {
    var query by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Search on ${service.name}")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { 
                        query = it
                        if (it.length >= 3) {
                            onSearch(it)
                        }
                    },
                    label = { Text("Manga title") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (searchResults.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { result ->
                            SearchResultItem(
                                result = result,
                                onClick = { onSelectResult(result) }
                            )
                        }
                    }
                } else if (query.length >= 3 && !isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextMuted
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = AppColors.Surface
    )
}

@Composable
fun SearchResultItem(
    result: TrackSearchResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = result.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${result.publishingType} - ${result.publishingStatus}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
                Text(
                    text = "${result.totalChapters} chapters",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted
                )
            }
        }
    }
}

@Composable
fun TrackingSettingsDialog(
    preferences: TrackerPreferences,
    onDismiss: () -> Unit,
    onUpdatePreferences: (TrackerPreferences) -> Unit
) {
    var autoTrack by remember { mutableStateOf(preferences.autoTrack) }
    var autoUpdateProgress by remember { mutableStateOf(preferences.autoUpdateProgress) }
    var updateChaptersRead by remember { mutableStateOf(preferences.updateChaptersRead) }
    var updateStatus by remember { mutableStateOf(preferences.updateStatus) }
    var updateScore by remember { mutableStateOf(preferences.updateScore) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tracking Settings") },
        text = {
            Column {
                ToggleSetting(
                    title = "Auto track new manga",
                    description = "Automatically search and add tracking when adding manga to library",
                    checked = autoTrack,
                    onCheckedChange = { autoTrack = it }
                )
                
                Divider(color = AppColors.SurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
                
                ToggleSetting(
                    title = "Auto update progress",
                    description = "Automatically update chapter progress when reading",
                    checked = autoUpdateProgress,
                    onCheckedChange = { autoUpdateProgress = it }
                )
                
                if (autoUpdateProgress) {
                    Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                        CheckboxSetting(
                            title = "Update chapters read",
                            checked = updateChaptersRead,
                            onCheckedChange = { updateChaptersRead = it }
                        )
                        CheckboxSetting(
                            title = "Update status to Reading",
                            checked = updateStatus,
                            onCheckedChange = { updateStatus = it }
                        )
                        CheckboxSetting(
                            title = "Update score",
                            checked = updateScore,
                            onCheckedChange = { updateScore = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpdatePreferences(
                        TrackerPreferences(
                            autoTrack = autoTrack,
                            autoUpdateProgress = autoUpdateProgress,
                            updateChaptersRead = updateChaptersRead,
                            updateStatus = updateStatus,
                            updateScore = updateScore
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = AppColors.Surface
    )
}

@Composable
fun TrackEditDialog(
    track: Track,
    service: TrackingService,
    onDismiss: () -> Unit,
    onSave: (Float, Int?, Double?) -> Unit
) {
    var chaptersRead by remember { mutableStateOf(track.lastChapterRead.toString()) }
    var selectedStatus by remember { mutableStateOf(track.status) }
    var score by remember { mutableStateOf(if (track.score > 0) track.score.toString() else "") }
    
    val statusOptions = listOf(
        TrackStatus.READING to "Reading",
        TrackStatus.COMPLETED to "Completed",
        TrackStatus.ON_HOLD to "On Hold",
        TrackStatus.DROPPED to "Dropped",
        TrackStatus.PLAN_TO_READ to "Plan to Read",
        TrackStatus.REPEATING to "Re-reading"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Progress") },
        text = {
            Column {
                OutlinedTextField(
                    value = chaptersRead,
                    onValueChange = { chaptersRead = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Chapters read / ${track.totalChapters}") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = statusOptions.find { it.first == selectedStatus }?.second ?: "Reading",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOptions.forEach { (status, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedStatus = status
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                if (service.supportsScore) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = score,
                        onValueChange = { 
                            val filtered = it.filter { c -> c.isDigit() || c == '.' }
                            score = when (service.scoreFormat) {
                                ScoreFormat.POINT_10 -> filtered.toFloatOrNull()?.coerceIn(0f, 10f)?.toString() ?: ""
                                ScoreFormat.POINT_100 -> filtered.toFloatOrNull()?.coerceIn(0f, 100f)?.toString() ?: ""
                                ScoreFormat.POINT_5 -> filtered.toFloatOrNull()?.coerceIn(0f, 5f)?.toString() ?: ""
                                else -> filtered
                            }
                        },
                        label = { 
                            val rangeText = when (service.scoreFormat) {
                                ScoreFormat.POINT_10 -> "0-10"
                                ScoreFormat.POINT_100 -> "0-100"
                                ScoreFormat.POINT_5 -> "0-5"
                                else -> "0-10"
                            }
                            Text("Score ($rangeText)") 
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        chaptersRead.toFloatOrNull() ?: track.lastChapterRead,
                        selectedStatus,
                        score.toDoubleOrNull() ?: track.score
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = AppColors.Surface
    )
}

@Composable
fun ToggleSetting(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppColors.Primary
            )
        )
    }
}

@Composable
fun CheckboxSetting(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = AppColors.Primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextPrimary
        )
    }
}
