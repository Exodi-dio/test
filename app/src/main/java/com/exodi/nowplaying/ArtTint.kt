package com.exodi.nowplaying

import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.ui.graphics.Color

/**
 * Two colours sampled from the cover so the wash follows the artwork.
 */
object ArtTint {

    private val cache = LruCache<Long, Pair<Color, Color>>(8)

    fun of(art: Bitmap?): Pair<Color, Color> {
        if (art == null) return Color(0xFF2878B7) to Color(0xFF4FAFF0)

        val key = art.hashCode().toLong()
        cache.get(key)?.let { return it }

        val small = Bitmap.createScaledBitmap(art, 12, 12, true)
        val pixels = IntArray(12 * 12)
        small.getPixels(pixels, 0, 12, 0, 0, 12, 12)

        var red = 0L
        var green = 0L
        var blue = 0L
        pixels.forEach { pixel ->
            red += (pixel shr 16) and 0xFF
            green += (pixel shr 8) and 0xFF
            blue += pixel and 0xFF
        }

        val count = pixels.size
        val top = Color(
            red / count / 255f * 0.62f,
            green / count / 255f * 0.62f,
            blue / count / 255f * 0.68f,
        )
        val bottom = Color(
            (red / count / 255f * 0.36f + 0.42f).coerceAtMost(1f),
            (green / count / 255f * 0.36f + 0.47f).coerceAtMost(1f),
            (blue / count / 255f * 0.36f + 0.55f).coerceAtMost(1f),
        )

        val pair = top to bottom
        cache.put(key, pair)
        return pair
    }
}
