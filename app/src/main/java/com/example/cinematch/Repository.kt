package com.example.cinematch

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class Repository(private val apiService: Apiserviceinterface) {

    suspend fun getpopularmovies(): Tmdbapiresponse = safeApiCall {
        apiService.getpopularmovies()
    }

    suspend fun gettrendingmovies(): Tmdbapiresponse = safeApiCall {
        apiService.gettrendingmovies()
    }

    suspend fun gettopratedmovies(): Tmdbapiresponse = safeApiCall {
        apiService.getTopRatedMovies()
    }

    suspend fun getnowplaying(): Tmdbapiresponse = safeApiCall {
        apiService.getNowPlayingMovies()
    }

    suspend fun searchmovies(query: String, page: Int = 1): Tmdbapiresponse = safeApiCall {
        apiService.searchMovies(query, page = page)
    }

    suspend fun getMovieVideos(movieId: Int): List<Trailer> = safeApiCall {

        val response = apiService.getMovieVideos(movieId)
        response.results
    }

    suspend fun getMovieRecommendation(movieId: Int): Tmdbapiresponse = safeApiCall {
        apiService.getMovieRecommendations(movieId)
    }

    suspend fun getSimilarMovies(movieId: Int): Tmdbapiresponse = safeApiCall {
        apiService.getSimilarMovies(movieId)
    }

    suspend fun getGenres(): GenreResponse = safeApiCall {
        apiService.getGenres()
    }

    // generic helper for safe calls
    private suspend fun <T> safeApiCall(block: suspend () -> T): T = withContext(Dispatchers.IO) {
        try {
            block()
        } catch (e: Exception) {
            throw when (e) {
                is IOException -> NetworkException("No Internet Connection", e)
                else -> UnknownException("Unknown Error", e)
            }
        }
    }
}


class NetworkException(message :String , cause : Throwable?) : Exception(message , cause)
class ApiException(message :String , cause : Throwable?) : Exception(message , cause)
class UnknownException(message :String , cause : Throwable?) : Exception(message , cause)