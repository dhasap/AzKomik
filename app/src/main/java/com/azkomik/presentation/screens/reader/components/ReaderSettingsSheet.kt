package com.azkomik.presentation.screens.reader.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.azkomik.domain.model.reader.*
import com.azkomik.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    settings: ReaderSettings,
    currentFilter: PageFilterType,
    currentSort: PageSortType,
    onDismiss: () -> Unit,
    onReadingModeChange: (ReadingMode) -> Unit,
    onScaleTypeChange: (ScaleType) -> Unit,
    onNavigationModeChange: (NavigationMode) -> Unit,
    onToggleCropBorders: () -> Unit,
    onToggleWebtoonCrop: () -> Unit,
    onTogglePageTransitions: () -> Unit,
    onToggleDualPage: () -> Unit,
    onFilterChange: (PageFilterType) -> Unit,
    onSortChange: (PageSortType) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = AppColors.Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Reader Settings",
                style = MaterialTheme.typography.headlineSmall,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Reading Mode Section
            SectionTitle("Reading Mode")
            ReadingModeSelector(
                currentMode = settings.readingMode,
                onModeChange = onReadingModeChange
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scale Type
            SectionTitle("Scale Type")
            ScaleTypeSelector(
                currentType = settings.scaleType,
                onTypeChange = onScaleTypeChange
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigation Mode
            SectionTitle("Navigation")
            NavigationModeSelector(
                currentMode = settings.navigationMode,
                onModeChange = onNavigationModeChange
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filters
            SectionTitle("Page Filter")
            PageFilterSelector(
                currentFilter = currentFilter,
                onFilterChange = onFilterChange
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sort
            SectionTitle("Sort Order")
            PageSortSelector(
                currentSort = currentSort,
                onSortChange = onSortChange
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Toggles
            SectionTitle("Display")
            ToggleItem(
                title = "Crop Borders",
                checked = settings.cropBorders,
                onCheckedChange = onToggleCropBorders
            )
            ToggleItem(
                title = "Webtoon Crop Borders",
                checked = settings.webtoonCropBorders,
                onCheckedChange = onToggleWebtoonCrop
            )
            ToggleItem(
                title = "Page Transitions",
                checked = settings.pageTransitions,
                onCheckedChange = onTogglePageTransitions
            )
            ToggleItem(
                title = "Dual Page Split",
                checked = settings.dualPageSplit,
                onCheckedChange = onToggleDualPage
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = AppColors.TextMuted,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingModeSelector(
    currentMode: ReadingMode,
    onModeChange: (ReadingMode) -> Unit
) {
    val modes = listOf(
        ReadingMode.PAGED_LEFT_TO_RIGHT to ("LTR" to Icons.Default.NavigateNext),
        ReadingMode.PAGED_RIGHT_TO_LEFT to ("RTL" to Icons.Default.NavigateBefore),
        ReadingMode.VERTICAL_SCROLL to ("Vertical" to Icons.Default.SwapVert),
        ReadingMode.WEBTOON to ("Webtoon" to Icons.Default.ViewAgenda),
        ReadingMode.CONTINUOUS_VERTICAL to ("Continuous" to Icons.Default.ViewDay)
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        modes.forEach { (mode, labelIcon) ->
            val (label, icon) = labelIcon
            val selected = currentMode == mode
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onModeChange(mode) },
                shape = RoundedCornerShape(12.dp),
                color = if (selected) AppColors.Primary else AppColors.SurfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) Color.White else AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) Color.White else AppColors.TextSecondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaleTypeSelector(
    currentType: ScaleType,
    onTypeChange: (ScaleType) -> Unit
) {
    val types = listOf(
        ScaleType.FIT_SCREEN to "Fit Screen",
        ScaleType.STRETCH to "Stretch",
        ScaleType.FIT_WIDTH to "Fit Width",
        ScaleType.FIT_HEIGHT to "Fit Height",
        ScaleType.SMART_FIT to "Smart Fit"
    )
    
    Column {
        types.forEach { (type, label) ->
            val selected = currentType == type
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTypeChange(type) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected,
                    onClick = { onTypeChange(type) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = AppColors.Primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationModeSelector(
    currentMode: NavigationMode,
    onModeChange: (NavigationMode) -> Unit
) {
    val modes = listOf(
        NavigationMode.L_SHAPE to "L-Shaped",
        NavigationMode.EDGE to "Edge",
        NavigationMode.KINDLE to "Kindle",
        NavigationMode.DISABLED to "Disabled"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        modes.forEach { (mode, label) ->
            val selected = currentMode == mode
            
            FilterChip(
                selected = selected,
                onClick = { onModeChange(mode) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageFilterSelector(
    currentFilter: PageFilterType,
    onFilterChange: (PageFilterType) -> Unit
) {
    val filters = PageFilterType.values()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val selected = currentFilter == filter
            
            FilterChip(
                selected = selected,
                onClick = { onFilterChange(filter) },
                label = { Text(filter.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageSortSelector(
    currentSort: PageSortType,
    onSortChange: (PageSortType) -> Unit
) {
    val sorts = PageSortType.values()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sorts.forEach { sort ->
            val selected = currentSort == sort
            
            FilterChip(
                selected = selected,
                onClick = { onSortChange(sort) },
                label = { Text(sort.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppColors.Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun ToggleItem(
    title: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCheckedChange)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextPrimary
        )
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppColors.Primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = AppColors.SurfaceVariant
            )
        )
    }
}
