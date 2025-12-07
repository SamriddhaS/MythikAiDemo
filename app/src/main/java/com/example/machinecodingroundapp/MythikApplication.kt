package com.example.machinecodingroundapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

@HiltAndroidApp
class MythikApplication:Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val newRequest = chain.request().newBuilder()
                            .header("User-Agent", "Android-Coil-App")
                            .build()
                        chain.proceed(newRequest)
                    }
                    .build()
            )
            .crossfade(true)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .build()
    }
}
