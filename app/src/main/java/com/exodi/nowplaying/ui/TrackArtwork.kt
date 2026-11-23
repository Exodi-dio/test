package com.exodi.nowplaying.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.exodi.nowplaying.Artwork
import com.exodi.nowplaying.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TrackArtwork(
    track: Track?,
    modifier: Modifier = Modifier,
    corner: Dp = 24.dp,
) {
    val context = LocalContext.current.applicationContext
    var bitmap by remember(track?.id) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(track?.id) {
        bitmap = null
        bitmap = withContext(Dispatchers.IO) { track?.let { Artwork.load(context, it) } }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(corner))
            .background(Color(0xFF23212D)),
        contentAlignment = Alignment.Center,
    ) {
        val image = bitmap
        if (image != null) {
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = null,
                tint = Color(0xFF4B4858),
                modifier = Modifier.size(64.dp),
            )
        }
    }
}
