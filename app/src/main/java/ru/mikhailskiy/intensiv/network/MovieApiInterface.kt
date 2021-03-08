package ru.mikhailskiy.intensiv.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mikhailskiy.intensiv.data.DetailActorResponse
import ru.mikhailskiy.intensiv.data.DetailMovieResponse
import ru.mikhailskiy.intensiv.data.MoviesResponse


interface MovieApiInterface {
    @GET("movie/now_playing")
    fun getNowPlayingMovies(@Query("api_key") apiKey: String, @Query("language") language: String): Single<MoviesResponse>
    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String, @Query("language") language: String): Single<MoviesResponse>
    @GET("movie/upcoming")
    fun getUpComingMovies(@Query("api_key") apiKey: String, @Query("language") language: String): Single<MoviesResponse>
    @GET("tv/popular")
    fun getTvShowMovies(@Query("api_key") apiKey: String, @Query("language") language: String): Single<MoviesResponse>
    @GET("movie/{movie_id}")
    fun getDetailMovie( @Path("movie_id") movie_id: Int, @Query("api_key") apiKey: String, @Query("language") language: String): Single<DetailMovieResponse>
    @GET("tv/{tv_id}")
    fun getDetailTvMovie( @Path("tv_id") tv_id: Int, @Query("api_key") apiKey: String, @Query("language") language: String): Single<DetailMovieResponse>
    @GET("movie/{movie_id}/credits")
    fun getDetailActors( @Path("movie_id") movie_id: Int, @Query("api_key") apiKey: String, @Query("language") language: String): Single<DetailActorResponse>
    @GET("tv/{tv_id}/credits")
    fun getDetailTvActors( @Path("tv_id") tv_id: Int, @Query("api_key") apiKey: String, @Query("language") language: String): Single<DetailActorResponse>
    @GET("search/movie")
    fun getSearchMovies(@Query("api_key") apiKey: String, @Query("language") language: String, @Query("query") query: String): Single<MoviesResponse>

}
