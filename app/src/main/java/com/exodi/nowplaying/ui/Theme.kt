package com.exodi.nowplaying.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFD5C7FF),
    onPrimary = Color(0xFF1B1430),
    secondary = Color(0xFF9BD9C4),
    background = Color(0xFF0A0A0E),
    onBackground = Color(0xFFF2F0F7),
    surface = Color(0xFF17161D),
    onSurface = Color(0xFFF2F0F7),
    surfaceVariant = Color(0xFF26242F),
    onSurfaceVariant = Color(0xFF9A96A8),
    outline = Color(0xFF3A3746),
)

@Composable
fun NowPlayingTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkScheme, content = content)
}
