package com.exodi.nowplaying.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.exodi.nowplaying.PlayerUiState
import com.exodi.nowplaying.Track

enum class PlayerMode { Big, Lyrics, Queue }

@Composable
fun PlayerBlock(
    state: PlayerUiState,
    track: Track?,
    mode: PlayerMode,
    favourite: Boolean,
    codec: String?,
    onLyrics: () -> Unit,
    onQueue: () -> Unit,
    onMore: () -> Unit,
    onAlbum: () -> Unit,
    onFavourite: () -> Unit,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onShuffle: () -> Unit,
    onAirPlay: () -> Unit,
    onSeek: (Long) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        if (mode == PlayerMode.Big) {
            Spacer(Modifier.height(29.dp))
            Text(
                text = track?.title.orEmpty(),
                style = Type.Title,
                color = Palette.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = track?.artist.orEmpty(),
                style = Type.Artist,
                color = Palette.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            )
            Spacer(Modifier.height(38.dp))
        } else {
            Spacer(Modifier.height(28.dp))
        }

        ControlRow(
            track = track,
            mode = mode,
            favourite = favourite,
            onLyrics = onLyrics,
            onQueue = onQueue,
            onMore = onMore,
            onAlbum = onAlbum,
            onFavourite = onFavourite,
        )

        Spacer(Modifier.height(40.dp))
        ProgressBlock(state = state, codec = codec, onSeek = onSeek)
        Spacer(Modifier.height(32.dp))
        TransportRow(
            state = state,
            onTogglePlay = onTogglePlay,
            onPrevious = onPrevious,
            onNext = onNext,
            onShuffle = onShuffle,
            onAirPlay = onAirPlay,
        )
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun ControlRow(
    track: Track?,
    mode: PlayerMode,
    favourite: Boolean,
    onLyrics: () -> Unit,
    onQueue: () -> Unit,
    onMore: () -> Unit,
    onAlbum: () -> Unit,
    onFavourite: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        GlassButton(onClick = onLyrics, modifier = Modifier.size(50.dp), pressed = mode == PlayerMode.Lyrics) {
            Icon(QuoteBubble, contentDescription = "Lyrics", tint = Color.White, modifier = Modifier.size(23.dp))
        }

        if (mode == PlayerMode.Big) {
            GlassButton(
                onClick = onMore,
                modifier = Modifier.width(158.dp).height(50.dp),
                shape = RoundedCornerShape(25.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Favourite",
                        tint = if (favourite) Color.White else Color.White.copy(alpha = 0.55f),
                        modifier = Modifier.size(22.dp).noRippleClick(onFavourite),
                    )
                    Spacer(Modifier.width(18.dp))
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = "Album",
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(22.dp).noRippleClick(onAlbum),
                    )
                    Spacer(Modifier.width(18.dp))
                    Icon(
                        imageVector = Icons.Rounded.MoreHoriz,
                        contentDescription = "More",
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        } else {
            GlassButton(
                onClick = onAlbum,
                modifier = Modifier.width(211.dp).height(50.dp),
                shape = RoundedCornerShape(25.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = track?.title.orEmpty(),
                        style = Type.MiniTitle,
                        color = Palette.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = " • ",
                        style = Type.MiniTitle,
                        color = Palette.TextPrimary,
                    )
                    Text(
                        text = track?.artist.orEmpty(),
                        style = Type.MiniTitle,
                        color = Palette.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        GlassButton(onClick = onQueue, modifier = Modifier.size(50.dp), pressed = mode == PlayerMode.Queue) {
            Icon(
                imageVector = ListIcon,
                contentDescription = "Queue",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun ProgressBlock(state: PlayerUiState, codec: String?, onSeek: (Long) -> Unit) {
    val duration = state.currentDuration
    var dragging by remember { mutableStateOf<Float?>(null) }
    val progress = if (duration > 0L) {
        ((dragging ?: (state.position.toFloat() / duration)).coerceIn(0f, 1f))
    } else {
        0f
    }

    Column(Modifier.fillMaxWidth().padding(horizontal = 31.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .pointerInput(duration) {
                    detectDragGestures(
                        onDragStart = { offset -> dragging = (offset.x / size.width).coerceIn(0f, 1f) },
                        onDragEnd = {
                            dragging?.let { value -> onSeek((value * duration).toLong()) }
                            dragging = null
                        },
                        onDrag = { change, _ ->
                            dragging = (change.position.x / size.width).coerceIn(0f, 1f)
                        },
                    )
                }
        ) {
            Canvas(Modifier.fillMaxWidth().height(4.dp)) {
                val radius = CornerRadius(size.height / 2f, size.height / 2f)
                drawRoundRect(
                    color = Palette.TrackIdle,
                    cornerRadius = radius,
                )
                drawRoundRect(
                    color = Palette.TrackPlayed,
                    size = Size(size.width * progress, size.height),
                    cornerRadius = radius,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = formatTime((progress * duration).toLong()),
                style = Type.Time,
                color = Palette.TextTertiary,
            )
            Spacer(Modifier.weight(1f))
            if (!codec.isNullOrBlank()) {
                Text(
                    text = codec,
                    style = Type.Time,
                    color = Palette.TextTertiary.copy(alpha = 0.8f),
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "- ${formatTime(duration)}",
                style = Type.Time,
                color = Palette.TextTertiary,
            )
        }
    }
}

@Composable
private fun TransportRow(
    state: PlayerUiState,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onShuffle: () -> Unit,
    onAirPlay: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 38.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton2(BroadcastIcon, "Output device", onAirPlay, size = 22.dp)
        IconButton2(Icons.Rounded.FastRewind, "Previous", onPrevious, size = 24.dp)
        IconButton2(
            imageVector = if (state.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = if (state.playing) "Pause" else "Play",
            onClick = onTogglePlay,
            size = if (state.playing) 30.dp else 32.dp,
            tint = Color.White.copy(alpha = 0.5f),
        )
        IconButton2(Icons.Rounded.FastForward, "Next", onNext, size = 24.dp)
        IconButton2(Icons.Rounded.Shuffle, "Shuffle", onShuffle, size = 30.dp)
    }
}

@Composable
private fun IconButton2(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 28.dp,
    tint: Color = Color.White.copy(alpha = 0.5f),
) {
    Box(
        Modifier
            .size(size)
            .noRippleClick(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun Modifier.noRippleClick(onClick: () -> Unit): Modifier = this
    .then(
        Modifier.pointerInput(onClick) {
            detectTapGestures { onClick() }
        }
    )

private fun formatTime(millis: Long): String {
    if (millis <= 0L) return "0:00"
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
