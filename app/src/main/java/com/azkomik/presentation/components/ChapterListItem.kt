package com.azkomik.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.azkomik.domain.model.Chapter
import com.azkomik.domain.model.Manga
import com.azkomik.presentation.theme.AppColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChapterListItem(
    chapter: Chapter,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    showNewBadge: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Chapter ${chapter.number.toInt()}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (chapter.isRead) AppColors.TextMuted else AppColors.TextPrimary
                )
                if (showNewBadge && !chapter.isRead) {
                    Spacer(modifier = Modifier.width(8.dp))
                    NewBadge()
                }
                if (chapter.isRead) {
                    Spacer(modifier = Modifier.width(8.dp))
                    ReadBadge()
                }
            }
            if (chapter.title.isNotBlank()) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDownloadClick) {
                Icon(
                    imageVector = if (chapter.isDownloaded)
                        Icons.Default.DownloadDone else Icons.Default.Download,
                    contentDescription = "Download",
                    tint = if (chapter.isDownloaded) AppColors.Secondary else AppColors.TextSecondary
                )
            }
        }
    }
}

@Composable
fun UpdateListItem(
    manga: Manga,
    chapter: Chapter,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val timeAgo = getTimeAgo(chapter.dateUpload)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            AsyncImage(
                model = manga.coverUrl,
                contentDescription = manga.title,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            // Unread indicator dot
            if (!chapter.isRead) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(AppColors.Unread, CircleShape)
                        .align(Alignment.TopStart)
                        .offset(x = (-4).dp, y = (-4).dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = manga.title,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Chapter ${chapter.number.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.Primary,
                maxLines = 1
            )
            Text(
                text = timeAgo,
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextMuted
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!chapter.isRead) {
                NewBadge()
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                ReadBadge()
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            IconButton(
                onClick = onMoreClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = AppColors.TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            IconButton(onClick = onDownloadClick) {
                Icon(
                    imageVector = if (chapter.isDownloaded)
                        Icons.Default.DownloadDone else Icons.Default.Download,
                    contentDescription = "Download",
                    tint = if (chapter.isDownloaded) AppColors.Secondary else AppColors.TextSecondary
                )
            }
        }
    }
}

@Composable
fun NewBadge() {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = AppColors.New
    ) {
        Text(
            text = "NEW",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ReadBadge() {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = AppColors.SurfaceLight
    ) {
        Text(
            text = "READ",
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.TextSecondary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)
    
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes mins ago"
        hours < 24 -> "$hours hours ago"
        days == 1L -> "Yesterday"
        days < 7 -> "$days days ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}
