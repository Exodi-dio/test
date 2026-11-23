package com.exodi.nowplaying

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.util.LruCache
import androidx.compose.ui.graphics.Color

/**
 * Real GPU blur for the backdrop, rendered once per track and cached.
 */
object ArtworkBlur {

    private val cache = LruCache<String, Bitmap>(4)

    fun blurred(
        context: Context,
        track: Track,
        radiusPx: Float,
        width: Int,
        height: Int,
    ): Bitmap? {
        val key = "${track.id}-$width-$height"
        cache.get(key)?.let { return it }

        val source = Artwork.load(context, track) ?: return null
        val scaled = Bitmap.createScaledBitmap(source, width, height, true)
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        paint.setRenderEffect(RenderEffect.createBlurEffect(radiusPx, radiusPx, Shader.TileMode.CLAMP))
        canvas.drawBitmap(scaled, 0f, 0f, paint)

        cache.put(key, output)
        return output
    }
}

/**
 * Two colours sampled from the cover so the wash follows the artwork.
 */
object ArtTint {

    private val cache = LruCache<Long, Pair<Color, Color>>(8)

    fun of(art: Bitmap?): Pair<Color, Color> {
        if (art == null) return Color(0xFF2878B7) to Color(0xFF4FAFF0)

        art.hashCode().let { key ->
            cache.get(key.toLong())?.let { return it }
        }

        val small = Bitmap.createScaledBitmap(art, 12, 12, true)
        var r = 0L
        var g = 0L
        var b = 0L
        val pixels = IntArray(12 * 12)
        small.getPixels(pixels, 0, 12, 0, 0, 12, 12)
        pixels.forEach { pixel ->
            r += (pixel shr 16) and 0xFF
            g += (pixel shr 8) and 0xFF
            b += pixel and 0xFF
        }
        val count = pixels.size
        val top = Color(r / count / 255f * 0.62f, g / count / 255f * 0.62f, b / count / 255f * 0.68f)
        val bottom = Color(
            (r / count / 255f * 0.36f + 0.42f).coerceAtMost(1f),
            (g / count / 255f * 0.36f + 0.47f).coerceAtMost(1f),
            (b / count / 255f * 0.36f + 0.55f).coerceAtMost(1f),
        )
        val pair = top to bottom
        cache.put(art.hashCode().toLong(), pair)
        return pair
    }
}
