package com.tencent.qqmusic.presentation.components.player

import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader as AndroidShader
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun LiquidGlassBackground(
    expansionFraction: Float,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    if (expansionFraction <= 0.01f) return

    val infiniteTransition = rememberInfiniteTransition(label = "LiquidGlassTransition")

    val angle1 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blob1Angle"
    )

    val scale1 = infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1Scale"
    )

    val angle2 = infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blob2Angle"
    )

    val scale2 = infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2Scale"
    )

    val angle3 = infiniteTransition.animateFloat(
        initialValue = 180f,
        targetValue = 540f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blob3Angle"
    )

    val scale3 = infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob3Scale"
    )

    val baseDarkColor = remember(colorScheme) {
        val blended = Color(
            red = (colorScheme.background.red * 0.12f + 0.04f).coerceIn(0f, 1f),
            green = (colorScheme.background.green * 0.12f + 0.04f).coerceIn(0f, 1f),
            blue = (colorScheme.background.blue * 0.12f + 0.04f).coerceIn(0f, 1f),
            alpha = 1f
        )
        blended
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = expansionFraction }
            .background(baseDarkColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val blurRadius = 140f
                        renderEffect = AndroidRenderEffect.createBlurEffect(
                            blurRadius,
                            blurRadius,
                            AndroidShader.TileMode.CLAMP
                        ).asComposeRenderEffect()
                    }
                }
                .then(
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        Modifier.blur(80.dp)
                    } else Modifier
                )
        ) {
            Box(
                modifier = Modifier
                    .size(340.dp)
                    .align(Alignment.TopStart)
                    .offset(x = (-60).dp, y = (-40).dp)
                    .graphicsLayer {
                        scaleX = scale1.value
                        scaleY = scale1.value
                        rotationZ = angle1.value
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.4f, 0.4f)
                    }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colorScheme.primary.copy(alpha = 0.45f),
                                colorScheme.primary.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 80.dp, y = (-60).dp)
                    .graphicsLayer {
                        scaleX = scale2.value
                        scaleY = scale2.value
                        rotationZ = angle2.value
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.6f, 0.3f)
                    }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colorScheme.tertiary.copy(alpha = 0.42f),
                                colorScheme.tertiary.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .size(320.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 120.dp)
                    .graphicsLayer {
                        scaleX = scale3.value
                        scaleY = scale3.value
                        rotationZ = angle3.value
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.7f)
                    }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colorScheme.secondary.copy(alpha = 0.38f),
                                colorScheme.secondary.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.96f)
                        )
                    )
                )
        )
    }
}
