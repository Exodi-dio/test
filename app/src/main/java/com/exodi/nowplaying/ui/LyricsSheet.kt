package com.exodi.nowplaying.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exodi.nowplaying.LyricLine
import com.exodi.nowplaying.Lyrics
import com.exodi.nowplaying.LyricsLoader
import com.exodi.nowplaying.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsSheet(
    track: Track?,
    position: Long,
    onDismiss: () -> Unit,
    onSeek: (Long) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var lyrics by remember(track?.id) { mutableStateOf<Lyrics?>(null) }
    var loading by remember(track?.id) { mutableStateOf(true) }

    LaunchedEffect(track?.id) {
        val current = track
        if (current == null) {
            loading = false
            return@LaunchedEffect
        }
        loading = true
        lyrics = withContext(Dispatchers.IO) { LyricsLoader.load(context, current) }
        loading = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.fillMaxHeight(0.85f).padding(horizontal = 24.dp)) {
            Text(
                text = track?.title ?: "Lyrics",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = track?.artist.orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
            Spacer(Modifier.height(16.dp))

            val current = lyrics
            when {
                loading -> Text(
                    text = "Looking for lyrics…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                current == null -> Text(
                    text = "No lyrics found. Drop a .lrc file next to the track or embed the lyrics in the tags.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                !current.synced -> Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 32.dp)
                ) {
                    current.lines.forEach { line ->
                        Text(
                            text = line.text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                }

                else -> SyncedLyrics(current, position, onSeek)
            }
        }
    }
}

@Composable
private fun SyncedLyrics(lyrics: Lyrics, position: Long, onSeek: (Long) -> Unit) {
    val listState = rememberLazyListState()
    val active = lyrics.lines.indexOfLast { it.timeMs <= position + 150L }

    LaunchedEffect(active) {
        if (active >= 0) {
            listState.animateScrollToItem(max(0, active - 2))
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 48.dp),
    ) {
        items(lyrics.lines.size) { index ->
            LyricRow(
                line = lyrics.lines[index],
                active = index == active,
                position = position,
                onClick = { onSeek(lyrics.lines[index].timeMs) },
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
    val accent = MaterialTheme.colorScheme.primary
    val dim = Color(0xFF6C6880)

    val text = if (line.words.isEmpty()) {
        AnnotatedString(line.text)
    } else {
        buildAnnotatedString {
            line.words.forEach { word ->
                withStyle(SpanStyle(color = if (position >= word.timeMs) accent else dim)) {
                    append(word.text)
                }
            }
        }
    }

    Text(
        text = text,
        color = if (active) accent else dim,
        fontSize = if (active) 20.sp else 17.sp,
        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
    )
}
