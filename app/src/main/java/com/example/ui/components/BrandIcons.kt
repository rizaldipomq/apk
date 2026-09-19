package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Authentic high-precision Instagram brand icon with signature gradient background
 * and white camera glyph.
 */
@Composable
fun InstagramBrandIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Signature Instagram Gradient Background
            val gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF833AB4), // Purple
                    Color(0xFFE1306C), // Magenta / Red-pink
                    Color(0xFFFD1D1D), // Crimson
                    Color(0xFFF77737), // Orange
                    Color(0xFFFCAF45)  // Warm Yellow
                ),
                start = Offset(0f, h),
                end = Offset(w, 0f)
            )
            drawRoundRect(
                brush = gradient,
                cornerRadius = CornerRadius(w * 0.28f, h * 0.28f),
                size = Size(w, h)
            )

            // 2. Camera outer rounded square stroke
            val strokeW = w * 0.08f
            val cameraPadding = w * 0.20f
            val cameraSize = w - (cameraPadding * 2f)
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cameraPadding, cameraPadding),
                size = Size(cameraSize, cameraSize),
                cornerRadius = CornerRadius(cameraSize * 0.32f, cameraSize * 0.32f),
                style = Stroke(width = strokeW)
            )

            // 3. Center lens circle stroke
            val center = Offset(w / 2f, h / 2f)
            val lensRadius = cameraSize * 0.26f
            drawCircle(
                color = Color.White,
                radius = lensRadius,
                center = center,
                style = Stroke(width = strokeW)
            )

            // 4. Flash indicator dot
            val dotRadius = strokeW * 0.75f
            val dotOffset = Offset(w * 0.72f, h * 0.28f)
            drawCircle(
                color = Color.White,
                radius = dotRadius,
                center = dotOffset
            )
        }
    }
}

/**
 * Authentic high-precision Spotify brand icon with official green (#1DB954)
 * and signature 3 curved audio stream waves.
 */
@Composable
fun SpotifyBrandIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    waveColor: Color = Color.Black
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)
            val radius = w / 2f

            // 1. Spotify Official Green Circle
            drawCircle(
                color = Color(0xFF1DB954),
                radius = radius,
                center = center
            )

            // 2. Three signature sound waves (curved arcs)
            // Top wave (longest, thickest)
            drawSpotifyArc(
                center = center,
                width = w * 0.62f,
                height = h * 0.44f,
                yOffset = -h * 0.12f,
                strokeWidth = w * 0.095f,
                color = waveColor
            )

            // Middle wave
            drawSpotifyArc(
                center = center,
                width = w * 0.52f,
                height = h * 0.38f,
                yOffset = h * 0.04f,
                strokeWidth = w * 0.085f,
                color = waveColor
            )

            // Bottom wave (shortest)
            drawSpotifyArc(
                center = center,
                width = w * 0.42f,
                height = h * 0.32f,
                yOffset = h * 0.19f,
                strokeWidth = w * 0.075f,
                color = waveColor
            )
        }
    }
}

private fun DrawScope.drawSpotifyArc(
    center: Offset,
    width: Float,
    height: Float,
    yOffset: Float,
    strokeWidth: Float,
    color: Color
) {
    val path = Path().apply {
        val left = center.x - (width / 2f)
        val right = center.x + (width / 2f)
        val startY = center.y + yOffset + (height * 0.22f)
        val endY = center.y + yOffset - (height * 0.12f)
        val controlY = center.y + yOffset - (height * 0.50f)

        moveTo(left, startY)
        quadraticBezierTo(
            center.x,
            controlY,
            right,
            endY
        )
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )
}
