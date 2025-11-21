package com.example.cinematch

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.orEmpty



@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    authviewModel: AuthenticationViewModel
) {
    val popularState by viewModel.popularMovies.collectAsState()
    val trendingState by viewModel.trendingMovies.collectAsState()
    val topRatedState by viewModel.topRatedMovies.collectAsState()
    val recommendedState by viewModel.recommendations.collectAsState()
    val similarState by viewModel.similarMovies.collectAsState()

    val context = LocalContext.current

    // 🔹 Load all data once when screen opens
    LaunchedEffect(Unit) {
        viewModel.getpopularmovies()
        viewModel.gettopratedmovies()
        viewModel.getrecommendedmovies()
        viewModel.getsimilarmovies()
        authviewModel.loadFavourites()
        authviewModel.loadCommonMoviesWithFriends()
    }

    val state = popularState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 🖼 Background image
        Image(
            painter = painterResource(R.drawable.get_started),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🖤 Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC000000))
        )

        // 📦 Main layout column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()      // ✅ Avoids cutting top logo
                .navigationBarsPadding()  // ✅ Keeps bottom bar visible
        ) {
            // 🔝 Header section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.cm_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            navController.navigate(Screens.ProfileScreen.route)
                        }
                )
            }

            // 📰 Main scrollable movie sections
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                // ⭐ Popular Movies
                item {
                    when (state) {
                        is UiState.Loading -> Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }

                        is UiState.Error -> Text(
                            text = state.message,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )

                        is UiState.Success -> {
                            val movies =
                                (state as UiState.Success<Tmdbapiresponse>).data.results
                            if (movies.isNotEmpty()) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(movies) { movie ->
                                        Box(
                                            modifier = Modifier
                                                .width(300.dp)
                                                .height(200.dp)
                                        ) {
                                            popularmoviescards(movie, viewModel, navController)
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    "No movies to display",
                                    color = Color.White,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }

                // 🔸 Other movie categories
                val sections = listOf(
                    "Top Rated" to topRatedState,
                    "Similar Movies" to similarState,
                    "Recommendations" to recommendedState
                )

                items(sections) { (title, state) ->
                    when (state) {
                        is UiState.Loading -> SectionLoading(title)
                        is UiState.Error -> SectionError(title, state.message)
                        is UiState.Success -> {
                            MovieCardWithTitle(
                                lazyitemsresponse(title, state.data),
                                viewModel,
                                navController
                            )
                        }
                    }
                }

                // ❤️ Favourites + Friends
                val favouritesandfriends = listOf(
                    favourites("Favourites", authviewModel.favouriteMovies.value),
                    favourites("Watch With Friends", authviewModel.commonMovies.value)
                )

                items(favouritesandfriends) { movie ->
                    favouritesandfriendsmovies(
                        movie.title,
                        movie.movie,
                        navController,
                        authviewModel ,
                        context
                    )
                }

                // Add bottom padding so last card isn’t hidden
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // 🟩 Bottom navigation bar (always visible)
            bottombar(navController, viewModel)
        }
    }
}


@Composable
fun popularmoviescards(
    movie: Movie,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val popularmoviesState by viewModel.popularMovies.collectAsState()
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .fillMaxWidth() // ✅ Take full screen width
            .aspectRatio(16 / 9f) // ✅ Maintain good cinematic aspect ratio
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                scope.launch {
                    // 🟡 Smooth click animation
                    scale.animateTo(0.95f, animationSpec = tween(80))
                    scale.animateTo(1f, animationSpec = tween(120))

                    // ✅ Navigate safely after animation
                    val movieResponse = when (popularmoviesState) {
                        is UiState.Success -> (popularmoviesState as UiState.Success).data
                        else -> null
                    }

                    movieResponse?.let {
                        viewModel.selectMoviesResponse(it)
                        navController.navigate("${Screens.StackCard.route}/${movie.id}")
                    }
                }
            }
    ) {
        // 🎬 Movie Poster (full width)
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            error = ColorPainter(Color.DarkGray)
        )

        // 🎨 Gradient overlay with movie title
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 400f
                    )
                )
        ) {
            Text(
                text = movie.title ?: "Unknown Title",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}



@Composable
private fun SectionLoading(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, color = Color.White, fontSize = 18.sp)
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
private fun SectionError(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = title, color = Color.White, fontSize = 18.sp)
        Text(text = message, color = Color.Red)
    }
}


@Composable
fun MovieCardWithTitle(
    movieItem: lazyitemsresponse,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp)
    ) {
        // Title
        Text(
            text = movieItem.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )

        val movies = movieItem.movieresponse?.results.orEmpty()

        when {
            movies.isEmpty() -> {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No movies available",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }

            else -> {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(movies) { movie ->
                        MovieCard(
                            movie = movie,
                            movieresponse = movieItem.movieresponse,
                            viewModel = viewModel,
                            navcontroller = navController
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MovieCard(
    movie: Movie,
    movieresponse: Tmdbapiresponse?,
    navcontroller: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                scope.launch {
                    // Smooth click animation
                    scale.animateTo(0.95f, animationSpec = tween(80))
                    scale.animateTo(1f, animationSpec = tween(120))

                    // ✅ Save selected movie list to ViewModel
                    viewModel.selectMoviesResponse(movieresponse)

                    delay(100)

                    // ✅ Navigate safely
                    navcontroller.navigate("${Screens.StackCard.route}/${movie.id}")
                }
            }
    ) {
        // Movie poster
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = ColorPainter(Color.DarkGray)
        )

        // Title overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 100f,
                        endY = 500f
                    ),
                    alpha = 0.8f
                )
        )
    }
}





@Composable
fun PopularMoviesCards(
    movie: Movie,
    viewModel: MainViewModel,
    navcontroller: NavHostController
) {
    val popularmoviesState by viewModel.popularMovies.collectAsState()
    val scope = rememberCoroutineScope()  // ✅ to safely launch animations
    val scale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                scope.launch {
                    // 🟡 Smooth click animation
                    scale.animateTo(0.95f, animationSpec = tween(80))
                    scale.animateTo(1f, animationSpec = tween(120))

                    // ✅ Navigate safely after animation
                    val movieResponse = when (popularmoviesState) {
                        is UiState.Success -> (popularmoviesState as UiState.Success).data
                        else -> null
                    }

                    movieResponse?.let {
                        viewModel.selectMoviesResponse(it)
                        navcontroller.navigate("${Screens.StackCard.route}/${movie.id}")
                    }
                }
            }
    ) {
        // 🎬 Movie Poster
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = ColorPainter(Color.DarkGray)
        )

        // 🎨 Gradient + Title overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 100f,
                        endY = 500f
                    ),
                    alpha = 0.8f
                )
        ) {
            Text(
                text = movie.title ?: "Unknown Title",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFFAF0E6),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun bottombar(navcontroller: NavHostController , viewModel1: MainViewModel) {



    BottomAppBar(
        Modifier.fillMaxWidth().height(56.dp),
        containerColor = Color.Black,
        tonalElevation = 8.dp,
        contentPadding = PaddingValues(8.dp)
    ) {
        Row(Modifier.fillMaxSize().padding(4.dp) , horizontalArrangement = Arrangement.SpaceEvenly , verticalAlignment = Alignment.CenterVertically ) {
            IconButton(
                onClick = {
                    viewModel1.selectedHome = true
                    viewModel1.selectedSearch =false
                    viewModel1.GenerativeChat = false
                    navcontroller.navigate(Screens.Home.route)


                }
            ) {
                Icon(Icons.Default.Home , contentDescription = "Home" , tint = viewModel1.tintHome)
            }

            IconButton(
                onClick = {
                    viewModel1.selectedHome = false
                    viewModel1.selectedSearch =true
                    viewModel1.GenerativeChat = false
                    navcontroller.navigate(Screens.SearchScreen.route)


                }
            ) {
                Icon(Icons.Default.Search , contentDescription = "Search Movies" , tint = viewModel1.tintSearch)
            }

            IconButton(
                onClick = {
                    viewModel1.selectedHome = false
                    viewModel1.selectedSearch =false
                    viewModel1.GenerativeChat = true
                    navcontroller.navigate(Screens.GenerativeAi.route)


                }
            ) {
                Icon(Icons.Default.ChatBubbleOutline , contentDescription = "Search Movies" , tint = viewModel1.tintChat)
            }

        }
    }

}

@Composable
fun favouritesandfriendsmovies(title: String, movie: List<Movie> , navController: NavHostController , viewModel : AuthenticationViewModel , context: Context) {

    LaunchedEffect(Unit) {
        viewModel.loadFavourites()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp)
    ) {
        // Title
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )



        when {
            movie.isEmpty() -> {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No movies available",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }

            else -> {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(movie) { movie ->
                        FavMovieCard(
                            movie, navController = navController , viewModel , context = context
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun FavMovieCard(
    movie: Movie,
    navController: NavHostController,
    authViewModel: AuthenticationViewModel ,
    context : Context
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    // ✅ Check if movie is already in favourites
    val favouriteMovies by authViewModel.favouriteMovies
    val isFav = favouriteMovies.any { it.id == movie.id }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                scope.launch {
                    // Smooth click animation
                    scale.animateTo(0.95f, animationSpec = tween(80))
                    scale.animateTo(1f, animationSpec = tween(120))

                    // ✅ Navigate to stack card with movie details
                    navController.navigate("${Screens.StackCard.route}/${movie.id}")
                }
            }
    ) {
        // 🎬 Movie Poster
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = ColorPainter(Color.DarkGray)
        )

        // 🎨 Gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 100f,
                        endY = 500f
                    ),
                    alpha = 0.8f
                )
        )


        IconButton(
            onClick = {
                scope.launch {
                    authViewModel.toggleFavouriteMovie(movie ,context)
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(36.dp)
                .background(Color(0xAA000000), shape = CircleShape)
        ) {
            Icon(
                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFav) Color.Red else Color.White
            )
        }

        // 🎬 Movie title at bottom

    }
}




data class lazyitemsresponse(val title: String, val movieresponse: Tmdbapiresponse?)
data class favourites(val title: String , val movie: List<Movie>)


