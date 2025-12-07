package com.example.machinecodingroundapp.ui.video_player_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.machinecodingroundapp.domain.model.Video
import com.example.machinecodingroundapp.domain.usecase.GetVideoByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val getVideoByIdUseCase: GetVideoByIdUseCase,
    savedStateHandle: SavedStateHandle,
    val player: Player
) : ViewModel() {

    private val videoId: String = checkNotNull(savedStateHandle["videoId"])

    private val _uiState = MutableStateFlow<VideoPlayerUiState>(VideoPlayerUiState.Loading)
    val uiState: StateFlow<VideoPlayerUiState> = _uiState

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            onEvent(VideoPlayerAction.PlayerStateChanged(state))
        }
    }

    init {
        setupPlayer()
        loadVideo()
    }

    private fun loadVideo() {
        viewModelScope.launch {
            val video = getVideoByIdUseCase(videoId)
            if (video != null) {
                loadVideoInExoPlayer(video)
                _uiState.value = VideoPlayerUiState.Loaded(
                    video = video,
                    isBuffering = true
                )
            } else {
                _uiState.value = VideoPlayerUiState.Error("Video not found")
            }
        }
    }

    private fun setupPlayer() {
        player.addListener(listener)
        player.stop()
        player.clearMediaItems()
    }

    private fun loadVideoInExoPlayer(video: Video) {
        player.setMediaItem(MediaItem.fromUri(video.videoUrl))
        player.prepare()
        player.playWhenReady = true
    }

    fun onEvent(action: VideoPlayerAction) {
        when (action) {
            is VideoPlayerAction.PlayerStateChanged -> {
                val isBuffering = action.state == Player.STATE_BUFFERING
                val current = _uiState.value
                if (current is VideoPlayerUiState.Loaded) {
                    _uiState.value = current.copy(isBuffering = isBuffering)
                }
            }

            VideoPlayerAction.ToggleFullscreen -> {
                val current = _uiState.value
                if (current is VideoPlayerUiState.Loaded) {
                    _uiState.value = current.copy(isFullScreen = !current.isFullScreen)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.stop()
        player.removeListener(listener)
    }
}

sealed class VideoPlayerUiState {
    object Loading : VideoPlayerUiState()
    data class Loaded(
        val video: Video,
        val isBuffering: Boolean = false,
        val isFullScreen:Boolean = false
    ) : VideoPlayerUiState()
    data class Error(val message: String) : VideoPlayerUiState()
}

sealed class VideoPlayerAction {
    data class PlayerStateChanged(val state: Int) : VideoPlayerAction()
    object ToggleFullscreen : VideoPlayerAction()
}


