package com.example.machinecodingroundapp.data.mappers

import com.example.machinecodingroundapp.data.local.VideoEntity
import com.example.machinecodingroundapp.data.remote.VideoDto
import com.example.machinecodingroundapp.domain.model.Video


fun VideoDto.toEntity(): VideoEntity {
    return VideoEntity(
        id = id,
        title = title,
        thumbnailUrl = thumbnailUrl,
        duration = duration,
        uploadTime = uploadTime,
        views = views,
        author = author,
        videoUrl = videoUrl,
        description = description,
        subscriber = subscriber,
        isLive = isLive
    )
}


fun VideoEntity.toDomain(): Video {
    return Video(
        id = id,
        title = title,
        thumbnailUrl = thumbnailUrl,
        duration = duration,
        uploadTime = uploadTime,
        views = views,
        author = author,
        videoUrl = videoUrl,
        description = description,
        subscriber = subscriber,
        isLive = isLive
    )
}

