package com.example.machinecodingroundapp.ui.home_screen

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machinecodingroundapp.domain.model.Video
import com.example.machinecodingroundapp.domain.repository.VideoRepository
import com.example.machinecodingroundapp.domain.usecase.GetAllVideosUseCase
import com.example.machinecodingroundapp.domain.usecase.GetRandomVideosUseCase
import com.example.machinecodingroundapp.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val getAllVideosUseCase: GetAllVideosUseCase,
    private val getRandomVideosUseCase: GetRandomVideosUseCase,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    // Single source of truth for the UI
    private val _screenState = MutableStateFlow<VideoScreenState>(VideoScreenState.Loading)
    val screenState: StateFlow<VideoScreenState> = _screenState.asStateFlow()

    // Events for one-time actions (clicks, navigation, refresh)
    private val _uiEvent = MutableSharedFlow<VideoUiEvent>()
    val uiEvent: SharedFlow<VideoUiEvent> = _uiEvent.asSharedFlow()

    var searchJob:Job? = null

    init {
        observeDatabaseAndNetwork()
        loadVideos()
    }

    private fun observeDatabaseAndNetwork() {

        viewModelScope.launch {
            getAllVideosUseCase().collect { videos ->
                val currentState = _screenState.value
                val isCurrentlyOnline = networkMonitor.isOnline.value

                // This logic will run whenever the local database changes.
                if (videos.isNotEmpty()) {
                    // If we have data, always show it.
                    val carouselVideos = getRandomVideosUseCase(videos)
                    _screenState.value = VideoScreenState.Loaded(
                        videos = videos,
                        carouselVideos = carouselVideos,
                        isOffline = !isCurrentlyOnline
                    )
                } else {
                    // No video found
                    if (!isCurrentlyOnline) {
                        // If the database is empty AND we are offline, it's a hard "No Internet" state.
                        _screenState.value = VideoScreenState.NoInternet
                    } else if (currentState !is VideoScreenState.Loading) {
                        // This might happen if the API returns an empty list.
                        _screenState.value = VideoScreenState.Error("No videos found.")
                    }
                }
            }
        }

        viewModelScope.launch {
            networkMonitor.isOnline.collect{ isOnline ->
                when (_screenState.value){
                    is VideoScreenState.Loaded -> {
                        _screenState.value = (_screenState.value as VideoScreenState.Loaded).copy(isOffline = !isOnline)
                    }

                    is VideoScreenState.NoInternet -> {
                        if(isOnline) loadVideos()
                    }

                    else -> {
                        _screenState.value = VideoScreenState.NoInternet
                    }
                }
            }
        }

    }

    fun loadVideos() {

        // If offline, no need to proceed with network calls.
        if (!networkMonitor.isOnline.value) {
            return
        }

        // Don't change state to Loading if we already have data.
        if (_screenState.value !is VideoScreenState.Loaded) {
            _screenState.value = VideoScreenState.Loading
        }

        viewModelScope.launch {
            try {
                // Refresh video from remote db.
                repository.refreshVideos()
            } catch (e: Exception) {
                // If the refresh fails and we have no local data, show an error.
                if (_screenState.value !is VideoScreenState.Loaded) {
                    _screenState.value = VideoScreenState.Error(
                        message = e.localizedMessage ?: "Something went wrong"
                    )
                }
            }
        }
    }

    // Handle UI Events
    fun onEvent(event: VideoUiEvent) {
        viewModelScope.launch {
            when (event) {
                is VideoUiEvent.OnVideoClicked -> {
                    if (networkMonitor.isOnline.value){
                        // If we are online only then try to navigate.
                        _uiEvent.emit(event)
                    }
                }

                is VideoUiEvent.RefreshVideos -> {
                    loadVideos()
                }

                is VideoUiEvent.SearchInputChanged -> {
                    val loaded = screenState.value as VideoScreenState.Loaded
                    _screenState.value = loaded.copy(searchInput = event.input)
                    searchJob?.cancel()// cancel existing job
                    searchJob = launch {
                        delay(500) // add 1sec debouncing effect
                        performSearch(event.input)
                    }
                }

                is VideoUiEvent.ToggleSort -> {
                    handleToggleSort()
                }
            }
        }
    }

    private suspend fun performSearch(searchInput: String){
        val filteredVideos =  getAllVideosUseCase.searchVideos(searchInput)
        _screenState.value = (screenState.value as VideoScreenState.Loaded).copy(videos = filteredVideos)
    }

    private suspend fun handleToggleSort(){
        val loaded = screenState.value as VideoScreenState.Loaded
        val isSorted = !loaded.isSorted // Toggle the current sorted state
        val sortedVideos = getAllVideosUseCase.getSortedVideos(isSorted)
        _screenState.value = loaded.copy(isSorted = isSorted, videos = sortedVideos)
    }
}

sealed class VideoScreenState {
    object Loading : VideoScreenState()
    object NoInternet : VideoScreenState()
    data class Error(val message: String) : VideoScreenState()
    data class Loaded(
        val videos: List<Video>,
        val carouselVideos: List<Video>,
        val isOffline: Boolean = false, //Offline - but showing data form local db
        val searchInput: String = "",
        val isSorted :Boolean = false
    ) : VideoScreenState()
}

sealed class VideoUiEvent {
    data class OnVideoClicked(val video: Video) : VideoUiEvent()
    object RefreshVideos : VideoUiEvent()
    data class SearchInputChanged(val input: String) : VideoUiEvent()
    object ToggleSort:VideoUiEvent()
}