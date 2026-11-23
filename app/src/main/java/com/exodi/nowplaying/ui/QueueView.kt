package com.exodi.nowplaying.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import com.exodi.nowplaying.Track

@Composable
fun QueueView(
    albumName: String,
    albumArtwork: Track?,
    queue: List<Track>,
    currentId: Long?,
    favourites: Set<Long>,
    onPlay: (Track) -> Unit,
    onAlbum: () -> Unit,
    onFavourite: (Track) -> Unit,
    onDownload: (Track) -> Unit,
    onRowMenu: (Track, IntRect) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 21.dp, end = 21.dp, top = 20.dp, bottom = 24.dp),
    ) {
        item(key = "source") {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable(onClick = onAlbum),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TrackArtwork(
                    track = albumArtwork,
                    modifier = Modifier.size(34.dp),
                    corner = 6.dp,
                )
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Playing from",
                        style = Type.SourceLabel,
                        color = Palette.TextSecondary,
                    )
                    Text(
                        text = albumName,
                        style = Type.PlaylistName,
                        color = Palette.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        item(key = "label") {
            Text(
                text = "Playing Next:",
                style = Type.Label,
                color = Palette.TextSecondary,
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
            )
        }

        itemsIndexed(queue, key = { _, track -> track.id }) { index, track ->
            QueueRow(
                track = track,
                current = track.id == currentId,
                favourite = track.id in favourites,
                onPlay = { onPlay(track) },
                onFavourite = { onFavourite(track) },
                onDownload = { onDownload(track) },
                onMenu = { bounds -> onRowMenu(track, bounds) },
            )
            if (index < queue.lastIndex) Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QueueRow(
    track: Track,
    current: Boolean,
    favourite: Boolean,
    onPlay: () -> Unit,
    onFavourite: () -> Unit,
    onDownload: () -> Unit,
    onMenu: (IntRect) -> Unit,
) {
    var bounds by remember { mutableStateOf(IntRect.Zero) }

    Row(
        Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clickable(onClick = onPlay),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TrackArtwork(track, Modifier.size(34.dp), corner = 6.dp)
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = Type.PlaylistName,
                color = if (current) Color.White else Palette.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = track.artist,
                style = Type.SourceLabel,
                color = Palette.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        RowIcon(
            icon = Icons.Rounded.Download,
            label = "Save a Copy",
            onClick = onDownload,
        )
        RowIcon(
            icon = if (favourite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            label = "Favourite",
            onClick = onFavourite,
            tint = if (favourite) Color.White else Color.White.copy(alpha = 0.5f),
        )
        Box(
            Modifier
                .size(24.dp)
                .onGloballyPositioned { bounds = it.boundsInWindow() }
                .clickable { onMenu(bounds) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreHoriz,
                contentDescription = "Options",
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun RowIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = Color.White.copy(alpha = 0.5f),
) {
    Box(
        Modifier
            .size(24.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
    }
}
