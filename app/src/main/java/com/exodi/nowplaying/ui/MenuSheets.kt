package com.exodi.nowplaying.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.exodi.nowplaying.Track

private val SheetColor = Color(0xF0211E1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreMenuSheet(
    favourite: Boolean,
    shuffle: Boolean,
    repeatOne: Boolean,
    onFavourite: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onSleepTimer: () -> Unit,
    onAlbum: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = SheetColor) {
        Column(Modifier.fillMaxWidth().padding(bottom = 28.dp)) {
            SheetItem(Icons.Rounded.Favorite, if (favourite) "Remove from Favourites" else "Add to Favourites", onFavourite)
            SheetItem(Icons.Rounded.Shuffle, "Shuffle", onShuffle, active = shuffle)
            SheetItem(
                if (repeatOne) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                "Repeat",
                onRepeat,
                active = repeatOne,
            )
            SheetItem(Icons.Rounded.Timer, "Sleep Timer", onSleepTimer)
            SheetItem(Icons.Rounded.Album, "Go to Album", onAlbum)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackMenuSheet(
    track: Track,
    favourite: Boolean,
    onPlayNext: () -> Unit,
    onFavourite: () -> Unit,
    onAlbum: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = SheetColor) {
        Column(Modifier.fillMaxWidth().padding(bottom = 28.dp)) {
            Text(
                text = track.title,
                style = Type.PlaylistName,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
            )
            Text(
                text = track.artist,
                style = Type.MiniTitle,
                color = Palette.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 28.dp),
            )
            Spacer(Modifier.height(12.dp))
            SheetItem(Icons.Rounded.PlaylistPlay, "Play Next", onPlayNext)
            SheetItem(
                if (favourite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                if (favourite) "Remove from Favourites" else "Add to Favourites",
                onFavourite,
            )
            SheetItem(Icons.Rounded.Album, "Go to Album", onAlbum)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimerSheet(
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = SheetColor) {
        Column(Modifier.fillMaxWidth().padding(bottom = 28.dp)) {
            Text(
                text = "Sleep Timer",
                style = Type.PlaylistName,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
            )
            listOf(15, 30, 45, 60).forEach { minutes ->
                SheetItem(Icons.Rounded.Timer, "$minutes minutes", onClick = { onSelect(minutes) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumSheet(
    albumName: String,
    tracks: List<Track>,
    currentId: Long?,
    onPlay: (Track) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = SheetColor) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = albumName,
                style = Type.PlaylistName,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
            )
            Column(Modifier.verticalScroll(rememberScrollState())) {
                tracks.forEach { track ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onPlay(track) }
                            .padding(horizontal = 28.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = track.title,
                            style = Type.MiniTitle,
                            color = if (track.id == currentId) Color.White else Palette.TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = track.artist,
                            style = Type.MiniTitle,
                            color = Palette.TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongInfoSheet(
    track: Track,
    codec: String?,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = SheetColor) {
        Column(Modifier.fillMaxWidth().padding(bottom = 28.dp)) {
            Text(
                text = track.title,
                style = Type.PlaylistName,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
            )
            Text(
                text = track.artist,
                style = Type.MiniTitle,
                color = Palette.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 28.dp),
            )
            Spacer(Modifier.height(16.dp))
            InfoRow("Album", track.album.ifBlank { "—" })
            InfoRow("Codec", codec ?: "—")
            InfoRow("Length", formatLength(track.duration))
            InfoRow("File", track.path?.substringAfterLast('/') ?: "—")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = Type.MiniTitle,
            color = Palette.TextSecondary,
            modifier = Modifier.width(72.dp),
        )
        Text(
            text = value,
            style = Type.MiniTitle,
            color = Palette.TextPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun formatLength(millis: Long): String {
    if (millis <= 0L) return "—"
    val totalSeconds = millis / 1000
    return "${totalSeconds / 60}:${(totalSeconds % 60).toString().padStart(2, '0')}"
}

@Composable
private fun SheetItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    active: Boolean = false,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) Color.White else Palette.TextSecondary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            style = Type.MiniTitle,
            color = if (active) Color.White else Palette.TextPrimary,
        )
    }
}
