package com.example.cinematch

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Apiserviceinterface {

    @GET("movie/popular")
    suspend fun getpopularmovies(
        @Query("api_key") apiKey: String = Constants.apikey,
        @Query ("page") page : Int = 1
    ) : Tmdbapiresponse

    @GET("trending/movie/week")
    suspend fun gettrendingmovies(
        @Query("api_key") apikey : String = Constants.apikey,
        @Query ("page") page : Int = 1
    ) : Tmdbapiresponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String = Constants.apikey,
        @Query ("page") page : Int = 1
    ): Tmdbapiresponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String = Constants.apikey,

    ): Tmdbapiresponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = Constants.apikey,
        @Query("page") page: Int = 1
    ): Tmdbapiresponse

    // 4. RECOMMENDATIONS & SIMILAR
    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = Constants.apikey
    ): Tmdbapiresponse

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = Constants.apikey
    ): Tmdbapiresponse

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String = Constants.apikey
    ): GenreResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = Constants.apikey
    ) : TrailerResponse



}