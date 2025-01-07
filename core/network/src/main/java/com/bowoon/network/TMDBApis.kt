package com.bowoon.network

import com.bowoon.network.model.NetworkTMDBConfiguration
import com.bowoon.network.model.NetworkTMDBLanguageItem
import com.bowoon.network.model.NetworkTMDBMovieDetail
import com.bowoon.network.model.NetworkTMDBPeopleDetail
import com.bowoon.network.model.NetworkTMDBRegion
import com.bowoon.network.model.NetworkTMDBSearch
import com.bowoon.network.model.NetworkUpcoming
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApis {
    @GET("/3/configuration")
    suspend fun getConfiguration(): ApiResponse<NetworkTMDBConfiguration>

    @GET("/3/movie/upcoming")
    suspend fun getUpcomingMovie(
        @Query("language") language: String = "ko-KR",
        @Query("region") region: String = "KR",
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "release_date.asc"
    ): ApiResponse<NetworkUpcoming>

    @GET("/3/search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String = "ko-KR",
        @Query("region") region: String = "KR",
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBSearch>

    @GET("/3/movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") id: Int,
        @Query("append_to_response") appendToResponse: String = "images,videos,credits,reviews,releases,translations,lists,keywords,alternative_titles,changes,similar",
        @Query("language") language: String = "ko-KR",
        @Query("include_image_language") includeImageLanguage: String = "ko",
        @Query("region") region: String = "KR"
    ): ApiResponse<NetworkTMDBMovieDetail>

    @GET("/3/discover/movie")
    suspend fun discoverMovie(
        @Query("release_date.gte") releaseDateGte: String,
        @Query("release_date.lte") releaseDateLte: String,
        @Query("language") language: String = "ko-KR",
        @Query("region") region: String = "KR"
    ): ApiResponse<NetworkTMDBSearch>

    @GET("/3/configuration/languages")
    suspend fun getAvailableLanguage(): ApiResponse<List<NetworkTMDBLanguageItem>>

    @GET("/3/watch/providers/regions")
    suspend fun getAvailableRegion(): ApiResponse<NetworkTMDBRegion>

    @GET("/3/person/{person_id}")
    suspend fun getPeopleDetail(
        @Path("person_id") personId: Int,
        @Query("append_to_response") appendToResponse: String = "images, combined_credits, external_ids",
        @Query("language") language: String = "ko-KR"
    ): ApiResponse<NetworkTMDBPeopleDetail>
}