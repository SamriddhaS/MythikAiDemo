package com.example.machinecodingroundapp.data.remote

import com.google.gson.annotations.SerializedName

data class VideoDto(

    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String,

    @SerializedName("duration")
    val duration: String,

    @SerializedName("uploadTime")
    val uploadTime: String,

    @SerializedName("views")
    val views: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("videoUrl")
    val videoUrl: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("subscriber")
    val subscriber: String,

    @SerializedName("isLive")
    val isLive: Boolean
)
