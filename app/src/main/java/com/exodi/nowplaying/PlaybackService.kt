package com.exodi.nowplaying

import android.content.Intent
import android.media.AudioDeviceInfo
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService : MediaSessionService() {

    private var session: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
        session = MediaSession.Builder(this, player).build()
        activePlayer = player
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = session?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        session?.run {
            player.release()
            release()
        }
        session = null
        activePlayer = null
        super.onDestroy()
    }

    companion object {
        @Volatile
        private var activePlayer: ExoPlayer? = null

        /**
         * Sends the playback to a different output, such as a pair of
         * headphones or a Bluetooth speaker.
         */
        fun setPreferredAudioDevice(device: AudioDeviceInfo?) {
            activePlayer?.setPreferredAudioDevice(device)
        }
    }
}
