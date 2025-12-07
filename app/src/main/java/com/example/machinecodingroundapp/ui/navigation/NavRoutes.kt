package com.example.machinecodingroundapp.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Splash : NavRoutes("splash")
    data object Home : NavRoutes("home")
    object VideoPlayer : NavRoutes("video_player/{videoId}") {
        fun passId(videoId: String) = "video_player/$videoId"
    }
}
