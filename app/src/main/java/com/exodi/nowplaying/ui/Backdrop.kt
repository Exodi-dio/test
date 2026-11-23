package com.exodi.nowplaying.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

/**
 * Masks the content with a vertical gradient. Used to fade the sharp artwork
 * into the blurred one, the way the Figma progressive blur does.
 */
fun Modifier.gradientMask(brush: Brush): Modifier = graphicsLayer {
    compositingStrategy = CompositingStrategy.Offscreen
}.drawWithContent {
    drawContent()
    drawRect(brush = brush, blendMode = BlendMode.DstIn)
}

/**
 * The screen backdrop: a wash of the cover colours with the artwork floating on
 * top, sharp at the top and progressively blurred towards the bottom.
 */
@Composable
fun ArtworkBackdrop(
    artwork: Bitmap?,
    blurred: Bitmap?,
    wash: Pair<Color, Color>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(wash.first, wash.second),
                    startY = 0f,
                    endY = 1000f,
                )
            )
    ) {
        val artWidth = maxWidth * 1.6f
        val artHeight = artWidth * (600f / 642f)

        Box(
            Modifier
                .width(artWidth)
                .height(artHeight)
                .offset(x = -maxWidth * (135f / 402f))
        ) {
            if (artwork != null) {
                Image(
                    bitmap = artwork.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .gradientMask(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to Color.White,
                                    0.824f to Color.White,
                                    1f to Color.Transparent,
                                ),
                                startY = 0f,
                                endY = artHeight.toPx(),
                            )
                        ),
                )
            }

            if (blurred != null) {
                Image(
                    bitmap = blurred.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .gradientMask(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to Color.Transparent,
                                    0.824f to Color.Transparent,
                                    1f to Color.White,
                                ),
                                startY = 0f,
                                endY = artHeight.toPx(),
                            )
                        ),
                )
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Palette.ArtFade),
                            startY = (artHeight * 0.55f).toPx(),
                            endY = artHeight.toPx(),
                        )
                    )
            )
        }
    }
}
