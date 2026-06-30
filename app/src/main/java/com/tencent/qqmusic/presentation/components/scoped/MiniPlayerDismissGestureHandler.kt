package com.tencent.qqmusic.presentation.components.scoped

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

private enum class MiniDismissDragPhase { IDLE, TENSION, SNAPPING, FREE_DRAG }

/**
 * Handles mini-player horizontal swipe gestures:
 *  - Short swipe LEFT  → next song
 *  - Short swipe RIGHT → previous song
 *  - Long swipe (>65% screen width) → dismiss queue (original behavior)
 */
internal class MiniPlayerDismissGestureHandler(
    private val scope: CoroutineScope,
    private val density: Density,
    private val hapticFeedback: HapticFeedback,
    private val offsetAnimatable: Animatable<Float, AnimationVector1D>,
    private val screenWidthPx: Float,
    private val onDismissPlaylistAndShowUndo: () -> Unit,
    private val onNextSong: () -> Unit = {},
    private val onPreviousSong: () -> Unit = {},
    private val onDismissStarted: () -> Unit = {}
) {
    private var dragPhase: MiniDismissDragPhase = MiniDismissDragPhase.IDLE
    private var accumulatedDragX: Float = 0f
    private var offsetJob: Job? = null
    private var hapticFiredForCurrentDrag = false

    // Swipe left >= 40% screen → next song
    // Swipe right >= 40% screen → previous song
    // Either direction >= 65% screen → dismiss queue
    private val skipThresholdFraction = 0.40f
    private val dismissThresholdFraction = 0.65f

    fun onDragStart() {
        dragPhase = MiniDismissDragPhase.TENSION
        accumulatedDragX = 0f
        hapticFiredForCurrentDrag = false
        offsetJob?.cancel()
        offsetJob = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            offsetAnimatable.stop()
        }
    }

    fun onHorizontalDrag(dragAmount: Float) {
        accumulatedDragX += dragAmount

        when (dragPhase) {
            MiniDismissDragPhase.TENSION -> {
                val snapThresholdPx = 100f * density.density
                if (abs(accumulatedDragX) < snapThresholdPx) {
                    val maxTensionOffsetPx = 30f * density.density
                    val dragFraction = (abs(accumulatedDragX) / snapThresholdPx).coerceIn(0f, 1f)
                    val tensionOffset = lerp(0f, maxTensionOffsetPx, dragFraction)
                    offsetJob?.cancel()
                    offsetJob = scope.launch(start = CoroutineStart.UNDISPATCHED) {
                        offsetAnimatable.snapTo(tensionOffset * accumulatedDragX.sign)
                    }
                } else {
                    dragPhase = MiniDismissDragPhase.SNAPPING
                }
            }

            MiniDismissDragPhase.SNAPPING -> {
                if (!hapticFiredForCurrentDrag) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    hapticFiredForCurrentDrag = true
                }
                offsetJob?.cancel()
                offsetJob = scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    offsetAnimatable.animateTo(
                        targetValue = accumulatedDragX,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)
                    )
                }
                dragPhase = MiniDismissDragPhase.FREE_DRAG
            }

            MiniDismissDragPhase.FREE_DRAG -> {
                offsetJob?.cancel()
                offsetJob = scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    offsetAnimatable.animateTo(
                        targetValue = accumulatedDragX,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    )
                }
            }

            MiniDismissDragPhase.IDLE -> Unit
        }
    }

    fun onDragEnd() {
        dragPhase = MiniDismissDragPhase.IDLE
        offsetJob?.cancel()
        val dismissThreshold = screenWidthPx * dismissThresholdFraction
        val skipThreshold = screenWidthPx * skipThresholdFraction
        val dragAbs = abs(accumulatedDragX)
        val isLeftSwipe = accumulatedDragX < 0

        when {
            // Long swipe → dismiss queue
            dragAbs > dismissThreshold -> {
                onDismissStarted()
                val targetDismissOffset = if (isLeftSwipe) -screenWidthPx else screenWidthPx
                offsetJob = scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    offsetAnimatable.animateTo(
                        targetValue = targetDismissOffset,
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                    )
                    onDismissPlaylistAndShowUndo()
                    offsetAnimatable.snapTo(0f)
                }
            }
            // Medium swipe → skip track
            dragAbs > skipThreshold -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                val slideTarget = if (isLeftSwipe) -screenWidthPx * 0.28f else screenWidthPx * 0.28f
                offsetJob = scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    offsetAnimatable.animateTo(
                        targetValue = slideTarget,
                        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
                    )
                    if (isLeftSwipe) onNextSong() else onPreviousSong()
                    offsetAnimatable.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            }
            // Short swipe → spring back
            else -> {
                offsetJob = scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    offsetAnimatable.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            }
        }
    }
}

@Composable
internal fun rememberMiniPlayerDismissGestureHandler(
    scope: CoroutineScope,
    density: Density,
    hapticFeedback: HapticFeedback,
    offsetAnimatable: Animatable<Float, AnimationVector1D>,
    screenWidthPx: Float,
    onDismissPlaylistAndShowUndo: () -> Unit,
    onNextSong: () -> Unit = {},
    onPreviousSong: () -> Unit = {},
    onDismissStarted: () -> Unit
): MiniPlayerDismissGestureHandler {
    val onDismissState = rememberUpdatedState(onDismissPlaylistAndShowUndo)
    val onNextState = rememberUpdatedState(onNextSong)
    val onPrevState = rememberUpdatedState(onPreviousSong)
    val onDismissStartedState = rememberUpdatedState(onDismissStarted)
    return remember(scope, density, hapticFeedback, offsetAnimatable, screenWidthPx) {
        MiniPlayerDismissGestureHandler(
            scope = scope,
            density = density,
            hapticFeedback = hapticFeedback,
            offsetAnimatable = offsetAnimatable,
            screenWidthPx = screenWidthPx,
            onDismissPlaylistAndShowUndo = { onDismissState.value() },
            onNextSong = { onNextState.value() },
            onPreviousSong = { onPrevState.value() },
            onDismissStarted = { onDismissStartedState.value() }
        )
    }
}

internal fun Modifier.miniPlayerDismissHorizontalGesture(
    enabled: Boolean,
    handler: MiniPlayerDismissGestureHandler
): Modifier {
    if (!enabled) return this
    return this.pointerInput(enabled, handler) {
        detectHorizontalDragGestures(
            onDragStart = { handler.onDragStart() },
            onHorizontalDrag = { change, dragAmount ->
                change.consume()
                handler.onHorizontalDrag(dragAmount)
            },
            onDragEnd = { handler.onDragEnd() }
        )
    }
}
