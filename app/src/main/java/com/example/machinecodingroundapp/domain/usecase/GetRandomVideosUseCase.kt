package com.example.machinecodingroundapp.domain.usecase

import com.example.machinecodingroundapp.domain.model.Video
import javax.inject.Inject
import kotlin.random.Random

class GetRandomVideosUseCase @Inject constructor() {

    operator fun invoke(videos: List<Video>, count: Int = 4): List<Video> {
        // Pick `count` random videos for carousel
        if (videos.size <= count) return videos
        return videos.shuffled(Random(System.currentTimeMillis())).take(count)
    }
}