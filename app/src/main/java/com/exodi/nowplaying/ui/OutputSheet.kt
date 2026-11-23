package com.exodi.nowplaying.ui

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.exodi.nowplaying.PlaybackService

/**
 * Lists the output devices the system knows about and routes the playback to
 * the one that gets picked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutputDeviceSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    val devices = remember {
        audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            .filter { it.isSink() }
            .distinctBy { it.id }
            .sortedBy { it.type }
    }
    val active = remember { audioManager.activePlaybackConfigurations.firstOrNull()?.audioDevice }

    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = SheetColor) {
        Column(Modifier.fillMaxWidth().padding(bottom = 28.dp)) {
            Text(
                text = "Play on",
                style = Type.PlaylistName,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
            )
            devices.forEach { device ->
                val selected = active != null && device.id == active.id
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            PlaybackService.setPreferredAudioDevice(device)
                            onDismiss()
                        }
                        .padding(horizontal = 28.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = iconFor(device.type),
                        contentDescription = null,
                        tint = if (selected) Color.White else Palette.TextSecondary,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = device.productName?.toString().orEmpty().ifBlank { "Audio output" },
                        style = Type.MiniTitle,
                        color = if (selected) Color.White else Palette.TextPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    if (selected) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
            if (devices.isEmpty()) {
                Text(
                    text = "No other output devices found.",
                    style = Type.MiniTitle,
                    color = Palette.TextSecondary,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun AudioDeviceInfo.isSink(): Boolean = when (type) {
    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
    AudioDeviceInfo.TYPE_BUILTIN_EARPIECE,
    AudioDeviceInfo.TYPE_WIRED_HEADSET,
    AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
    AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
    AudioDeviceInfo.TYPE_USB_DEVICE,
    AudioDeviceInfo.TYPE_USB_HEADSET,
    AudioDeviceInfo.TYPE_HDMI,
    AudioDeviceInfo.TYPE_HDMI_ARC,
    AudioDeviceInfo.TYPE_AUX_LINE,
    -> true

    else -> false
}

private fun iconFor(type: Int): ImageVector = when (type) {
    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
    AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
    -> Icons.Rounded.Bluetooth

    AudioDeviceInfo.TYPE_WIRED_HEADSET,
    AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
    AudioDeviceInfo.TYPE_USB_HEADSET,
    -> Icons.Rounded.Headphones

    AudioDeviceInfo.TYPE_HDMI,
    AudioDeviceInfo.TYPE_HDMI_ARC,
    -> Icons.Rounded.Tv

    AudioDeviceInfo.TYPE_USB_DEVICE,
    -> Icons.Rounded.Usb

    else -> Icons.Rounded.Speaker
}
