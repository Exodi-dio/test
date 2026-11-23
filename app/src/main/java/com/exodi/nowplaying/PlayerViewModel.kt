package com.exodi.nowplaying

import android.app.Application
import android.content.ComponentName
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PlayerUiState(
    val tracks: List<Track> = emptyList(),
    val index: Int = -1,
    val playing: Boolean = false,
    val position: Long = 0L,
    val duration: Long = 0L,
    val buffering: Boolean = false,
    val shuffle: Boolean = false,
    val repeat: Int = Player.REPEAT_MODE_OFF,
    val scanning: Boolean = true,
) {
    val track: Track? get() = tracks.getOrNull(index)
    val currentDuration: Long get() = duration.takeIf { it > 0L } ?: track?.duration ?: 0L
}

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(PlayerUiState())
    val state: StateFlow<PlayerUiState> = _state.asStateFlow()

    private var controller: MediaController? = null
    private var connection: ListenableFuture<MediaController>? = null
    private var ticker: Job? = null
    private var loadPending = false

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) = sync()
    }

    init {
        connect()
    }

    private fun connect() {
        val context = getApplication<Application>()
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        connection = future

        future.addListener({
            val connected = runCatching { future.get() }.getOrNull() ?: return@addListener
            controller = connected
            connected.addListener(listener)
            if (loadPending) loadLibrary()
            sync()
            startTicker()
        }, ContextCompat.getMainExecutor(context))
    }

    fun loadLibrary() {
        val connected = controller
        if (connected == null) {
            loadPending = true
            return
        }
        loadPending = false
        _state.update { it.copy(scanning = true) }

        viewModelScope.launch {
            val tracks = withContext(Dispatchers.IO) { Library.scan(getApplication()) }
            _state.update { it.copy(tracks = tracks, scanning = false) }
            if (tracks.isEmpty()) return@launch

            val items = tracks.map { track ->
                MediaItem.Builder()
                    .setMediaId(track.id.toString())
                    .setUri(track.uri)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(track.title)
                            .setArtist(track.artist)
                            .setAlbumTitle(track.album)
                            .build()
                    )
                    .build()
            }
            connected.setMediaItems(items)
            connected.prepare()
            connected.play()
        }
    }

    fun playAt(index: Int) {
        val connected = controller ?: return
        connected.seekToDefaultPosition(index)
        connected.play()
    }

    fun togglePlay() {
        val connected = controller ?: return
        if (connected.isPlaying) connected.pause() else connected.play()
    }

    fun next() {
        controller?.takeIf { it.hasNextMediaItem() }?.seekToNextMediaItem()
    }

    fun previous() {
        val connected = controller ?: return
        if (connected.currentPosition > 3_000L || !connected.hasPreviousMediaItem()) {
            connected.seekTo(0L)
        } else {
            connected.seekToPreviousMediaItem()
        }
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs.coerceAtLeast(0L))
    }

    fun toggleShuffle() {
        val connected = controller ?: return
        connected.shuffleModeEnabled = !connected.shuffleModeEnabled
    }

    fun toggleRepeat() {
        val connected = controller ?: return
        connected.repeatMode = when (connected.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
    }

    private fun startTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (true) {
                val connected = controller
                if (connected != null && connected.isPlaying) {
                    _state.update { it.copy(position = connected.currentPosition.coerceAtLeast(0L)) }
                }
                delay(250)
            }
        }
    }

    private fun sync() {
        val connected = controller ?: return
        _state.update { current ->
            current.copy(
                index = connected.currentMediaItemIndex,
                playing = connected.isPlaying,
                position = connected.currentPosition.coerceAtLeast(0L),
                duration = if (connected.duration > 0L) connected.duration else 0L,
                buffering = connected.playbackState == Player.STATE_BUFFERING,
                shuffle = connected.shuffleModeEnabled,
                repeat = connected.repeatMode,
            )
        }
    }

    override fun onCleared() {
        ticker?.cancel()
        controller?.removeListener(listener)
        connection?.let { MediaController.releaseFuture(it) }
        connection = null
        controller = null
        super.onCleared()
    }
}
