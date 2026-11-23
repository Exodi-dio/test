package com.exodi.nowplaying

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Moving a track out of the library: a copy in the Downloads folder, a share
 * sheet and a hand-off to whatever file app the user prefers.
 */
object Transfer {

    fun mimeOf(path: String?): String {
        val extension = path?.substringAfterLast('.', "")?.lowercase().orEmpty()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "audio/*"
    }

    suspend fun saveCopy(context: Context, track: Track): Boolean = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val extension = track.path?.substringAfterLast('.', "mp3")?.lowercase() ?: "mp3"
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "${track.title}.$extension")
            put(MediaStore.Downloads.MIME_TYPE, mimeOf(track.path))
            put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/Now Playing")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val target = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: return@withContext false

        val copied = runCatching {
            val input = resolver.openInputStream(track.uri) ?: return@runCatching false
            val output = resolver.openOutputStream(target) ?: return@runCatching false
            input.use { source -> output.use { sink -> source.copyTo(sink) } }
            true
        }.getOrDefault(false)

        val done = ContentValues().apply {
            put(MediaStore.Downloads.IS_PENDING, if (copied) 0 else 1)
        }
        resolver.update(target, done, null, null)
        if (!copied) resolver.delete(target, null, null)
        copied
    }

    fun share(context: Context, track: Track) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeOf(track.path)
            putExtra(Intent.EXTRA_STREAM, track.uri)
            putExtra(Intent.EXTRA_TITLE, track.title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share ${track.title}"))
    }

    fun openInFiles(context: Context, track: Track) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(track.uri, mimeOf(track.path))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching { context.startActivity(intent) }
    }
}
