package com.example.machinecodingroundapp.data.repository

import com.example.machinecodingroundapp.data.local.VideoDao
import com.example.machinecodingroundapp.data.mappers.toDomain
import com.example.machinecodingroundapp.data.mappers.toEntity
import com.example.machinecodingroundapp.data.remote.VideoApiService
import com.example.machinecodingroundapp.domain.model.Video
import com.example.machinecodingroundapp.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val apiService: VideoApiService,
    private val videoDao: VideoDao
) : VideoRepository {

    override fun getVideos(): Flow<List<Video>> {
        return videoDao.getAllVideos()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun refreshVideos() {
        try {
            val apiResponse = apiService.getVideos()

            // Store them in DB
            val entities = apiResponse.map { it.toEntity() }
            videoDao.insertVideos(entities)

        } catch (e: Exception) {
            // Log, handle API failure, fallback to DB
            throw e
        }
    }

    override suspend fun getVideoById(videoId: String): Video? {
        return videoDao.getVideoById(videoId)?.toDomain()
    }

}
