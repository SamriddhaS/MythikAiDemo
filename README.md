# MachineCodingRoundApp

> A compact Android video-browsing app demonstrating MVVM + Clean Architecture, offline-first behavior, network monitoring, and an ExoPlayer-based video player. This repository is prepared for interview review — the README highlights architecture, key files, features, and how to run the project.

## Download APK

Download the latest APK here: [Download APK](
https://drive.google.com/drive/folders/180eUrGQwzNPeJkMrIfmqLcbdsjcaDXMR?usp=sharing)

## Preview

![App Preview](demo/demo.gif)

## Main features

- Auto Network Monitoring
  - Show Disconnected Status: the app detects connection changes and shows an offline indicator in the UI.
  - If no local data is present, the app will auto-fetch content when network connectivity is restored.
  - Auto reload content if not loaded (ViewModel triggers refresh when network becomes available).
- Light / Dark mode support via Material3 theming.
- Offline support
  - Local persistence using Room: content remains available when offline.
- Video Player
  - Buffering state is surfaced to the UI while the player loads content.
  - Fullscreen functionality (landscape) implemented without interrupting playback.
- Clean architecture
  - MVVM + Clean Architecture with MVI-style events in the UI layer (ViewModels provide StateFlow + SharedFlow for UI state and one-shot events).

## Architecture & code organization

This project follows clean architecture: data -> domain -> ui, combined with Hilt for DI and Jetpack Compose for UI.

High-level architecture (click any file to open it on GitHub):

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

For a compact diagram you can also open the ASCII tree in `app/src/main/java/com/example/machinecodingroundapp/notes.txt` — the file contains the same structure in a single glance.

Key files (click to open):

- [MythikApplication.kt](app/src/main/java/com/example/machinecodingroundapp/MythikApplication.kt) — custom Coil ImageLoader + Hilt app annotation.
- [MainActivity.kt](app/src/main/java/com/example/machinecodingroundapp/MainActivity.kt) — Compose host and `AppNavHost` wiring.
- [NetworkModule.kt](app/src/main/java/com/example/machinecodingroundapp/di/NetworkModule.kt) — provides `Retrofit` & `OkHttp`.
- [VideoApiService.kt](app/src/main/java/com/example/machinecodingroundapp/data/remote/VideoApiService.kt) — Retrofit API.
- [VideoDao.kt](app/src/main/java/com/example/machinecodingroundapp/data/local/VideoDao.kt) and [VideoEntity.kt](app/src/main/java/com/example/machinecodingroundapp/data/local/VideoEntity.kt) — Room schema and DAO.
- [VideoRepositoryImpl.kt](app/src/main/java/com/example/machinecodingroundapp/data/repository/VideoRepositoryImpl.kt) — repository that bridges network + local DB.
- [HomeScreen.kt](app/src/main/java/com/example/machinecodingroundapp/ui/home_screen/HomeScreen.kt) and [HomeViewModel.kt](app/src/main/java/com/example/machinecodingroundapp/ui/home_screen/HomeViewModel.kt) — list + carousel UI and state management.
- [VideoPlayerScreen.kt](app/src/main/java/com/example/machinecodingroundapp/ui/video_player_screen/VideoPlayerScreen.kt) and [VideoPlayerViewModel.kt](app/src/main/java/com/example/machinecodingroundapp/ui/video_player_screen/VideoPlayerViewModel.kt) — ExoPlayer integration and fullscreen handling.

## Major dependencies

The project centralizes versions in `gradle/libs.versions.toml`. Major runtime/test dependencies used:

- Kotlin & Android Gradle Plugin (Kotlin 2.x, AGP 8.x via TOML)
- Jetpack Compose (Compose BOM)
- Hilt (`com.google.dagger:hilt-android`, `hilt-compiler`) for dependency injection
- Room (`androidx.room:room-runtime`, `room-ktx`, and KSP `room-compiler`) for local persistence
- Retrofit (`com.squareup.retrofit2:retrofit`) + Gson converter for network
- OkHttp (`okhttp` + `logging-interceptor`)
- ExoPlayer / media3 (`androidx.media3:media3-exoplayer`) for media playback
- Coil (`io.coil-kt:coil-compose`) for image loading
- Paging (Compose integration)
- DataStore (preferences)
- Accompanist (pager & indicators)

See `gradle/libs.versions.toml` and `app/build.gradle.kts` for exact versions and full list of libs.

## Behavior details & design notes (what reviewers care about)

- Offline-first pattern: UI observes a Room Flow as the single source of truth and the `VideoRepositoryImpl.refreshVideos()` writes API responses to the DB. This means the UI reacts only to DB changes and remains consistent across network transitions.
- Network handling: `NetworkMonitor` exposes an `isOnline` StateFlow. `HomeViewModel` uses this to avoid unnecessary network calls when offline and to trigger refresh when connectivity returns.
- Single-player model: `PlayerModule` provides a singleton ExoPlayer instance. This simplifies state management for a single active playback session.
- Error handling: `HomeViewModel` surfaces `Error` and `NoInternet` states; repository exceptions are surfaced to the ViewModel which decides whether to fallback to local DB.
- Scoping & DI: Hilt modules are separated by concern (network, db, repo, usecases, player). 
