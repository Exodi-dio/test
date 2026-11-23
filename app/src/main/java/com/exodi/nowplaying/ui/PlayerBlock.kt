package com.exodi.nowplaying.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirPlay
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
            Spacer(Modifier.height(28.dp))
            Text(
                text = track?.title.orEmpty(),
                style = Type.Title,
                color = Palette.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = track?.artist.orEmpty(),
                style = Type.Artist,
                color = Palette.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            )
            Spacer(Modifier.height(28.dp))
        } else {
            Spacer(Modifier.height(24.dp))
        }

        ControlRow(
            state = state,
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
        Spacer(Modifier.height(14.dp))
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
    state: PlayerUiState,
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
    ) {
        GlassButton(onClick = onLyrics, modifier = Modifier.size(50.dp), pressed = mode == PlayerMode.Lyrics) {
            Icon(QuoteBubble, contentDescription = "Lyrics", tint = Color.White, modifier = Modifier.size(22.dp))
        }

        Spacer(Modifier.weight(1f))

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
                        modifier = Modifier.size(20.dp).noRippleClick(onFavourite),
                    )
                    Spacer(Modifier.width(18.dp))
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = "Album",
                        tint = Color.White.copy(alpha = 0.55f),
                        modifier = Modifier.size(20.dp).noRippleClick(onAlbum),
                    )
                    Spacer(Modifier.width(18.dp))
                    Icon(
                        imageVector = Icons.Rounded.MoreHoriz,
                        contentDescription = "More",
                        tint = Color.White.copy(alpha = 0.55f),
                        modifier = Modifier.size(20.dp),
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

        Spacer(Modifier.weight(1f))

        GlassButton(onClick = onQueue, modifier = Modifier.size(50.dp), pressed = mode == PlayerMode.Queue) {
            Icon(
                imageVector = Icons.Rounded.GraphicEq,
                contentDescription = "Queue",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
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
    val density = LocalDensity.current

    Column(Modifier.fillMaxWidth().padding(horizontal = 28.dp)) {
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
        IconButton2(Icons.Rounded.AirPlay, "AirPlay", onAirPlay)
        IconButton2(Icons.Rounded.SkipPrevious, "Previous", onPrevious, size = 32.dp)
        IconButton2(
            imageVector = if (state.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = if (state.playing) "Pause" else "Play",
            onClick = onTogglePlay,
            size = if (state.playing) 32.dp else 36.dp,
            tint = Color.White,
        )
        IconButton2(Icons.Rounded.SkipNext, "Next", onNext, size = 32.dp)
        IconButton2(Icons.Rounded.Shuffle, "Shuffle", onShuffle)
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
        Icon(imageVector, contentDescription = contentDescription, tint = tint, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun Modifier.noRippleClick(onClick: () -> Unit): Modifier = this
    .then(
        Modifier.pointerInput(onClick) {
            androidx.compose.foundation.gestures.detectTapGestures { onClick() }
        }
    )

private fun formatTime(millis: Long): String {
    if (millis <= 0L) return "0:00"
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
