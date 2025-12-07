package com.example.machinecodingroundapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.machinecodingroundapp.ui.splash_screen.SplashScreen
import com.example.machinecodingroundapp.ui.home_screen.HomeScreen
import com.example.machinecodingroundapp.ui.home_screen.VideoViewModel
import com.example.machinecodingroundapp.ui.video_player_screen.VideoPlayerScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
        modifier = modifier
    ) {

        composable(NavRoutes.Splash.route) {
            SplashScreen(
                onNavigateHome = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Home.route) {
            val viewModel: VideoViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToPlayerScreen = {
                    videoId ->
                    navController.navigate("video_player/$videoId")
                }
            )
        }

        composable(
            route = NavRoutes.VideoPlayer.route,
            arguments = listOf(
                navArgument("videoId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
            VideoPlayerScreen()
        }

    }
}
