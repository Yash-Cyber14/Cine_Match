package com.example.cinematch

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.msusman.compose.cardstack.CardStack
import com.msusman.compose.cardstack.Direction
import com.msusman.compose.cardstack.Duration
import com.msusman.compose.cardstack.SwipeDirection
import com.msusman.compose.cardstack.SwipeMethod
import com.msusman.compose.cardstack.rememberStackState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch




@Composable
fun StackCardScreen(
    id: Int?,
    moviestyperesponse: Tmdbapiresponse?,
    navController: NavHostController,
    viewModel: AuthenticationViewModel,
    viewModel1: MainViewModel
) {
    var allMovies = moviestyperesponse?.results ?: emptyList()

    val startIndex = allMovies.indexOfFirst { it.id == id }
    if (startIndex in 0 until allMovies.size) {
        val head = allMovies.drop(startIndex)
        val tail = allMovies.take(startIndex)
        allMovies = if (head.isNotEmpty()) head + tail else allMovies
    }


    val movies = remember(allMovies) { allMovies.takeIf { it.size > 0 } ?: emptyList() }


    // Control whether the CardStack is rendered
    var showStack by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 🖼️ Background
        Image(
            painter = painterResource(R.drawable.get_started),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🎞️ Movie Stack Layer (Middle)
        if (movies.isNotEmpty() && showStack) {
            val stackState = rememberStackState()

            CardStack(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                stackState = stackState,
                cardElevation = 10.dp,
                scaleRatio = 0.95f,
                rotationMaxDegree = 12,
                displacementThreshold = 60.dp,
                animationDuration = Duration.NORMAL,
                visibleCount = 1,
                stackDirection = Direction.BottomAndRight,
                swipeDirection = SwipeDirection.FREEDOM,
                swipeMethod = SwipeMethod.AUTOMATIC_AND_MANUAL,
                items = movies,
                // ✅ helps preserve stability
            ) { movie ->
                // Defensive guard
                if (movies.contains(movie)) {
                    MovieCard(
                        movie = movie,
                        viewModel1 = viewModel1,
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            }
        } else if (!showStack) {
            // ...
        } else {
            Text(
                text = "Not enough movies to display",
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }


        // ⬅️ Back Button (Top Layer)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .size(42.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        showStack = false
                        coroutineScope.launch {
                            delay(100)
                            navController.popBackStack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}




@Composable
fun MovieCard(movie: Movie , viewModel: AuthenticationViewModel , viewModel1: MainViewModel , navController: NavHostController ) {

    val scope = rememberCoroutineScope()



    Box(Modifier.fillMaxSize()
        .clickable {
            navController.navigate("${Screens.VideoPlayer.route}/${movie.id}")
        }) {


        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = ColorPainter(Color.DarkGray)
        )



        Box(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 100f,
                        endY = 1000f
                    ),
                    alpha = 0.9f
                )
        )
        {


            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFFAF0E6),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDCDCDC),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
            val favouriteMovies = viewModel.favouriteMovies.value
            var liked by remember { mutableStateOf(favouriteMovies.any { it.id == movie.id }) }
            Box(Modifier.fillMaxSize()) {
                // Movie Poster etc.

                IconButton(
                    onClick = {
                        liked = !liked
                        viewModel.toggleFavorite(movie)
                        scope.launch {
                            viewModel.loadFavourites()
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (liked) Color.Red else Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun ExoPlayerView(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    )
}

@Composable
fun YouTubePlayerView(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val videoId = remember(videoUrl) {
        Uri.parse(videoUrl).getQueryParameter("v")
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView(ctx).apply {
                lifecycleOwner.lifecycle.addObserver(this)

                addYouTubePlayerListener(object :
                    com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                        videoId?.let {
                            youTubePlayer.loadVideo(it, 0f)
                        }
                    }
                })
            }
        }
    )
}

@Composable
fun MovieVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    when {
        videoUrl.contains("youtube.com", ignoreCase = true) ||
                videoUrl.contains("youtu.be", ignoreCase = true) -> {
            YouTubePlayerView(videoUrl, modifier)
        }
        else -> {
            ExoPlayerView(videoUrl, modifier)
        }
    }
}




