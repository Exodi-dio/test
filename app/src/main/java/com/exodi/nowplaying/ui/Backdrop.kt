package com.exodi.nowplaying.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ArtworkBackdrop(
    artwork: Bitmap?,
    wash: Pair<Color, Color>,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to wash.first,
                        0.77f to wash.second,
                        1f to wash.second,
                    ),
                )
            )
    ) {
        val artWidth = maxWidth * (642f / 402f)
        val artHeight = maxHeight * (600f / 874f)
        val artHeightPx = with(density) { artHeight.toPx() }

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
                        .blur(86.4.dp)
                        .gradientMask(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to Color.Transparent,
                                    0.824f to Color.Transparent,
                                    1f to Color.White,
                                ),
                                startY = 0f,
                                endY = artHeightPx,
                            )
                        ),
                )

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
                                endY = artHeightPx,
                            )
                        ),
                )
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                0.875f to Color.Transparent,
                                1f to Palette.ArtFade,
                            ),
                            startY = 0f,
                            endY = artHeightPx,
                        )
                    )
            )
        }
    }
}
