package com.example.machinecodingroundapp.domain.usecase

import com.example.machinecodingroundapp.domain.model.Video
import com.example.machinecodingroundapp.domain.repository.VideoRepository


class GetVideoByIdUseCase(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(videoId: String): Video? {
        return repository.getVideoById(videoId)
    }
}
