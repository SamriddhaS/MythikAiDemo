# MachineCodingRoundApp

> A demo app demonstrating MVVM + Clean Architecture, offline-first behavior, network monitoring, and an ExoPlayer-based video player. The README highlights architecture, key files, features, and how to run the project.

## Download APK

Download the latest APK here: [Download APK](
https://drive.google.com/drive/folders/180eUrGQwzNPeJkMrIfmqLcbdsjcaDXMR?usp=sharing)

## Preview

![App Preview](demo/demo.gif)

## Main features

- Clean architecture
  - MVVM + Clean Architecture with MVI-style events in the UI layer (ViewModels provide StateFlow + SharedFlow for UI state and one-shot events).
- Offline support (HomeScreen)
  - Local persistence using Room: content remains available when offline.
- Light / Dark mode support in HomeScreen.
- Video Player
  - Show loader while buffering
  - Fullscreen functionality (landscape) implemented without interrupting playback.
- Auto Network Monitoring(HomeScreen)
  - Show Disconnected Status: the app detects connection changes and shows an offline indicator in the UI.
  - If no local data is present, the app will auto-fetch content when network connectivity is restored.
  - Auto reload content if not loaded (ViewModel triggers refresh when network becomes available).


## Architecture & code organization

This project follows clean architecture: data -> domain -> ui, combined with Hilt for DI and Jetpack Compose for UI.

High-level architecture :

- com.example.machinecodingroundapp
  - data
    - local (Room): [VideoEntity](app/src/main/java/com/example/machinecodingroundapp/data/local/VideoEntity.kt), [VideoDao](app/src/main/java/com/example/machinecodingroundapp/data/local/VideoDao.kt), [AppDatabase](app/src/main/java/com/example/machinecodingroundapp/data/local/AppDatabase.kt)
    - remote (Retrofit): [VideoDto](app/src/main/java/com/example/machinecodingroundapp/data/remote/VideoDto.kt), [VideoApiService](app/src/main/java/com/example/machinecodingroundapp/data/remote/VideoApiService.kt)
    - mappers: [VideoMappers](app/src/main/java/com/example/machinecodingroundapp/data/mappers/VideoMappers.kt)
    - repository: [VideoRepositoryImpl](app/src/main/java/com/example/machinecodingroundapp/data/repository/VideoRepositoryImpl.kt)
  - domain
    - model: [Video](app/src/main/java/com/example/machinecodingroundapp/domain/model/Video.kt)
    - usecases: [GetAllVideosUseCase](app/src/main/java/com/example/machinecodingroundapp/domain/usecase/GetAllVideosUseCase.kt), [GetRandomVideosUseCase](app/src/main/java/com/example/machinecodingroundapp/domain/usecase/GetRandomVideosUseCase.kt), [GetVideoByIdUseCase](app/src/main/java/com/example/machinecodingroundapp/domain/usecase/GetVideoByIdUseCase.kt)
  - di (Hilt modules): [NetworkModule](app/src/main/java/com/example/machinecodingroundapp/di/NetworkModule.kt), [DatabaseModule](app/src/main/java/com/example/machinecodingroundapp/di/DatabaseModule.kt), [RepositoryModule](app/src/main/java/com/example/machinecodingroundapp/di/RepositoryModule.kt), [UseCaseModule](app/src/main/java/com/example/machinecodingroundapp/di/UseCaseModule.kt), [PlayerModule](app/src/main/java/com/example/machinecodingroundapp/di/PlayerModule.kt), [AppModule](app/src/main/java/com/example/machinecodingroundapp/di/AppModule.kt)
  - ui
    - navigation: [AppNavHost](app/src/main/java/com/example/machinecodingroundapp/ui/navigation/AppNavHost.kt), [NavRoutes](app/src/main/java/com/example/machinecodingroundapp/ui/navigation/NavRoutes.kt)
    - splash: [SplashScreen](app/src/main/java/com/example/machinecodingroundapp/ui/splash_screen/SplashScreen.kt)
    - home: [HomeScreen](app/src/main/java/com/example/machinecodingroundapp/ui/home_screen/HomeScreen.kt), [HomeViewModel](app/src/main/java/com/example/machinecodingroundapp/ui/home_screen/HomeViewModel.kt)
    - video player: [VideoPlayerScreen](app/src/main/java/com/example/machinecodingroundapp/ui/video_player_screen/VideoPlayerScreen.kt), [VideoPlayerViewModel](app/src/main/java/com/example/machinecodingroundapp/ui/video_player_screen/VideoPlayerViewModel.kt)
  - utils: [NetworkMonitor](app/src/main/java/com/example/machinecodingroundapp/utils/NetworkMonitor.kt)


## Behavior details & design notes

- Offline-first pattern: UI observes a Room Flow as the single source of truth and the `VideoRepositoryImpl.refreshVideos()` writes API responses to the DB. This means the UI reacts only to DB changes and remains consistent across network transitions.
- Network handling: `NetworkMonitor` exposes an `isOnline` StateFlow. `HomeViewModel` uses this to avoid unnecessary network calls when offline and to trigger refresh when connectivity returns.
- Single-player model: `PlayerModule` provides a singleton ExoPlayer instance to maintain a single active playback session.
- Error handling: `HomeViewModel` handel's `Error` and `NoInternet` states; repository exceptions are sent to the ViewModel which decides whether to fallback to local DB.
- Scoping & DI: Hilt modules are separated by concern (network, db, repo, usecases, player).

## Major dependencies

- Kotlin & Android Gradle Plugin (Kotlin 2.x, AGP 8.x)
- Jetpack Compose
- Hilt - for dependency injection
- Room - for local persistence
- Retrofit + Gson converter for network calls
- OkHttp (`okhttp` + `logging-interceptor`)
- ExoPlayer / media3 for media playback
- Coil for image loading in compose