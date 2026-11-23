package com.exodi.nowplaying.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import com.exodi.nowplaying.PlayerUiState
import com.exodi.nowplaying.PlayerViewModel

@Composable
fun NowPlayingScreen(
    player: PlayerViewModel,
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit,
) {
    val state by player.state.collectAsStateWithLifecycle()
    var showQueue by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF1A1824), Color(0xFF0A0A0E)))
            )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            when {
                !permissionGranted -> Message(
                    text = "Now Playing needs access to the audio files on this device.",
                    action = "Allow access",
                    onAction = onRequestPermission,
                )

                state.scanning -> Message(
                    text = "Looking for music…",
                    action = null,
                    onAction = null,
                )

                state.tracks.isEmpty() -> Message(
                    text = "No music found on this device.",
                    action = "Scan again",
                    onAction = player::loadLibrary,
                )

                else -> PlayerBody(
                    state = state,
                    player = player,
                    onQueue = { showQueue = true },
                    onLyrics = { showLyrics = true },
                )
            }
        }
    }

    if (showQueue) {
        QueueSheet(
            state = state,
            onDismiss = { showQueue = false },
            onSelect = { index ->
                player.playAt(index)
                showQueue = false
            },
        )
    }

    if (showLyrics) {
        LyricsSheet(
            track = state.track,
            position = state.position,
            onDismiss = { showLyrics = false },
            onSeek = player::seekTo,
        )
    }
}

@Composable
private fun PlayerBody(
    state: PlayerUiState,
    player: PlayerViewModel,
    onQueue: () -> Unit,
    onLyrics: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            val side = minOf(maxWidth, maxHeight)
            TrackArtwork(state.track, Modifier.size(side), corner = 24.dp)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = state.track?.title ?: "Nothing playing",
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = state.track?.artist.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(Modifier.height(20.dp))
        SeekBar(state, player::seekTo)
        Spacer(Modifier.height(8.dp))
        Controls(state, player)
        Spacer(Modifier.height(8.dp))
        BottomBar(state, onLyrics = onLyrics, onQueue = onQueue)
    }
}

@Composable
private fun SeekBar(state: PlayerUiState, onSeek: (Long) -> Unit) {
    val duration = state.currentDuration
    var drag by remember { mutableStateOf<Float?>(null) }
    val progress = if (duration > 0L) (state.position.toFloat() / duration).coerceIn(0f, 1f) else 0f
    val shown = drag ?: progress

    Column(Modifier.fillMaxWidth()) {
        Slider(
            value = shown,
            onValueChange = { drag = it },
            onValueChangeFinished = {
                val value = drag
                if (value != null && duration > 0L) onSeek((value * duration).toLong())
                drag = null
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatTime((shown * duration).toLong()),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatTime(duration),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Controls(state: PlayerUiState, player: PlayerViewModel) {
    val accent = MaterialTheme.colorScheme.primary
    val dim = MaterialTheme.colorScheme.onSurfaceVariant
    val hasPrevious = state.index > 0
    val hasNext = state.index >= 0 && state.index < state.tracks.lastIndex

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = player::toggleShuffle) {
            Icon(
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = "Shuffle",
                tint = if (state.shuffle) accent else dim,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = player::previous) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = "Previous",
                    tint = if (hasPrevious) MaterialTheme.colorScheme.onSurface else dim.copy(alpha = 0.4f),
                    modifier = Modifier.size(36.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            FilledIconButton(
                onClick = player::togglePlay,
                modifier = Modifier.size(72.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = Color(0xFF0A0A0E),
                ),
            ) {
                Icon(
                    imageVector = if (state.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (state.playing) "Pause" else "Play",
                    modifier = Modifier.size(36.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            IconButton(onClick = player::next) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "Next",
                    tint = if (hasNext) MaterialTheme.colorScheme.onSurface else dim.copy(alpha = 0.4f),
                    modifier = Modifier.size(36.dp),
                )
            }
        }

        IconButton(onClick = player::toggleRepeat) {
            Icon(
                imageVector = if (state.repeat == Player.REPEAT_MODE_ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                contentDescription = "Repeat",
                tint = if (state.repeat != Player.REPEAT_MODE_OFF) accent else dim,
            )
        }
    }
}

@Composable
private fun BottomBar(state: PlayerUiState, onLyrics: () -> Unit, onQueue: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onLyrics) {
            Icon(
                imageVector = Icons.Rounded.Subtitles,
                contentDescription = "Lyrics",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = if (state.index >= 0) "${state.index + 1} / ${state.tracks.size}" else "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.weight(1f))

        FilledIconButton(
            onClick = onQueue,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Icon(
                imageVector = Icons.Rounded.QueueMusic,
                contentDescription = "Queue",
            )
        }
    }
}

@Composable
private fun Message(text: String, action: String?, onAction: (() -> Unit)?) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (action != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onAction) {
                Text(action)
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    if (millis <= 0L) return "0:00"
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
