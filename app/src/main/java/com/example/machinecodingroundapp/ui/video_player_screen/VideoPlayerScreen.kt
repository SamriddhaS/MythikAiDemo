package com.example.machinecodingroundapp.ui.video_player_screen

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayerScreen(
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    HandleFullScreen(state)

    when (val ui = state) {
        is VideoPlayerUiState.Loading -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier
                    .size(78.dp)
                    .align(Alignment.Center)
                )
            }
        }

        is VideoPlayerUiState.Error -> {
            Box(Modifier.fillMaxSize()) {
                Text("Error: ${ui.message}", modifier = Modifier.fillMaxSize())
            }
        }

        is VideoPlayerUiState.Loaded -> {

            // Handle back button press to toggle fullscreen
            BackHandler(enabled = ui.isFullScreen) {
                viewModel.onEvent(VideoPlayerAction.ToggleFullscreen)
            }

            Box(Modifier.fillMaxSize()) {

                VideoPlayerView(
                    viewModel = viewModel
                )

                if (ui.isBuffering) {
                    CircularProgressIndicator(Modifier
                        .size(78.dp)
                        .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun VideoPlayerView(
    viewModel: VideoPlayerViewModel
) {
    AndroidView(
        factory = {
            PlayerView(it).apply {
                this.player = viewModel.player
                this.setFullscreenButtonClickListener {
                    viewModel.onEvent(VideoPlayerAction.ToggleFullscreen)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun HandleFullScreen(state: VideoPlayerUiState) {
    val context = LocalContext.current
    val isFullScreen = (state as? VideoPlayerUiState.Loaded)?.isFullScreen ?: return
    LaunchedEffect(isFullScreen) {
        val activity = context as? Activity ?: return@LaunchedEffect
        val window = activity.window ?: return@LaunchedEffect
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (isFullScreen) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}
