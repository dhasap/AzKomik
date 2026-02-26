package com.azkomik.presentation.screens.reader.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.azkomik.domain.model.Chapter
import com.azkomik.presentation.theme.AppColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterListSheet(
    chapters: List<Chapter>,
    currentChapterId: String,
    onDismiss: () -> Unit,
    onChapterClick: (String) -> Unit
) {
    // Mock chapters for demo
    val mockChapters = remember {
        (179 downTo 150).mapIndexed { index, number ->
            Chapter(
                id = "chapter_$number",
                mangaId = "manga_1",
                number = number.toFloat(),
                title = when (number) {
                    179 -> "The Final Battle - Part 2"
                    178 -> "The Final Battle - Part 1"
                    177 -> "Preparing for War"
                    176 -> "Prologue"
                    175 -> "Aftermath"
                    174 -> "Dragon King's Fury"
                    else -> "Chapter $number"
                },
                dateUpload = System.currentTimeMillis() - (index * 86400000L),
                isRead = index >= 3,
                isBookmarked = index == 1,
                pageCount = 45
            )
        }
    }
    
    var sortDescending by remember { mutableStateOf(true) }
    val sortedChapters = remember(mockChapters, sortDescending) {
        if (sortDescending) mockChapters else mockChapters.reversed()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = AppColors.Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${mockChapters.size} Chapters",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary
                )
                Row {
                    IconButton(onClick = { sortDescending = !sortDescending }) {
                        Icon(
                            imageVector = if (sortDescending) 
                                Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = "Sort",
                            tint = AppColors.TextPrimary
                        )
                    }
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = AppColors.TextPrimary
                        )
                    }
                }
            }
            
            Divider(color = AppColors.SurfaceVariant)
            
            // Chapter list
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sortedChapters, key = { it.id }) { chapter ->
                    ChapterListItemReader(
                        chapter = chapter,
                        isCurrent = chapter.id == currentChapterId,
                        onClick = { onChapterClick(chapter.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterListItemReader(
    chapter: Chapter,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isCurrent) AppColors.Primary.copy(alpha = 0.1f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Read indicator
            if (chapter.isRead) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Read",
                    tint = AppColors.Secondary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Box(
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Chapter ${chapter.number.toInt()}${if (chapter.title.isNotBlank()) " - ${chapter.title}" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrent) AppColors.Primary else AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row {
                    Text(
                        text = dateFormat.format(Date(chapter.dateUpload)),
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextMuted
                    )
                    if (chapter.pageCount > 0) {
                        Text(
                            text = " • ${chapter.pageCount} pages",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppColors.TextMuted
                        )
                    }
                }
            }
            
            // Bookmark indicator
            if (chapter.isBookmarked) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Bookmarked",
                    tint = AppColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            if (isCurrent) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = AppColors.Primary
                ) {
                    Text(
                        text = "CURRENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
