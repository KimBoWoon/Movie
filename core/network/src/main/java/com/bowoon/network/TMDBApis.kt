package com.bowoon.network

import com.bowoon.network.model.NetworkTMDBCertificationData
import com.bowoon.network.model.NetworkTMDBCombineCredits
import com.bowoon.network.model.NetworkTMDBConfiguration
import com.bowoon.network.model.NetworkTMDBExternalIds
import com.bowoon.network.model.NetworkTMDBLanguageItem
import com.bowoon.network.model.NetworkTMDBMovieDetail
import com.bowoon.network.model.NetworkTMDBMovieDetailSimilar
import com.bowoon.network.model.NetworkTMDBMovieGenres
import com.bowoon.network.model.NetworkTMDBMovieList
import com.bowoon.network.model.NetworkTMDBMovieSeries
import com.bowoon.network.model.NetworkTMDBPeopleDetail
import com.bowoon.network.model.NetworkTMDBRegion
import com.bowoon.network.model.NetworkTMDBSearchMovie
import com.bowoon.network.model.NetworkTMDBSearchPeople
import com.bowoon.network.model.NetworkTMDBSearchSeries
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApis {
    @GET("/3/configuration")
    suspend fun getConfiguration(): ApiResponse<NetworkTMDBConfiguration>

    @GET("/3/certification/movie/list")
    suspend fun getCertification(): ApiResponse<NetworkTMDBCertificationData>

    @GET("/3/genre/movie/list")
    suspend fun getGenres(
        @Query("language") language: String = "ko-KR"
    ): ApiResponse<NetworkTMDBMovieGenres>

    @GET("/3/movie/now_playing")
    suspend fun getNowPlaying(
        @Query("language") language: String = "ko-KR",
        @Query("region") region: String = "KR",
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBMovieList>

    @GET("/3/movie/upcoming")
    suspend fun getUpcomingMovie(
        @Query("language") language: String = "ko-KR",
        @Query("region") region: String = "KR",
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBMovieList>

    @GET("/3/search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = true,
        @Query("language") language: String = "ko-KR",
        @Query("region") region: String = "KR",
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBSearchMovie>

    @GET("/3/search/person")
    suspend fun searchPeople(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = true,
        @Query("language") language: String = "ko-KR",
        @Query("region") region: String = "KR",
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBSearchPeople>

    @GET("/3/collection/{collection_id}")
    suspend fun getMovieSeries(
        @Path("collection_id") collectionId: Int,
        @Query("language") language: String = "ko-KR",
    ): ApiResponse<NetworkTMDBMovieSeries>

    @GET("/3/search/collection")
    suspend fun searchMovieSeries(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = true,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ko-KR",
        @Query("region") region: String = "KR"
    ): ApiResponse<NetworkTMDBSearchSeries>

    @GET("/3/movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") id: Int,
        @Query("append_to_response") appendToResponse: String = "images,videos,credits,reviews,releases,translations,lists,keywords,alternative_titles,changes,similar",
        @Query("language") language: String = "ko-KR",
        @Query("include_image_language") includeImageLanguage: String = "ko",
        @Query("region") region: String = "KR"
    ): ApiResponse<NetworkTMDBMovieDetail>

    @GET("/3/movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") id: Int,
        @Query("language") language: String = "ko-KR",
        @Query("page") page: Int = 1,
    ): ApiResponse<NetworkTMDBMovieDetailSimilar>

    @GET("/3/discover/movie")
    suspend fun discoverMovie(
        @Query("release_date.gte") releaseDateGte: String,
        @Query("release_date.lte") releaseDateLte: String,
        @Query("include_adult") includeAdult: Boolean = true,
        @Query("language") language: String = "ko-KR",
        @Query("region") region: String = "KR",
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "primary_release_date.asc",
        @Query("with_release_type") withReleaseType: String = "2|3"
    ): ApiResponse<NetworkTMDBSearchMovie>

    @GET("/3/configuration/languages")
    suspend fun getAvailableLanguage(): ApiResponse<List<NetworkTMDBLanguageItem>>

    @GET("/3/watch/providers/regions")
    suspend fun getAvailableRegion(): ApiResponse<NetworkTMDBRegion>

    @GET("/3/person/{person_id}")
    suspend fun getPeopleDetail(
        @Path("person_id") personId: Int,
        @Query("append_to_response") appendToResponse: String = "images, combined_credits, external_ids",
        @Query("language") language: String = "ko-KR",
        @Query("include_image_language") includeImageLanguage: String = "ko"
    ): ApiResponse<NetworkTMDBPeopleDetail>

    @GET("/3/person/{person_id}/combined_credits")
    suspend fun getCombineCredits(
        @Path("person_id") personId: Int,
        @Query("language") language: String = "ko-KR"
    ): ApiResponse<NetworkTMDBCombineCredits>

    @GET("/3/person/{person_id}/external_ids")
    suspend fun getExternalIds(
        @Path("person_id") personId: Int
    ): ApiResponse<NetworkTMDBExternalIds>
}