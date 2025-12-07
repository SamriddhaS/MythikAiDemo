package com.example.machinecodingroundapp.ui.home_screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.machinecodingroundapp.domain.model.Video
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.pager.HorizontalPager // And this one

@Composable
fun HomeScreen(
    viewModel: VideoViewModel,
    onNavigateToPlayerScreen:(String)->Unit
) {

    val uiState by viewModel.screenState.collectAsState()

    LaunchedEffect(Unit) {
        /*
        * We need to handel the navigation related events in the Screen and not in view model.
        * */
        viewModel.uiEvent.collect { event ->
            when (event) {
                is VideoUiEvent.OnVideoClicked -> {
                    val clickedVideo = event.video
                    onNavigateToPlayerScreen(clickedVideo.id)
                }
                else -> {}
            }
        }
    }

    when(uiState){
        is VideoScreenState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Loading…", fontSize = 22.sp)
            }
        }
        is VideoScreenState.NoInternet -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "No Internet Connection", fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadVideos() }) {
                        Text(text = "Retry")
                    }
                }
            }
        }
        is VideoScreenState.Error -> {
            val state = uiState as VideoScreenState.Error
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Oops! Something went wrong", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.message, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadVideos() }) {
                        Text(text = "Retry")
                    }
                }
            }
        }
        is VideoScreenState.Loaded -> {
            val state = uiState as VideoScreenState.Loaded
            VideoListContent(
                loadedState = state,
                onVideoClicked = { video ->
                    viewModel.onEvent(VideoUiEvent.OnVideoClicked(video))
                }
            )
        }

    }
}

@Composable
fun VideoListContent(
    loadedState: VideoScreenState.Loaded,
    onVideoClicked: (Video) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            item {
                CarouselSection(loadedState.carouselVideos, onVideoClicked = onVideoClicked)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(loadedState.videos, key = {it.id}) { video ->
                VideoRowItem(video, onVideoClicked = onVideoClicked)
                Spacer(modifier = Modifier.height(12.dp))
            }

        }

        if (loadedState.isOffline){
            Text(
                text = "You are offline",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun CarouselSection(
    videos: List<Video>,
    onVideoClicked: (Video) -> Unit
) {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { videos.size })

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) { page ->

            val video = videos[page]

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onVideoClicked(video) }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(video.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(0.5f)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                            )
                        )
                        .align(Alignment.BottomEnd)
                    ,
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dot indicator
        LazyRow(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(count = pagerState.pageCount, key = { it }) { index ->

                val isSelected = pagerState.currentPage == index

                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    label = "IndicatorWidth"
                )

                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

@Composable
fun VideoRowItem(
    video: Video,
    onVideoClicked: (Video) -> Unit
) {
    Row(

        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onVideoClicked(video) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(140.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )


            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = video.duration,
                    color = Color.White,
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.labelSmall // Use theme typography
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))


        Column(

            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = video.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}


