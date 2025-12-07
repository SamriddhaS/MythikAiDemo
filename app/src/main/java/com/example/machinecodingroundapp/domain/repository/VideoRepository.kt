package com.example.machinecodingroundapp.domain.repository

import com.example.machinecodingroundapp.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getVideos(): Flow<List<Video>>
    suspend fun refreshVideos()
    suspend fun getVideoById(videoId: String): Video?
}