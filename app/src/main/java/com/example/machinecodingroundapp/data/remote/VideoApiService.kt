package com.example.machinecodingroundapp.data.remote

import retrofit2.http.GET

interface VideoApiService {

    @GET("poudyalanil/ca84582cbeb4fc123a13290a586da925/raw/videos.json")
    suspend fun getVideos(): List<VideoDto>
}