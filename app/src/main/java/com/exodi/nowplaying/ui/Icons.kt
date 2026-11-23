package com.exodi.nowplaying.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

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
            strokeLineWidth = 1.7f,
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
