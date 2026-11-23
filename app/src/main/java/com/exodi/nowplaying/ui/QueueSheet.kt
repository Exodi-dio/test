package com.exodi.nowplaying.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.exodi.nowplaying.PlayerUiState
import com.exodi.nowplaying.Track

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueSheet(
    state: PlayerUiState,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.fillMaxHeight(0.75f)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Queue", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "${state.tracks.size} tracks",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            LazyColumn(
                Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 32.dp),
            ) {
                itemsIndexed(state.tracks, key = { _, track -> track.id }) { index, track ->
                    QueueRow(
                        track = track,
                        current = index == state.index,
                        playing = index == state.index && state.playing,
                        onClick = { onSelect(index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueRow(
    track: Track,
    current: Boolean,
    playing: Boolean,
    onClick: () -> Unit,
) {
    val titleColor = if (current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TrackArtwork(track, Modifier.size(44.dp), corner = 10.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyMedium,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = track.artist,
                style = MaterialTheme.typography.labelSmall,
                color = subtitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (current) {
            Spacer(Modifier.width(12.dp))
            Text(
                text = if (playing) "Playing" else "Paused",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
