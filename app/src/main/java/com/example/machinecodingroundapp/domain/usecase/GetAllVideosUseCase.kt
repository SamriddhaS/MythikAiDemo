package com.example.machinecodingroundapp.domain.usecase

import com.example.machinecodingroundapp.domain.model.Video
import com.example.machinecodingroundapp.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllVideosUseCase @Inject constructor(
    private val repository: VideoRepository
) {

    operator fun invoke(): Flow<List<Video>> {
        // Returns all videos from repository as Flow
        return repository.getVideos()
    }
}
