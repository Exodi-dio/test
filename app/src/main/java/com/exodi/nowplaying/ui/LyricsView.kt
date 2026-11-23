package com.exodi.nowplaying.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.exodi.nowplaying.LyricLine
import com.exodi.nowplaying.Lyrics

/**
 * Karaoke lyrics: the active line stays sharp, lines further away fade and blur
 * out, and the word being sung fills in on the active line.
 */
@Composable
fun LyricsView(
    lyrics: Lyrics?,
    position: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lines = lyrics?.lines.orEmpty()

    if (lines.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No lyrics for this track",
                style = Type.Label,
                color = Palette.TextSecondary,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    val listState = rememberLazyListState()
    val active = remember(lines, position) {
        lines.indexOfLast { it.timeMs <= position + 120L }.coerceAtLeast(0)
    }

    LaunchedEffect(active, lines.size) {
        val viewport = listState.layoutInfo.viewportSize.height
        val target = (viewport * 0.21f).toInt()
        listState.animateScrollToItem(active.coerceIn(0, lines.lastIndex), -target)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 46.dp, vertical = 24.dp),
    ) {
        itemsIndexed(lines, key = { index, _ -> index }) { index, line ->
            LyricRow(
                line = line,
                active = index == active,
                position = position,
                onClick = { onSeek(line.timeMs) },
            )
        }
    }
}

@Composable
private fun LyricRow(
    line: LyricLine,
    active: Boolean,
    position: Long,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (active) 1f else 0.96f,
        label = "lyricScale",
    )

    val text: AnnotatedString = if (active && line.words.isNotEmpty()) {
        buildAnnotatedString {
            line.words.forEach { word ->
                val sung = position >= word.timeMs
                withStyle(SpanStyle(color = if (sung) Color.White else Color.White.copy(alpha = 0.55f))) {
                    append(word.text)
                }
            }
        }
    } else {
        AnnotatedString(line.text)
    }

    Text(
        text = text,
        style = Type.Lyrics,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .alpha(if (active) 1f else 0.32f)
            .blur(if (active) 0.dp else 5.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
    )
}
