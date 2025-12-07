package com.example.machinecodingroundapp.di

import com.example.machinecodingroundapp.data.local.VideoDao
import com.example.machinecodingroundapp.data.remote.VideoApiService
import com.example.machinecodingroundapp.data.repository.VideoRepositoryImpl
import com.example.machinecodingroundapp.domain.repository.VideoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideVideoRepository(
        apiService: VideoApiService,
        videoDao: VideoDao
    ): VideoRepository {
        return VideoRepositoryImpl(apiService, videoDao)
    }
}