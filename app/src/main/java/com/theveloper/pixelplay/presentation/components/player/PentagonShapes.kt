package com.theveloper.pixelplay.presentation.components.player

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class PentagonRightShape(private val cornerRadius: Float = 24f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val w = size.width
        val h = size.height
        val r = cornerRadius.coerceAtMost(h / 4f)
        val path = Path().apply {
            moveTo(0f, r)
            quadraticBezierTo(0f, 0f, r, 0f)
            lineTo(w * 0.7f - r, 0f)
            quadraticBezierTo(w * 0.7f, 0f, w * 0.78f, r * 0.5f)
            lineTo(w - r * 0.5f, h / 2f - r * 0.5f)
            quadraticBezierTo(w, h / 2f, w - r * 0.5f, h / 2f + r * 0.5f)
            lineTo(w * 0.78f, h - r * 0.5f)
            quadraticBezierTo(w * 0.7f, h, w * 0.7f - r, h)
            lineTo(r, h)
            quadraticBezierTo(0f, h, 0f, h - r)
            close()
        }
        return Outline.Generic(path)
    }
}

class PentagonLeftShape(private val cornerRadius: Float = 24f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val w = size.width
        val h = size.height
        val r = cornerRadius.coerceAtMost(h / 4f)
        val path = Path().apply {
            moveTo(w, h - r)
            quadraticBezierTo(w, h, w - r, h)
            lineTo(w * 0.3f + r, h)
            quadraticBezierTo(w * 0.3f, h, w * 0.22f, h - r * 0.5f)
            lineTo(r * 0.5f, h / 2f + r * 0.5f)
            quadraticBezierTo(0f, h / 2f, r * 0.5f, h / 2f - r * 0.5f)
            lineTo(w * 0.22f, r * 0.5f)
            quadraticBezierTo(w * 0.3f, 0f, w * 0.3f + r, 0f)
            lineTo(w - r, 0f)
            quadraticBezierTo(w, 0f, w, r)
            close()
        }
        return Outline.Generic(path)
    }
}
