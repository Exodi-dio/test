package com.exodi.nowplaying.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.Shape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawOutline
import androidx.compose.ui.unit.dp

/**
 * Liquid glass: a translucent fill, a light inner edge along the top and a soft
 * shadow underneath. Matches the Figma "Liquid Glass" recipe.
 */
fun Modifier.liquidGlass(
    shape: Shape,
    fill: Color = Palette.Glass,
    shadowRadius: Int = 5,
): Modifier = this
    .shadow(
        elevation = shadowRadius.dp,
        shape = shape,
        ambientColor = Color.Black.copy(alpha = 0.10f),
        spotColor = Color.Black.copy(alpha = 0.10f),
    )
    .background(fill, shape)
    .drawWithContent {
        drawContent()
        val edge = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.24f), Color.Transparent),
            startY = 0f,
            endY = size.height * 0.6f,
        )
        val outline = shape.createOutline(size, layoutDirection, this)
        drawOutline(outline, edge, style = Stroke(width = 3.dp.toPx()))
    }

@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    pressed: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .liquidGlass(shape = shape, fill = if (pressed) Palette.GlassPressed else Palette.Glass)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
        content = content,
    )
}
