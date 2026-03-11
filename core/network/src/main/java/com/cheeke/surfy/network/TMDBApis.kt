package com.cheeke.surfy.network

import com.cheeke.surfy.network.model.NetworkTMDBCertificationData
import com.cheeke.surfy.network.model.NetworkTMDBCombineCredits
import com.cheeke.surfy.network.model.NetworkTMDBConfiguration
import com.cheeke.surfy.network.model.NetworkTMDBExternalIds
import com.cheeke.surfy.network.model.NetworkTMDBLanguageItem
import com.cheeke.surfy.network.model.NetworkTMDBMovie
import com.cheeke.surfy.network.model.NetworkTMDBMovieDetailSimilar
import com.cheeke.surfy.network.model.NetworkTMDBMovieGenres
import com.cheeke.surfy.network.model.NetworkTMDBMovieList
import com.cheeke.surfy.network.model.NetworkTMDBMovieReviews
import com.cheeke.surfy.network.model.NetworkTMDBMovieSeries
import com.cheeke.surfy.network.model.NetworkTMDBMovieWatchProvider
import com.cheeke.surfy.network.model.NetworkTMDBPeopleDetail
import com.cheeke.surfy.network.model.NetworkTMDBRegion
import com.cheeke.surfy.network.model.NetworkTMDBSearchKeywordData
import com.cheeke.surfy.network.model.NetworkTMDBSearchMovie
import com.cheeke.surfy.network.model.NetworkTMDBSearchPeople
import com.cheeke.surfy.network.model.NetworkTMDBSearchSeries
import com.cheeke.surfy.network.model.NetworkTMDBSimilarTv
import com.cheeke.surfy.network.model.NetworkTMDBTrendingMovie
import com.cheeke.surfy.network.model.NetworkTMDBTrendingPeople
import com.cheeke.surfy.network.model.NetworkTMDBTrendingTv
import com.cheeke.surfy.network.model.NetworkTMDBTv
import com.cheeke.surfy.network.model.NetworkTMDBTvEpisode
import com.cheeke.surfy.network.model.NetworkTMDBTvReviews
import com.cheeke.surfy.network.model.NetworkTMDBTvSeasons
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApis {
    @GET("/3/configuration")
    suspend fun getConfiguration(): ApiResponse<NetworkTMDBConfiguration>

    @GET("/3/certification/movie/list")
    suspend fun getCertification(): ApiResponse<NetworkTMDBCertificationData>

    @GET("/3/genre/movie/list")
    suspend fun getMovieGenres(
        @Query("language") language: String = "ko-KR"
    ): ApiResponse<NetworkTMDBMovieGenres>

    @GET("/3/genre/tv/list")
    suspend fun getTvGenres(
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

    @GET("/3/search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = true,
        @Query("language") language: String = "ko-KR",
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBSearchMovie>

    @GET("/3/search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = true,
        @Query("language") language: String = "ko-KR",
        @Query("region") region: String = "KR",
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBSearchMovie>

    @GET("/3/search/tv")
    suspend fun searchTv(
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
    suspend fun getMovie(
        @Path("movie_id") id: Int,
        @Query("append_to_response") appendToResponse: String = "images,videos,credits,releases,alternative_titles",
        @Query("language") language: String = "ko-KR",
        @Query("include_image_language") includeImageLanguage: String = "ko",
        @Query("region") region: String = "KR"
    ): ApiResponse<NetworkTMDBMovie>

    @GET("/3/movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") id: Int,
        @Query("language") language: String = "ko-KR",
        @Query("page") page: Int = 1,
    ): ApiResponse<NetworkTMDBMovieDetailSimilar>

    @GET("/3/tv/{series_id}/similar")
    suspend fun getSimilarTv(
        @Path("series_id") id: Int,
        @Query("language") language: String = "ko-KR",
        @Query("page") page: Int = 1,
    ): ApiResponse<NetworkTMDBSimilarTv>

    @GET("/3/tv/{series_id}")
    suspend fun getTv(
        @Path("series_id") id: Int,
        @Query("append_to_response") appendToResponse: String = "images,videos,credits,releases,alternative_titles",
        @Query("language") language: String = "ko-KR",
        @Query("include_image_language") includeImageLanguage: String = "ko"
    ): ApiResponse<NetworkTMDBTv>

    @GET("/3/tv/{series_id}/season/{season_number}")
    suspend fun getTvSeasons(
        @Path("series_id") seriesId: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("append_to_response") appendToResponse: String = "images,videos,credits,releases,alternative_titles",
        @Query("language") language: String = "ko-KR"
    ): ApiResponse<NetworkTMDBTvSeasons>

    @GET("/3/tv/{series_id}/season/{season_number}/episode/{episode_number}")
    suspend fun getTvEpisode(
        @Path("series_id") seriesId: Int,
        @Path("season_number") seasonNumber: Int,
        @Path("episode_number") episodeNumber: Int,
        @Query("append_to_response") appendToResponse: String = "images,videos,credits,releases,alternative_titles",
        @Query("language") language: String = "ko-KR"
    ): ApiResponse<NetworkTMDBTvEpisode>

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

    @GET("/3/search/keyword")
    suspend fun getSearchKeyword(
        @Query("query") query: String,
        @Query("page") page: Int
    ): ApiResponse<NetworkTMDBSearchKeywordData>

    @GET("/3/movie/{movie_id}/reviews")
    suspend fun getMovieReview(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "ko-KR",
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBMovieReviews>

    @GET("/3/tv/{series_id}/reviews")
    suspend fun getTvReview(
        @Path("series_id") seriesId: Int,
        @Query("language") language: String = "ko-KR",
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBTvReviews>

    @GET("/3/trending/movie/{time_window}")
    suspend fun getTrendingMovie(
        @Path("time_window") timeWindow: String,
        @Query("language") language: String,
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBTrendingMovie>

    @GET("/3/trending/person/{time_window}")
    suspend fun getTrendingPeople(
        @Path("time_window") timeWindow: String,
        @Query("language") language: String,
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBTrendingPeople>

    @GET("/3/trending/tv/{time_window}")
    suspend fun getTrendingTv(
        @Path("time_window") timeWindow: String,
        @Query("language") language: String,
        @Query("page") page: Int = 1
    ): ApiResponse<NetworkTMDBTrendingTv>

    @GET("/3/movie/{movie_id}/watch/providers")
    suspend fun getMovieWatchProvider(
        @Path("movie_id") movieId: Int
    ): ApiResponse<NetworkTMDBMovieWatchProvider>
}