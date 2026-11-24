package com.exodi.nowplaying.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exodi.nowplaying.R

private fun inter(weight: Int) = Font(
    resId = R.font.inter_variable,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val InterFamily = FontFamily(
    inter(400),
    inter(500),
    inter(600),
    inter(700),
)

val InterDisplay = FontFamily(
    inter(700),
)

object Palette {
    val WashTop = Color(0xFF2878B7)
    val WashBottom = Color(0xFF4FAFF0)
    val ArtFade = Color(0xFF4E93C6)
    val Glass = Color(0x33FFFFFF)
    val GlassPressed = Color(0x73FFFFFF)
    val TextPrimary = Color(0xE6FFFFFF)
    val TextSecondary = Color(0x80FFFFFF)
    val TextTertiary = Color(0x40FFFFFF)
    val TrackIdle = Color(0x0D000000)
    val TrackPlayed = Color(0x80FFFFFF)
}

object Type {
    val Lyrics = TextStyle(
        fontFamily = InterDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.sp,
    )
    val Title = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.4).sp,
    )
    val Artist = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.36).sp,
    )
    val MiniTitle = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.32).sp,
    )
    val Label = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
    )
    val SourceLabel = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 17.sp,
        letterSpacing = (-0.28).sp,
    )
    val MenuTitle = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.8.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.sp,
    )
    val MenuSubtitle = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.4.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp,
    )
    val PlaylistName = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.sp,
    )
    val Time = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        letterSpacing = (-0.24).sp,
    )
}

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF101014),
    background = Color(0xFF2878B7),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF17171D),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0x33211E1E),
    onSurfaceVariant = Color(0x80FFFFFF),
    outline = Color(0x26FFFFFF),
)

@Composable
fun NowPlayingTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkScheme, content = content)
}
