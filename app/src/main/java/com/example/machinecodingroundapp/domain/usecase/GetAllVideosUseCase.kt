package com.example.machinecodingroundapp.domain.usecase

import com.example.machinecodingroundapp.domain.model.Video
import com.example.machinecodingroundapp.domain.repository.VideoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class GetAllVideosUseCase @Inject constructor(
    private val repository: VideoRepository
) {

    operator fun invoke(): Flow<List<Video>> {
        // Returns all videos from repository as Flow
        return repository.getVideos()
    }

    suspend fun searchVideos(query:String):List<Video>{
        delay(1000)
        val videos = repository.getVideos().first()
        if(query.isEmpty()) return videos
        return videos.filter {
            it.title.contains(query, ignoreCase = true)
                    || it.description.contains(query, ignoreCase = true)
        }
    }

    suspend fun getSortedVideos(isSorted:Boolean):List<Video>{
        val videos = repository.getVideos().first()
        return if (isSorted) videos.sortedBy {
            it.title
        } else videos
    }
}
