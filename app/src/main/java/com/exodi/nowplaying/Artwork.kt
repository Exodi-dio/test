package com.exodi.nowplaying

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.LruCache

object Artwork {

    private val cache = LruCache<Long, Bitmap>(32)

    fun load(context: Context, track: Track): Bitmap? {
        cache.get(track.id)?.let { return it }

        val bitmap = runCatching {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, track.uri)
                retriever.embeddedPicture?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            } finally {
                retriever.release()
            }
        }.getOrNull()

        if (bitmap != null) cache.put(track.id, bitmap)
        return bitmap
    }
}
