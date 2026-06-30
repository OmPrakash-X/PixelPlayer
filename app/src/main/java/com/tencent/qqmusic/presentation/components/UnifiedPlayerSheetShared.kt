@file:kotlin.OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package com.tencent.qqmusic.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.size.Size
import com.tencent.qqmusic.data.model.Song
import com.tencent.qqmusic.ui.theme.GoogleSansRounded

internal val LocalMaterialTheme = compositionLocalOf<ColorScheme> { error("No ColorScheme provided") }

val MiniPlayerHeight = 64.dp
const val ANIMATION_DURATION_MS = 255
val MiniPlayerBottomSpacer = 8.dp

@Composable
fun getNavigationBarHeight(): Dp {
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    return sanitizeNavigationBarBottomInset(insets.calculateBottomPadding())
}

@Composable
internal fun MiniPlayerContentInternal(
    song: Song,
    isPlaying: Boolean,
    isCastConnecting: Boolean,
    isPreparingPlayback: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    canScroll: Boolean = true,
    progressFraction: Float = 0f
) {
    val hapticFeedback = LocalHapticFeedback.current
    val controlsEnabled = !isCastConnecting && !isPreparingPlayback

    val playPauseInteraction = remember { MutableInteractionSource() }
    val miniPlayerIndication = remember { ripple(bounded = false) }

    val primaryColor = LocalMaterialTheme.current.primary
    val containerColor = LocalMaterialTheme.current.primaryContainer

    // Pill container: two background layers + content on top
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MiniPlayerHeight)
    ) {
        // Layer 1: full dim background track
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(containerColor)
        )
        // Layer 2: progress fill — grows left-to-right as song plays
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progressFraction.coerceIn(0f, 1f))
                .background(primaryColor.copy(alpha = 0.28f))
        )

        // Layer 3: content row sits on top of the two fill layers
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val albumArtModel = song.albumArtUriString?.takeIf { it.isNotBlank() }
            Box(contentAlignment = Alignment.Center) {
                key(song.id) {
                    SmartImage(
                        model = albumArtModel,
                        contentDescription = "Album art for ${song.title}",
                        shape = CircleShape,
                        targetSize = Size(150, 150),
                        modifier = Modifier.size(44.dp),
                        placeholderModel = if (albumArtModel?.startsWith("telegram_art") == true) {
                            "$albumArtModel?quality=thumb"
                        } else null
                    )
                }
                if (isCastConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = LocalMaterialTheme.current.onPrimaryContainer
                    )
                } else if (isPreparingPlayback) {
                    CircularWavyProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                val titleStyle = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp,
                    fontFamily = GoogleSansRounded,
                    color = LocalMaterialTheme.current.onPrimaryContainer
                )
                val artistStyle = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    letterSpacing = 0.sp,
                    fontFamily = GoogleSansRounded,
                    color = LocalMaterialTheme.current.onPrimaryContainer.copy(alpha = 0.7f)
                )

                AutoScrollingText(
                    text = when {
                        isCastConnecting -> "Connecting to device…"
                        isPreparingPlayback -> "Preparing playback…"
                        else -> song.title
                    },
                    style = titleStyle,
                    gradientEdgeColor = LocalMaterialTheme.current.primaryContainer,
                    canScroll = canScroll
                )
                AutoScrollingText(
                    text = if (isPreparingPlayback) "Loading audio…" else song.displayArtist,
                    style = artistStyle,
                    gradientEdgeColor = LocalMaterialTheme.current.primaryContainer,
                    canScroll = canScroll
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Single play/pause button — no prev/next (use swipe gestures instead)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(LocalMaterialTheme.current.primary)
                    .clickable(
                        interactionSource = playPauseInteraction,
                        indication = miniPlayerIndication,
                        enabled = controlsEnabled
                    ) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onPlayPause()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = LocalMaterialTheme.current.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
