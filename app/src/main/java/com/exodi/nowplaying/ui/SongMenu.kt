package com.exodi.nowplaying.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.exodi.nowplaying.Track

private val MenuShape = RoundedCornerShape(27.2.dp)
private val MenuFill = Color(0xBF211E1E)
private val MenuStroke = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.10f),
        Color.White.copy(alpha = 0f),
        Color.White.copy(alpha = 0.20f),
    ),
)

/**
 * The options card that opens from the overflow button. Layout follows the
 * Figma "mobile - song menu" frame: a glass card with a row of quick actions,
 * then three groups separated by hairlines.
 */
@Composable
fun SongMenu(
    track: Track,
    anchor: IntRect,
    artwork: Bitmap?,
    favourite: Boolean,
    onFavourite: () -> Unit,
    onDownload: () -> Unit,
    onAlbum: () -> Unit,
    onMore: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onSongInfo: () -> Unit,
    onPinToTop: () -> Unit,
    onRemove: () -> Unit,
    onOpenFiles: () -> Unit,
    onShare: () -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    var measured by remember { mutableStateOf(IntSize.Zero) }

    val cardWidth = with(density) { 209.dp.toPx() }.toInt()
    val estimatedHeight = with(density) { 380.dp.toPx() }.toInt()
    val cardHeight = if (measured.height > 0) measured.height else estimatedHeight
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }.toInt()
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }.toInt()

    var x = anchor.right - cardWidth
    var y = anchor.top - cardHeight - 8
    if (y < 8) y = anchor.bottom + 8
    x = x.coerceIn(8, (screenWidth - cardWidth - 8).coerceAtLeast(8))
    y = y.coerceIn(8, (screenHeight - cardHeight - 8).coerceAtLeast(8))

    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(x, y),
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Box(
            Modifier
                .width(209.dp)
                .shadow(
                    elevation = 6.4.dp,
                    shape = MenuShape,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f),
                )
                .clip(MenuShape)
                .background(MenuFill)
                .border(0.8.dp, MenuStroke, MenuShape)
                .onSizeChanged { measured = it },
        ) {
            if (artwork != null) {
                Image(
                    bitmap = artwork.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(36.dp)
                        .alpha(0.16f),
                )
            }

            Column(Modifier.padding(horizontal = 12.8.dp, vertical = 8.dp)) {
                Row(Modifier.height(40.dp)) {
                    QuickAction(
                        icon = if (favourite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        label = "Favourite",
                        onClick = onFavourite,
                    )
                    QuickAction(Icons.Rounded.Download, "Save a Copy", onDownload)
                    QuickAction(Icons.Rounded.MusicNote, "Go to Album", onAlbum)
                    QuickAction(Icons.Rounded.MoreHoriz, "More", onMore)
                }

                MenuSeparator()

                MenuItem(Icons.Rounded.PlaylistPlay, "Play Next", "Up Next", onPlayNext)
                MenuItem(Icons.Rounded.PlaylistAdd, "Add to Queue", "End of Queue", onAddToQueue)
                MenuItem(Icons.Rounded.Info, "Song Info", "Metadata", onSongInfo, chevron = true)

                MenuSeparator()

                MenuItem(Icons.Rounded.PushPin, "Pin to Top", "Start of Queue", onPinToTop)
                MenuItem(Icons.Rounded.PlaylistRemove, "Remove from Queue", "This Session", onRemove)

                MenuSeparator()

                MenuItem(Icons.Rounded.FolderOpen, "Show in Files", "Open Folder", onOpenFiles)
                MenuItem(Icons.Rounded.Share, "Share", "Send File", onShare)
            }
        }
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .width(42.76.dp)
            .fillMaxSize()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(17.dp),
        )
    }
}

@Composable
private fun MenuSeparator() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(8.8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(0.8.dp)
                .background(Color.White.copy(alpha = 0.12f)),
        )
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    chevron: Boolean = false,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(42.76.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(17.dp),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = Type.MenuTitle,
                color = Color.White.copy(alpha = 0.8f),
            )
            Text(
                text = subtitle,
                style = Type.MenuSubtitle,
                color = Color.White.copy(alpha = 0.5f),
            )
        }
        if (chevron) {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(12.dp),
            )
        }
    }
}
