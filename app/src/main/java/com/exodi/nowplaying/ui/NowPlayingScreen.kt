package com.exodi.nowplaying.ui

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.mediarouter.app.MediaRouteDialogFactory
import com.exodi.nowplaying.Artwork
import com.exodi.nowplaying.ArtworkBlur
import com.exodi.nowplaying.ArtTint
import com.exodi.nowplaying.Library
import com.exodi.nowplaying.Lyrics
import com.exodi.nowplaying.LyricsLoader
import com.exodi.nowplaying.PlayerViewModel
import com.exodi.nowplaying.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun NowPlayingScreen(
    player: PlayerViewModel,
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit,
) {
    val state by player.state.collectAsStateWithLifecycle()
    val favourites by player.favourites.ids.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    var mode by remember { mutableStateOf(PlayerMode.Big) }
    var showMore by remember { mutableStateOf(false) }
    var menuTrack by remember { mutableStateOf<Track?>(null) }
    var showAlbum by remember { mutableStateOf(false) }
    var showSleep by remember { mutableStateOf(false) }

    val track = state.track

    val artwork by produceState<Bitmap?>(null, track?.id) {
        value = track?.let { withContext(Dispatchers.IO) { Artwork.load(context, it) } }
    }

    val artWidthPx = with(density) { (configuration.screenWidthDp * 1.6f).roundToPx() }
    val artHeightPx = (artWidthPx * (600f / 642f)).toInt()
    val radiusPx = with(density) { 86.4.dp.toPx() }
    val blurred by produceState<Bitmap?>(null, track?.id, artWidthPx) {
        value = track?.let {
            withContext(Dispatchers.IO) {
                ArtworkBlur.blurred(context, it, radiusPx, artWidthPx, artHeightPx)
            }
        }
    }
    val wash = remember(artwork) { ArtTint.of(artwork) }

    var lyrics by remember { mutableStateOf<Lyrics?>(null) }
    LaunchedEffect(track?.id) {
        lyrics = track?.let { withContext(Dispatchers.IO) { LyricsLoader.load(context, it) } }
    }

    val albumTracks by produceState<List<Track>?>(null, showAlbum, track?.albumId) {
        value = if (showAlbum && track != null) {
            withContext(Dispatchers.IO) { Library.albumTracks(context, track.albumId) }
        } else {
            null
        }
    }

    val queue = remember(state.tracks, state.index) {
        val upcoming = state.tracks.drop((state.index + 1).coerceAtLeast(0))
        if (upcoming.isEmpty()) state.tracks else upcoming
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF101014))) {
        ArtworkBackdrop(artwork = artwork, blurred = blurred, wash = wash)

        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when {
                    !permissionGranted -> CenteredMessage(
                        text = "Now Playing needs access to the audio files on this device.",
                        action = "Allow access",
                        onAction = onRequestPermission,
                    )

                    state.scanning -> CenteredMessage(text = "Looking for music…", action = null, onAction = null)

                    state.tracks.isEmpty() -> CenteredMessage(
                        text = "No music found on this device.",
                        action = "Scan again",
                        onAction = player::loadLibrary,
                    )

                    else -> AnimatedContent(
                        targetState = mode,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "playerMode",
                    ) { target ->
                        when (target) {
                            PlayerMode.Big -> Box(Modifier.fillMaxSize())

                            PlayerMode.Lyrics -> LyricsView(
                                lyrics = lyrics,
                                position = state.position,
                                onSeek = player::seekTo,
                            )

                            PlayerMode.Queue -> QueueView(
                                albumName = track?.album.orEmpty().ifBlank { "Your library" },
                                albumArtwork = track,
                                queue = queue,
                                currentId = track?.id,
                                onPlay = {
                                    player.play(it)
                                    mode = PlayerMode.Big
                                },
                                onAlbum = { showAlbum = true },
                                onRowMenu = { menuTrack = it },
                            )
                        }
                    }
                }
            }

            if (permissionGranted && state.tracks.isNotEmpty() && !state.scanning) {
                PlayerBlock(
                    state = state,
                    track = track,
                    mode = mode,
                    favourite = track != null && track.id in favourites,
                    codec = player.codecOf(track),
                    onLyrics = { mode = if (mode == PlayerMode.Lyrics) PlayerMode.Big else PlayerMode.Lyrics },
                    onQueue = { mode = if (mode == PlayerMode.Queue) PlayerMode.Big else PlayerMode.Queue },
                    onMore = { showMore = true },
                    onAlbum = { showAlbum = true },
                    onFavourite = { track?.let { player.favourites.toggle(it.id) } },
                    onTogglePlay = player::togglePlay,
                    onPrevious = player::previous,
                    onNext = player::next,
                    onShuffle = player::toggleShuffle,
                    onAirPlay = {
                        runCatching {
                            MediaRouteDialogFactory(context).onCreateChooserDialog().show()
                        }
                    },
                    onSeek = player::seekTo,
                )
            }
        }
    }

    if (showMore) {
        MoreMenuSheet(
            favourite = track != null && track.id in favourites,
            shuffle = state.shuffle,
            repeatOne = state.repeat == androidx.media3.common.Player.REPEAT_MODE_ONE,
            onFavourite = { track?.let { player.favourites.toggle(it.id) } },
            onShuffle = {
                player.toggleShuffle()
                showMore = false
            },
            onRepeat = {
                player.toggleRepeat()
                showMore = false
            },
            onSleepTimer = {
                showMore = false
                showSleep = true
            },
            onAlbum = {
                showMore = false
                showAlbum = true
            },
            onDismiss = { showMore = false },
        )
    }

    menuTrack?.let { target ->
        TrackMenuSheet(
            track = target,
            favourite = target.id in favourites,
            onPlayNext = {
                player.playNext(target)
                menuTrack = null
            },
            onFavourite = {
                player.favourites.toggle(target.id)
                menuTrack = null
            },
            onAlbum = {
                menuTrack = null
                showAlbum = true
            },
            onDismiss = { menuTrack = null },
        )
    }

    if (showSleep) {
        SleepTimerSheet(
            onSelect = { minutes ->
                player.startSleepTimer(minutes)
                showSleep = false
            },
            onDismiss = { showSleep = false },
        )
    }

    if (showAlbum) {
        AlbumSheet(
            albumName = track?.album.orEmpty().ifBlank { "Your library" },
            tracks = albumTracks.orEmpty(),
            currentId = track?.id,
            onPlay = {
                player.play(it)
                showAlbum = false
                mode = PlayerMode.Big
            },
            onDismiss = { showAlbum = false },
        )
    }
}

@Composable
private fun CenteredMessage(text: String, action: String?, onAction: (() -> Unit)?) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                style = Type.MiniTitle,
                color = Palette.TextSecondary,
                textAlign = TextAlign.Center,
            )
            if (action != null && onAction != null) {
                Button(onClick = onAction, modifier = Modifier.padding(top = 16.dp)) {
                    Text(action)
                }
            }
        }
    }
}
