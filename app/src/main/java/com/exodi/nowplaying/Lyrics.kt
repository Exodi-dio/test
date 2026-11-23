package com.exodi.nowplaying

import android.content.Context
import java.io.File
import java.nio.charset.Charset

data class LyricWord(val timeMs: Long, val text: String)

data class LyricLine(
    val timeMs: Long,
    val text: String,
    val words: List<LyricWord> = emptyList(),
)

data class Lyrics(
    val lines: List<LyricLine>,
    val synced: Boolean,
)

object LyricsLoader {

    fun load(context: Context, track: Track): Lyrics? {
        sidecar(track)?.let { file ->
            runCatching { file.readText() }.getOrNull()
                ?.let { LrcParser.parse(it) }
                ?.let { return it }
        }

        val file = audioFile(context, track) ?: return null
        val raw = EmbeddedTags.lyrics(file) ?: return null
        return LrcParser.parse(raw)
    }

    private fun sidecar(track: Track): File? {
        val audio = track.path?.let(::File) ?: return null
        val parent = audio.parentFile ?: return null
        val base = audio.nameWithoutExtension
        return listOf("$base.lrc", "$base.LRC")
            .map { File(parent, it) }
            .firstOrNull { it.exists() }
    }

    private fun audioFile(context: Context, track: Track): File? {
        track.path?.let { path ->
            val file = File(path)
            if (file.exists()) return file
        }

        val cached = File(context.cacheDir, "tag_${track.id}")
        if (!cached.exists() || cached.length() == 0L) {
            runCatching {
                context.contentResolver.openInputStream(track.uri)?.use { input ->
                    cached.outputStream().use { output -> input.copyTo(output) }
                }
            }
        }
        return cached.takeIf { it.length() > 0L }
    }
}

object LrcParser {

    private val lineTime = Regex("\\[(\\d{1,3}):(\\d{1,2})(?:[.:](\\d{1,3}))?]")
    private val wordTime = Regex("<(\\d{1,3}):(\\d{1,2})(?:[.:](\\d{1,3}))?>")

    fun parse(raw: String): Lyrics? {
        val stamped = mutableListOf<LyricLine>()
        val plain = mutableListOf<String>()

        raw.lineSequence().forEach { source ->
            val line = source.trim()
            if (line.isEmpty()) return@forEach

            val marks = lineTime.findAll(line).toList()
            if (marks.isEmpty()) {
                if (!line.startsWith("[")) plain += line
                return@forEach
            }

            val content = line.substring(marks.last().range.last + 1).trim()
            if (content.isEmpty()) return@forEach

            val first = timeOf(marks.first())
            val words = parseWords(content, first)
            val text = words.joinToString("") { it.text }.trim()
            if (text.isEmpty()) return@forEach

            marks.forEach { mark -> stamped += LyricLine(timeOf(mark), text, words) }
        }

        if (stamped.isNotEmpty()) {
            return Lyrics(stamped.sortedBy { it.timeMs }, synced = true)
        }
        if (plain.isEmpty()) return null
        return Lyrics(plain.map { LyricLine(0, it) }, synced = false)
    }

    private fun parseWords(content: String, fallback: Long): List<LyricWord> {
        val marks = wordTime.findAll(content).toList()
        if (marks.isEmpty()) return emptyList()

        val words = mutableListOf<LyricWord>()
        val lead = content.substring(0, marks.first().range.first).trim()
        if (lead.isNotEmpty()) words += LyricWord(fallback, lead)

        marks.forEachIndexed { index, mark ->
            val start = mark.range.last + 1
            val end = if (index + 1 < marks.size) marks[index + 1].range.first else content.length
            val text = content.substring(start, end)
            if (text.isNotBlank()) words += LyricWord(timeOf(mark), text)
        }
        return words
    }

    private fun timeOf(match: MatchResult): Long {
        val minutes = match.groupValues[1].toLong()
        val seconds = match.groupValues[2].toLong()
        val fraction = match.groupValues[3]
        val millis = when (fraction.length) {
            0 -> 0L
            1 -> fraction.toLong() * 100
            2 -> fraction.toLong() * 10
            else -> fraction.take(3).toLong()
        }
        return (minutes * 60 + seconds) * 1000 + millis
    }
}

object EmbeddedTags {

    private val lyricsKeys = listOf("LYRICS", "UNSYNCEDLYRICS", "SYNCEDLYRICS")

    fun lyrics(file: File): String? = runCatching {
        val bytes = file.readBytes()
        when {
            bytes.startsWith("ID3") -> id3(bytes)
            bytes.startsWith("fLaC") -> flac(bytes)
            bytes.startsWith("OggS") -> ogg(bytes)
            bytes.size > 12 && bytes.startsWith("ftyp", 4) -> mp4(bytes)
            else -> null
        }
    }.getOrNull()

    private fun id3(bytes: ByteArray): String? {
        val version = bytes[3].toInt()
        val flags = bytes[5].toInt()
        val tagEnd = minOf(bytes.size, 10 + syncSafe(bytes, 6))

        var position = 10
        if (flags and 0x40 != 0) {
            position += if (version == 4) syncSafe(bytes, position) else 4 + beInt(bytes, position)
        }

        while (position + 10 <= tagEnd) {
            val id = String(bytes, position, 4, Charsets.ISO_8859_1)
            if (id[0] == '\u0000') break

            val size = if (version == 4) syncSafe(bytes, position + 4) else beInt(bytes, position + 4)
            if (size <= 0 || position + 10 + size > tagEnd) break

            val frame = bytes.copyOfRange(position + 10, position + 10 + size)
            when (id) {
                "USLT" -> frame.described(4)?.second?.let { return it }
                "TXXX" -> frame.described(1)?.let { (key, value) ->
                    if (key.uppercase() in lyricsKeys && value.isNotEmpty()) return value
                }
            }
            position += 10 + size
        }
        return null
    }

    private fun ByteArray.described(start: Int): Pair<String, String>? {
        if (isEmpty() || start >= size) return null
        val encoding = this[0].toInt()
        val charset = when (encoding) {
            0 -> Charsets.ISO_8859_1
            1 -> Charsets.UTF_16
            2 -> Charsets.UTF_16BE
            else -> Charsets.UTF_8
        }

        var cursor = start
        if (encoding == 1 || encoding == 2) {
            while (cursor + 1 < size && !(this[cursor] == 0.toByte() && this[cursor + 1] == 0.toByte())) cursor += 2
            if (cursor + 1 >= size) return null
            val description = String(this, start, cursor - start, charset)
            cursor += 2
            if (cursor >= size) return null
            val value = String(this, cursor, size - cursor, charset)
            return description.trim('\u0000') to value.trim('\u0000', '\n', '\r', ' ')
        }

        while (cursor < size && this[cursor] != 0.toByte()) cursor++
        if (cursor >= size) return null
        val description = String(this, start, cursor - start, charset)
        cursor += 1
        if (cursor >= size) return null
        val value = String(this, cursor, size - cursor, charset)
        return description.trim('\u0000') to value.trim('\u0000', '\n', '\r', ' ')
    }

    private fun flac(bytes: ByteArray): String? {
        var position = 4
        while (position + 4 <= bytes.size) {
            val header = bytes[position].toInt() and 0xFF
            val last = header and 0x80 != 0
            val type = header and 0x7F
            val length = ((bytes[position + 1].toInt() and 0xFF) shl 16) or
                ((bytes[position + 2].toInt() and 0xFF) shl 8) or
                (bytes[position + 3].toInt() and 0xFF)
            val start = position + 4
            if (start + length > bytes.size) break
            if (type == 4) return vorbisComments(bytes, start, start + length)
            if (last) break
            position = start + length
        }
        return null
    }

    private fun ogg(bytes: ByteArray): String? {
        val opus = bytes.findSequence("OpusTags")
        if (opus >= 0) return vorbisComments(bytes, opus + 8, bytes.size)

        val vorbis = bytes.findSequence(
            byteArrayOf(3, 'v'.code.toByte(), 'o'.code.toByte(), 'r'.code.toByte(), 'b'.code.toByte(), 'i'.code.toByte(), 's'.code.toByte())
        )
        if (vorbis >= 0) return vorbisComments(bytes, vorbis + 7, bytes.size)
        return null
    }

    private fun vorbisComments(bytes: ByteArray, start: Int, end: Int): String? {
        var position = start
        if (position + 4 > end) return null
        val vendorLength = leInt(bytes, position)
        if (vendorLength < 0) return null
        position += 4 + vendorLength
        if (position + 4 > end) return null

        val count = leInt(bytes, position)
        position += 4
        var found: String? = null

        repeat(count.coerceIn(0, 10_000)) {
            if (position + 4 > end) return found
            val length = leInt(bytes, position)
            if (length < 0) return found
            position += 4
            if (position + length > end) return found

            val comment = String(bytes, position, length, Charsets.UTF_8)
            position += length
            val separator = comment.indexOf('=')
            if (separator > 0) {
                val key = comment.substring(0, separator).uppercase()
                if (key in lyricsKeys && found == null) found = comment.substring(separator + 1)
            }
        }
        return found
    }

    private fun mp4(bytes: ByteArray): String? {
        val marker = bytes.findSequence(byteArrayOf(0xA9.toByte(), 'l'.code.toByte(), 'y'.code.toByte(), 'r'.code.toByte()))
        if (marker < 0) return null

        val dataAtom = marker + 4
        if (dataAtom + 16 > bytes.size) return null
        if (String(bytes, dataAtom + 4, 4, Charsets.ISO_8859_1) != "data") return null

        val textStart = dataAtom + 16
        return String(bytes, textStart, bytes.size - textStart, Charsets.UTF_8)
            .substringBefore('\u0000')
            .trim()
            .takeIf { it.isNotEmpty() }
    }

    private fun ByteArray.startsWith(text: String, offset: Int = 0): Boolean {
        if (offset + text.length > size) return false
        for (index in text.indices) {
            if (this[offset + index] != text[index].code.toByte()) return false
        }
        return true
    }

    private fun ByteArray.findSequence(text: String): Int =
        findSequence(text.toByteArray(Charsets.ISO_8859_1))

    private fun ByteArray.findSequence(needle: ByteArray): Int {
        if (needle.isEmpty() || needle.size > size) return -1
        outer@ for (index in 0..size - needle.size) {
            for (offset in needle.indices) {
                if (this[index + offset] != needle[offset]) continue@outer
            }
            return index
        }
        return -1
    }

    private fun syncSafe(bytes: ByteArray, offset: Int): Int =
        ((bytes[offset].toInt() and 0x7F) shl 21) or
            ((bytes[offset + 1].toInt() and 0x7F) shl 14) or
            ((bytes[offset + 2].toInt() and 0x7F) shl 7) or
            (bytes[offset + 3].toInt() and 0x7F)

    private fun beInt(bytes: ByteArray, offset: Int): Int =
        ((bytes[offset].toInt() and 0xFF) shl 24) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
            (bytes[offset + 3].toInt() and 0xFF)

    private fun leInt(bytes: ByteArray, offset: Int): Int =
        (bytes[offset].toInt() and 0xFF) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 3].toInt() and 0xFF) shl 24)
}
