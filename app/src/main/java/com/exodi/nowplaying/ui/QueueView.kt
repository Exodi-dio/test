package com.exodi.nowplaying.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.exodi.nowplaying.Track

@Composable
fun QueueView(
    albumName: String,
    albumArtwork: Track?,
    queue: List<Track>,
    currentId: Long?,
    onPlay: (Track) -> Unit,
    onAlbum: () -> Unit,
    onRowMenu: (Track) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 21.dp, end = 21.dp, top = 20.dp, bottom = 24.dp),
    ) {
        item(key = "source") {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAlbum)
                    .padding(vertical = 6.dp),
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
                        style = Type.Label,
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
                modifier = Modifier.padding(top = 22.dp, bottom = 12.dp),
            )
        }

        itemsIndexed(queue, key = { _, track -> track.id }) { _, track ->
            QueueRow(
                track = track,
                current = track.id == currentId,
                onPlay = { onPlay(track) },
                onMenu = { onRowMenu(track) },
            )
        }
    }
}

@Composable
private fun QueueRow(
    track: Track,
    current: Boolean,
    onPlay: () -> Unit,
    onMenu: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TrackArtwork(track, Modifier.size(42.dp), corner = 6.dp)
        Spacer(Modifier.width(12.dp))
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
                style = Type.MiniTitle,
                color = Palette.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = Icons.Rounded.MoreHoriz,
            contentDescription = "Options",
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onMenu),
        )
    }
}
