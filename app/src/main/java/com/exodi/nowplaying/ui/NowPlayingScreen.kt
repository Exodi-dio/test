package com.exodi.nowplaying.ui

import android.graphics.Bitmap
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exodi.nowplaying.Artwork
import com.exodi.nowplaying.ArtTint
import com.exodi.nowplaying.Library
import com.exodi.nowplaying.Lyrics
import com.exodi.nowplaying.LyricsLoader
import com.exodi.nowplaying.PlayerViewModel
import com.exodi.nowplaying.Track
import com.exodi.nowplaying.Transfer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf(PlayerMode.Big) }
    var showMore by remember { mutableStateOf(false) }
    var menuTrack by remember { mutableStateOf<Track?>(null) }
    var menuAnchor by remember { mutableStateOf(IntRect.Zero) }
    var showAlbum by remember { mutableStateOf(false) }
    var showSleep by remember { mutableStateOf(false) }
    var showOutputs by remember { mutableStateOf(false) }
    var infoTrack by remember { mutableStateOf<Track?>(null) }

    val track = state.track

    val artwork by produceState<Bitmap?>(null, track?.id) {
        value = track?.let { withContext(Dispatchers.IO) { Artwork.load(context, it) } }
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

    val downloadTrack: (Track) -> Unit = { target ->
        scope.launch {
            val saved = Transfer.saveCopy(context, target)
            Toast.makeText(
                context,
                if (saved) "Saved to Downloads" else "Could not save the copy",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF101014))) {
        ArtworkBackdrop(artwork = artwork, wash = wash)

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
                                favourites = favourites,
                                onPlay = {
                                    player.play(it)
                                    mode = PlayerMode.Big
                                },
                                onAlbum = { showAlbum = true },
                                onFavourite = { player.favourites.toggle(it.id) },
                                onDownload = downloadTrack,
                                onRowMenu = { target, bounds ->
                                    menuTrack = target
                                    menuAnchor = bounds
                                },
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
                    onAirPlay = { showOutputs = true },
                    onSeek = player::seekTo,
                )
            }
        }
    }

    menuTrack?.let { target ->
        SongMenu(
            track = target,
            anchor = menuAnchor,
            artwork = artwork,
            favourite = target.id in favourites,
            onFavourite = {
                player.favourites.toggle(target.id)
                menuTrack = null
            },
            onDownload = {
                menuTrack = null
                downloadTrack(target)
            },
            onAlbum = {
                menuTrack = null
                showAlbum = true
            },
            onMore = {
                menuTrack = null
                showMore = true
            },
            onPlayNext = {
                player.playNext(target)
                menuTrack = null
            },
            onAddToQueue = {
                player.addToQueue(target)
                menuTrack = null
            },
            onSongInfo = {
                menuTrack = null
                infoTrack = target
            },
            onPinToTop = {
                player.pinToTop(target)
                menuTrack = null
            },
            onRemove = {
                player.removeFromQueue(target)
                menuTrack = null
            },
            onOpenFiles = {
                menuTrack = null
                Transfer.openInFiles(context, target)
            },
            onShare = {
                menuTrack = null
                Transfer.share(context, target)
            },
        )
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

    if (showOutputs) {
        OutputDeviceSheet(onDismiss = { showOutputs = false })
    }

    infoTrack?.let { target ->
        SongInfoSheet(
            track = target,
            codec = player.codecOf(target),
            onDismiss = { infoTrack = null },
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
