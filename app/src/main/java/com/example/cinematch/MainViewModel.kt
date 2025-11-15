package com.example.cinematch


import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    // Bottom navigation state
    var selectedHome by mutableStateOf(true)
    var selectedSearch by mutableStateOf(false)

    var GenerativeChat by mutableStateOf(false)
    val tintHome get() = if (selectedHome) Color(0xFF008B8B) else Color.White
    val tintSearch get() = if (selectedSearch) Color(0xFF008B8B) else Color.White

    val tintChat get() = if (GenerativeChat) Color(0xFF008B8B) else Color.White

    // --- Movie data states ---
    private val _popularMovies = MutableStateFlow<UiState<Tmdbapiresponse>>(UiState.Loading)
    val popularMovies: StateFlow<UiState<Tmdbapiresponse>> = _popularMovies

    private val _trendingMovies = MutableStateFlow<UiState<Tmdbapiresponse>>(UiState.Loading)
    val trendingMovies: StateFlow<UiState<Tmdbapiresponse>> = _trendingMovies

    private val _topRatedMovies = MutableStateFlow<UiState<Tmdbapiresponse>>(UiState.Loading)
    val topRatedMovies: StateFlow<UiState<Tmdbapiresponse>> = _topRatedMovies

    private val _searchMovies = MutableStateFlow<UiState<Tmdbapiresponse>>(UiState.Loading)
    val searchMovies: StateFlow<UiState<Tmdbapiresponse>> = _searchMovies

    private val _recommendations = MutableStateFlow<UiState<Tmdbapiresponse>>(UiState.Loading)
    val recommendations: StateFlow<UiState<Tmdbapiresponse>> = _recommendations

    private val _similarMovies = MutableStateFlow<UiState<Tmdbapiresponse>>(UiState.Loading)
    val similarMovies: StateFlow<UiState<Tmdbapiresponse>> = _similarMovies


    // Generic safe API call helper
    private fun <T> safeApiCall(
        flow: MutableStateFlow<UiState<T>>,
        call: suspend () -> T
    ) {
        viewModelScope.launch {
            flow.value = UiState.Loading
            try {
                val result = call()
                flow.value = UiState.Success(result)
            } catch (e: Exception) {
                flow.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    // Fetchers
    fun getpopularmovies() = safeApiCall(_popularMovies) { repository.getpopularmovies() }
    fun gettrendingmovies() = safeApiCall(_trendingMovies) { repository.gettrendingmovies() }
    fun gettopratedmovies() = safeApiCall(_topRatedMovies) { repository.gettopratedmovies() }
    fun searchmovies(query: String) = safeApiCall(_searchMovies) { repository.searchmovies(query) }
    fun getrecommendedmovies(id: Int = 507244) = safeApiCall(_recommendations) { repository.getMovieRecommendation(id) }
    fun getsimilarmovies(id: Int = 1197137) = safeApiCall(_similarMovies) { repository.getSimilarMovies(id) }

    // Movie selection
    var selectedMoviesResponse by mutableStateOf<Tmdbapiresponse?>(null)
        private set
    fun selectMoviesResponse(response: Tmdbapiresponse?) {
        selectedMoviesResponse = response
    }

    private val _videoUrls = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val videoUrls: StateFlow<UiState<List<String>>> = _videoUrls

    fun loadMovieVideos(movieId: Int) {
        viewModelScope.launch {
            _videoUrls.value = UiState.Loading
            try {
                val trailers = repository.getMovieVideos(movieId)

                val urls = trailers.mapNotNull { trailer ->
                    when (trailer.site.lowercase()) {
                        "youtube" -> "https://www.youtube.com/watch?v=${trailer.key}"
                        "vimeo" -> "https://vimeo.com/${trailer.key}"
                        else -> trailer.key // assume direct mp4 or external link
                    }
                }

                _videoUrls.value = UiState.Success(urls)
            } catch (e: Exception) {
                _videoUrls.value = UiState.Error(e.message ?: "Failed to load videos")
            }
        }
    }

}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

