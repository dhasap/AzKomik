package com.azkomik.presentation.screens.migration

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.azkomik.domain.model.Manga
import com.azkomik.domain.model.migration.*
import com.azkomik.domain.model.source.Source
import com.azkomik.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MigrationScreen(
    onNavigateBack: () -> Unit,
    viewModel: MigrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MigrationTopBar(
                currentStep = uiState.currentStep,
                onNavigateBack = if (uiState.currentStep == MigrationStep.SELECT_SOURCE) {
                    onNavigateBack
                } else {
                    { viewModel.goBack() }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState.currentStep) {
                MigrationStep.SELECT_SOURCE -> SelectSourceStep(
                    sources = uiState.availableSources,
                    onSelectSource = viewModel::selectSource
                )
                
                MigrationStep.SELECT_MANGA -> SelectMangaStep(
                    manga = uiState.mangaToMigrate,
                    selectedManga = uiState.selectedManga,
                    source = uiState.selectedSource,
                    onToggleSelection = viewModel::toggleMangaSelection,
                    onSelectAll = viewModel::selectAllManga,
                    onDeselectAll = viewModel::deselectAllManga,
                    onProceed = viewModel::proceedToTargetSelection
                )
                
                MigrationStep.SEARCH_TARGET -> SelectTargetStep(
                    sources = uiState.availableSources.filter { it.id != uiState.selectedSource?.id },
                    onSelectTarget = viewModel::selectTargetSource
                )
                
                MigrationStep.CONFIRM_MIGRATION -> ConfirmMigrationStep(
                    migrationItems = uiState.migrationItems,
                    onSearchAlternatives = viewModel::searchAlternatives,
                    onSelectAlternative = viewModel::selectAlternative,
                    onSkip = viewModel::skipMigration,
                    onConfirm = viewModel::confirmAndMigrate
                )
                
                MigrationStep.MIGRATING -> MigratingStep(
                    migrationItems = uiState.migrationItems,
                    completedCount = uiState.completedCount,
                    totalCount = uiState.totalCount
                )
                
                MigrationStep.COMPLETED -> CompletedStep(
                    successCount = uiState.completedCount,
                    totalCount = uiState.totalCount,
                    onFinish = onNavigateBack,
                    onMigrateMore = viewModel::resetMigration
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MigrationTopBar(
    currentStep: MigrationStep,
    onNavigateBack: () -> Unit
) {
    val title = when (currentStep) {
        MigrationStep.SELECT_SOURCE -> "Migration"
        MigrationStep.SELECT_MANGA -> "Select Manga"
        MigrationStep.SEARCH_TARGET -> "Select Target Source"
        MigrationStep.CONFIRM_MIGRATION -> "Confirm Migration"
        MigrationStep.MIGRATING -> "Migrating..."
        MigrationStep.COMPLETED -> "Migration Complete"
    }

    TopAppBar(
        title = { Text(title, color = AppColors.TextPrimary) },
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

@Composable
fun SelectSourceStep(
    sources: List<Source>,
    onSelectSource: (Source) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select source to migrate from",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Choose the source that is no longer working or has issues",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sources) { source ->
                SourceCard(
                    source = source,
                    onClick = { onSelectSource(source) },
                    showMangaCount = true
                )
            }
        }
    }
}

@Composable
fun SourceCard(
    source: Source,
    onClick: () -> Unit,
    showMangaCount: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = AppColors.SurfaceVariant,
                modifier = Modifier.size(56.dp)
            ) {
                AsyncImage(
                    model = source.iconUrl,
                    contentDescription = source.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = source.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = "${source.flagEmoji} ${source.langDisplay}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
                if (showMangaCount) {
                    Text(
                        text = "${source.mangaCount} manga in library",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AppColors.TextMuted
            )
        }
    }
}

@Composable
fun SelectMangaStep(
    manga: List<Manga>,
    selectedManga: List<Manga>,
    source: Source?,
    onToggleSelection: (Manga) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${selectedManga.size} of ${manga.size} selected",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextPrimary
            )
            
            Row {
                TextButton(onClick = onSelectAll) {
                    Text("Select All")
                }
                TextButton(onClick = onDeselectAll) {
                    Text("Deselect")
                }
            }
        }
        
        // Manga list
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(manga) { item ->
                MangaSelectCard(
                    manga = item,
                    isSelected = selectedManga.contains(item),
                    onToggle = { onToggleSelection(item) }
                )
            }
        }
        
        // Bottom button
        Surface(
            color = AppColors.Surface,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = onProceed,
                enabled = selectedManga.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    disabledContainerColor = AppColors.SurfaceVariant
                )
            ) {
                Text("Select Target Source (${selectedManga.size})")
            }
        }
    }
}

@Composable
fun MangaSelectCard(
    manga: Manga,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                AppColors.Primary.copy(alpha = 0.1f) else AppColors.Surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) {
            BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.3f))
        } else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = AppColors.Primary)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            AsyncImage(
                model = manga.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = manga.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = manga.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}

@Composable
fun SelectTargetStep(
    sources: List<Source>,
    onSelectTarget: (Source) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select target source",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Choose a new source to migrate your manga to",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sources) { source ->
                SourceCard(
                    source = source,
                    onClick = { onSelectTarget(source) },
                    showMangaCount = true
                )
            }
        }
    }
}

@Composable
fun ConfirmMigrationStep(
    migrationItems: List<MigrationItem>,
    onSearchAlternatives: (MigrationItem) -> Unit,
    onSelectAlternative: (MigrationItem, Manga) -> Unit,
    onSkip: (MigrationItem) -> Unit,
    onConfirm: () -> Unit
) {
    val allReady = migrationItems.all { 
        it.status == MigrationStatus.COMPLETED || 
        it.selectedMatch != null ||
        it.status == MigrationStatus.CANCELLED
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Review migration",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(16.dp)
        )
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(migrationItems) { item ->
                MigrationItemCard(
                    item = item,
                    onSearch = { onSearchAlternatives(item) },
                    onSelectAlternative = { manga ->
                        onSelectAlternative(item, manga)
                    },
                    onSkip = { onSkip(item) }
                )
            }
        }
        
        // Bottom button
        Surface(
            color = AppColors.Surface,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = onConfirm,
                enabled = allReady,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    disabledContainerColor = AppColors.SurfaceVariant
                )
            ) {
                Text("Start Migration")
            }
        }
    }
}

@Composable
fun MigrationItemCard(
    item: MigrationItem,
    onSearch: () -> Unit,
    onSelectAlternative: (Manga) -> Unit,
    onSkip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Original manga info
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = item.manga.coverUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.manga.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "From: ${item.currentSource.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Status or search results
            when (item.status) {
                MigrationStatus.PENDING -> {
                    OutlinedButton(
                        onClick = onSearch,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppColors.Primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search in ${item.targetSource?.name}")
                    }
                }
                MigrationStatus.SEARCHING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = AppColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Searching...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextSecondary
                        )
                    }
                }
                MigrationStatus.CHOOSING -> {
                    Text(
                        text = "Select match:",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    item.foundManga.forEach { manga ->
                        val isSelected = item.selectedMatch?.id == manga.id
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSelectAlternative(manga) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) 
                                AppColors.Primary.copy(alpha = 0.2f) else AppColors.SurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = manga.coverUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = manga.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AppColors.TextPrimary
                                    )
                                    Text(
                                        text = manga.author,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppColors.TextSecondary
                                    )
                                }
                                
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AppColors.Primary
                                    )
                                }
                            }
                        }
                    }
                    
                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Skip this manga")
                    }
                }
                MigrationStatus.CANCELLED -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = AppColors.TextMuted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Skipped",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextMuted
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun MigratingStep(
    migrationItems: List<MigrationItem>,
    completedCount: Int,
    totalCount: Int
) {
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.size(120.dp),
            strokeWidth = 8.dp,
            color = AppColors.Primary,
            trackColor = AppColors.SurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.displayMedium,
            color = AppColors.TextPrimary
        )
        
        Text(
            text = "$completedCount of $totalCount manga migrated",
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.TextSecondary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Current status list
        LazyColumn(
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            items(migrationItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (item.status) {
                        MigrationStatus.COMPLETED -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AppColors.Secondary
                            )
                        }
                        MigrationStatus.MIGRATING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        MigrationStatus.ERROR -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = AppColors.Accent
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AppColors.TextMuted
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = item.manga.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun CompletedStep(
    successCount: Int,
    totalCount: Int,
    onFinish: () -> Unit,
    onMigrateMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = AppColors.Secondary,
            modifier = Modifier.size(100.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Migration Complete!",
            style = MaterialTheme.typography.headlineMedium,
            color = AppColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "$successCount of $totalCount manga successfully migrated",
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.TextSecondary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary
            )
        ) {
            Text("Finish")
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = onMigrateMore,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Migrate More")
        }
    }
}
