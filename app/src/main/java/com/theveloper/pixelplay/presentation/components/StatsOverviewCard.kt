package com.theveloper.pixelplay.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import android.text.format.DateFormat as AndroidDateFormat
import com.theveloper.pixelplay.R
import com.theveloper.pixelplay.data.stats.PlaybackStatsRepository
import com.theveloper.pixelplay.data.stats.StatsTimeRange
import com.theveloper.pixelplay.presentation.stats.displayNameRes
import com.theveloper.pixelplay.utils.formatListeningDurationCompact
import com.theveloper.pixelplay.utils.formatListeningDurationLong
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsOverviewCard(
    modifier: Modifier = Modifier,
    summary: PlaybackStatsRepository.PlaybackStatsSummary?,
    onClick: () -> Unit
) {
    val cornerRadius = 24.dp
    val shape = AbsoluteSmoothCornerShape(
        cornerRadiusTL = cornerRadius,
        smoothnessAsPercentTR = 60,
        cornerRadiusBR = cornerRadius,
        smoothnessAsPercentTL = 60,
        cornerRadiusBL = cornerRadius,
        smoothnessAsPercentBR = 60,
        cornerRadiusTR = cornerRadius,
        smoothnessAsPercentBL = 60,
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.home_stats_overview_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource((summary?.range ?: StatsTimeRange.WEEK).displayNameRes()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Crossfade(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                targetState = summary
            ) { currentSummary ->
                if (currentSummary == null) {
                    PlaceholderOverviewContent()
                } else {
                    OverviewContent(currentSummary)
                }
            }
        }
    }
}

@Composable
private fun OverviewContent(summary: PlaybackStatsRepository.PlaybackStatsSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = formatListeningDurationLong(summary.totalDurationMs),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_stats_overview_total_plays),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = summary.totalPlayCount.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_stats_overview_avg_per_day),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatListeningDurationCompact(summary.averageDailyDurationMs),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        val topTrack = summary.topSongs.firstOrNull()
        if (topTrack != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.25f))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_stats_overview_top_track),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = topTrack.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.home_stats_overview_top_track_line,
                        topTrack.artist,
                        topTrack.playCount
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        MiniListeningTimeline(summary, AndroidDateFormat.is24HourFormat(LocalContext.current))
    }
}

@Composable
private fun PlaceholderOverviewContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PlaceholderLine(width = 140.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            PlaceholderLine(width = 60.dp)
            PlaceholderLine(width = 60.dp)
        }
        PlaceholderLine(width = 120.dp)
        MiniListeningTimeline(null, AndroidDateFormat.is24HourFormat(LocalContext.current))
    }
}

@Composable
private fun MiniListeningTimeline(
    summary: PlaybackStatsRepository.PlaybackStatsSummary?,
    use24Hour: Boolean = false
) {
    val timeline = summary?.timeline ?: emptyList()
    val range = summary?.range ?: StatsTimeRange.WEEK
    if (summary?.range == StatsTimeRange.MONTH && timeline.isNotEmpty()) {
        MonthlyHorizontalListeningTimeline(timeline)
        return
    }
    val maxDuration = timeline.maxOfOrNull { it.totalDurationMs }?.takeIf { it > 0 } ?: 1L
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val entries = if (timeline.isEmpty()) {
            List(5) { null }
        } else {
            timeline.takeLast(minOf(7, timeline.size))
        }
        entries.forEach { entry ->
            val heightFraction = entry?.let { it.totalDurationMs.toFloat() / maxDuration.toFloat() }?.coerceIn(0f, 1f) ?: 0.1f
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((75.dp * heightFraction).coerceAtLeast(10.dp))
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = convertHourLabel(entry?.label ?: "", range, use24Hour),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MonthlyHorizontalListeningTimeline(
    timeline: List<PlaybackStatsRepository.TimelineEntry>
) {
    val maxDuration = timeline.maxOfOrNull { it.totalDurationMs }?.takeIf { it > 0 } ?: 1L
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        timeline.forEach { entry ->
            val widthFraction = (entry.totalDurationMs.toFloat() / maxDuration.toFloat())
                .coerceIn(0f, 1f)
                .takeIf { it > 0f }
                ?: 0.06f
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(56.dp),
                    text = entry.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(widthFraction)
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderLine(width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(18.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    )
}

private fun convertHourLabel(label: String, range: StatsTimeRange, use24Hour: Boolean): String {
    if (label.isBlank()) return label
    if (range != StatsTimeRange.DAY) return label

    // "7 AM", "7am", "7:00 AM", "7:00am" etc.
    val amPmMatch = Regex("(?i)^(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)$").matchEntire(label.trim())
    if (amPmMatch != null) {
        val hour12 = amPmMatch.groupValues[1].toIntOrNull() ?: return label
        val isPm = amPmMatch.groupValues[3].equals("pm", ignoreCase = true)
        val hour24 = when {
            isPm && hour12 != 12 -> hour12 + 12
            !isPm && hour12 == 12 -> 0
            else -> hour12
        }
        return if (use24Hour) {
            String.format(java.util.Locale.getDefault(), "%02d:00", hour24)
        } else {
            val time = java.time.LocalTime.of(hour24, 0)
            val formatter = java.time.format.DateTimeFormatter.ofPattern("h a", java.util.Locale.getDefault())
            time.format(formatter)
        }
    }

    // Already "HH:MM" 24h
    val h24Match = Regex("^(\\d{1,2}):(\\d{2})$").matchEntire(label.trim())
    if (h24Match != null) {
        val hour24 = h24Match.groupValues[1].toIntOrNull() ?: return label
        return if (use24Hour) {
            String.format(java.util.Locale.getDefault(), "%02d:00", hour24)
        } else {
            val time = java.time.LocalTime.of(hour24, 0)
            val formatter = java.time.format.DateTimeFormatter.ofPattern("h a", java.util.Locale.getDefault())
            time.format(formatter)
        }
    }

    // Bare integer "7" or "19"
    val bareHour = label.trim().toIntOrNull()
    if (bareHour != null && bareHour in 0..23) {
        return if (use24Hour) {
            String.format(java.util.Locale.getDefault(), "%02d:00", bareHour)
        } else {
            val time = java.time.LocalTime.of(bareHour, 0)
            val formatter = java.time.format.DateTimeFormatter.ofPattern("h a", java.util.Locale.getDefault())
            time.format(formatter)
        }
    }

    return label
}
