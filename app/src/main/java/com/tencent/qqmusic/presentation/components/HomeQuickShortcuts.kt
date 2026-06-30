package com.tencent.qqmusic.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tencent.qqmusic.R

@Immutable
data class ShortcutItem(
    val titleResId: Int,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun HomeQuickShortcuts(
    modifier: Modifier = Modifier,
    onNavigateToLibraryTab: (Int) -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToRecentlyPlayed: () -> Unit,
    onNavigateToEqualizer: () -> Unit
) {
    val items = remember {
        listOf(
            ShortcutItem(
                titleResId = R.string.library_tab_liked,
                icon = Icons.Rounded.Favorite,
                onClick = { onNavigateToLibraryTab(5) } // Liked / Favorites index is 5
            ),
            ShortcutItem(
                titleResId = R.string.library_tab_folders,
                icon = Icons.Rounded.Folder,
                onClick = { onNavigateToLibraryTab(4) } // Folders index is 4
            ),
            ShortcutItem(
                titleResId = R.string.stats_title,
                icon = Icons.Rounded.BarChart,
                onClick = onNavigateToStats
            ),
            ShortcutItem(
                titleResId = R.string.recently_played_title,
                icon = Icons.Rounded.History,
                onClick = onNavigateToRecentlyPlayed
            ),
            ShortcutItem(
                titleResId = R.string.equalizer_title,
                icon = Icons.Rounded.Tune,
                onClick = onNavigateToEqualizer
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { item ->
                ShortcutItemCard(item = item)
            }
        }
    }
}

@Composable
private fun ShortcutItemCard(item: ShortcutItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable(
                onClick = item.onClick,
                interactionSource = null,
                indication = null // Simple, responsive feedback handles custom styling
            )
    ) {
        val glassBrush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f)
            )
        )
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(glassBrush)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), CircleShape)
                .clickable { item.onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = stringResource(item.titleResId),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(item.titleResId),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
