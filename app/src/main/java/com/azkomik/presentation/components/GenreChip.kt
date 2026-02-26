package com.azkomik.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.azkomik.presentation.theme.AppColors

@Composable
fun GenreChip(
    genre: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) AppColors.Primary else AppColors.Surface,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, AppColors.Border) else null,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color.White else AppColors.TextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
