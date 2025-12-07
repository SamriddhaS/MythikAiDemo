package com.example.machinecodingroundapp.di

import com.example.machinecodingroundapp.domain.repository.VideoRepository
import com.example.machinecodingroundapp.domain.usecase.GetAllVideosUseCase
import com.example.machinecodingroundapp.domain.usecase.GetRandomVideosUseCase
import com.example.machinecodingroundapp.domain.usecase.GetVideoByIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetAllVideosUseCase(
        repository: VideoRepository
    ): GetAllVideosUseCase {
        return GetAllVideosUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetRandomVideosUseCase(): GetRandomVideosUseCase {
        return GetRandomVideosUseCase()
    }

    @Provides
    @Singleton
    fun provideGetVideoByIdUseCase(repository: VideoRepository): GetVideoByIdUseCase {
        return GetVideoByIdUseCase(repository)
    }

}