package com.exodi.nowplaying

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.exodi.nowplaying.ui.NowPlayingScreen
import com.exodi.nowplaying.ui.NowPlayingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NowPlayingTheme {
                val context = LocalContext.current
                val player: PlayerViewModel = viewModel()

                val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

                var granted by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(context, audioPermission) == PackageManager.PERMISSION_GRANTED
                    )
                }

                val notifications = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { }

                val requestAudio = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { allowed ->
                    granted = allowed
                    if (allowed) {
                        player.loadLibrary()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notifications.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    if (granted) player.loadLibrary() else requestAudio.launch(audioPermission)
                }

                NowPlayingScreen(
                    player = player,
                    permissionGranted = granted,
                    onRequestPermission = { requestAudio.launch(audioPermission) },
                )
            }
        }
    }
}
