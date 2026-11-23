package com.exodi.nowplaying.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Speech bubble with quote marks, the shape used for the lyrics button.
 */
val QuoteBubble: ImageVector by lazy {
    ImageVector.Builder(
        name = "QuoteBubble",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(6f, 4f)
            lineTo(18f, 4f)
            curveTo(20.2f, 4f, 21.8f, 5.7f, 21.8f, 8f)
            lineTo(21.8f, 13f)
            curveTo(21.8f, 15.3f, 20.2f, 17f, 18f, 17f)
            lineTo(11.5f, 17f)
            lineTo(6.5f, 20.5f)
            lineTo(6.5f, 17f)
            lineTo(6f, 17f)
            curveTo(3.8f, 17f, 2.2f, 15.3f, 2.2f, 13f)
            lineTo(2.2f, 8f)
            curveTo(2.2f, 5.7f, 3.8f, 4f, 6f, 4f)
            close()
        }
        path(fill = SolidColor(Color.White)) {
            moveTo(7.2f, 8.6f)
            lineTo(11.2f, 8.6f)
            lineTo(11.2f, 12.1f)
            curveTo(11.2f, 13.3f, 10.4f, 14.2f, 9.1f, 14.2f)
            lineTo(9.1f, 12.9f)
            curveTo(9.8f, 12.9f, 10.2f, 12.5f, 10.2f, 11.7f)
            lineTo(7.2f, 11.7f)
            close()
        }
        path(fill = SolidColor(Color.White)) {
            moveTo(13f, 8.6f)
            lineTo(17f, 8.6f)
            lineTo(17f, 12.1f)
            curveTo(17f, 13.3f, 16.2f, 14.2f, 14.9f, 14.2f)
            lineTo(14.9f, 12.9f)
            curveTo(15.6f, 12.9f, 16f, 12.5f, 16f, 11.7f)
            lineTo(13f, 11.7f)
            close()
        }
    }.build()
}

/**
 * The queue button: three dots and three rules.
 */
val ListIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "ListIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.White)) {
            circle(4.6f, 6f, 1.7f)
            circle(4.6f, 12f, 1.7f)
            circle(4.6f, 18f, 1.7f)
        }
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 2.4f,
            strokeLineCap = StrokeCap.Round,
        ) {
            moveTo(10.2f, 6f)
            lineTo(20.4f, 6f)
            moveTo(10.2f, 12f)
            lineTo(20.4f, 12f)
            moveTo(10.2f, 18f)
            lineTo(20.4f, 18f)
        }
    }.build()
}

/**
 * Output device: a marker under three broadcast arcs.
 */
val BroadcastIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "BroadcastIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
        ) {
            val centerX = 12f
            val centerY = 14.6f
            val handle = 0.4204f
            listOf(4.4f, 7.2f, 10f).forEach { radius ->
                var angle = 200.0
                val first = pointOn(centerX, centerY, radius, angle)
                moveTo(first.first, first.second)
                repeat(2) {
                    val next = angle + 70.0
                    val from = pointOn(centerX, centerY, radius, angle)
                    val to = pointOn(centerX, centerY, radius, next)
                    val startTan = tangentOn(angle)
                    val endTan = tangentOn(next)
                    curveTo(
                        from.first + handle * radius * startTan.first,
                        from.second + handle * radius * startTan.second,
                        to.first - handle * radius * endTan.first,
                        to.second - handle * radius * endTan.second,
                        to.first,
                        to.second,
                    )
                    angle = next
                }
            }
        }
        path(fill = SolidColor(Color.White)) {
            moveTo(12f, 22.4f)
            lineTo(6.6f, 14.6f)
            lineTo(17.4f, 14.6f)
            close()
        }
    }.build()
}

private fun pointOn(centerX: Float, centerY: Float, radius: Float, degrees: Double): Pair<Float, Float> {    val radians = degrees * PI / 180.0
    return (centerX + radius * cos(radians)).toFloat() to (centerY + radius * sin(radians)).toFloat()
}

private fun tangentOn(degrees: Double): Pair<Float, Float> {
    val radians = degrees * PI / 180.0
    return (-sin(radians)).toFloat() to cos(radians).toFloat()
}

private fun PathBuilder.circle(centerX: Float, centerY: Float, radius: Float) {
    val k = 0.5523f * radius
    moveTo(centerX + radius, centerY)
    curveTo(centerX + radius, centerY + k, centerX + k, centerY + radius, centerX, centerY + radius)
    curveTo(centerX - k, centerY + radius, centerX - radius, centerY + k, centerX - radius, centerY)
    curveTo(centerX - radius, centerY - k, centerX - k, centerY - radius, centerX, centerY - radius)
    curveTo(centerX + k, centerY - radius, centerX + radius, centerY - k, centerX + radius, centerY)
    close()
}
