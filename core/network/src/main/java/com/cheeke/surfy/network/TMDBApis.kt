package com.cheeke.surfy.network

import com.cheeke.surfy.network.model.NetworkTMDBCertificationData
import com.cheeke.surfy.network.model.NetworkTMDBCombineCredits
import com.cheeke.surfy.network.model.NetworkTMDBConfiguration
import com.cheeke.surfy.network.model.NetworkTMDBExternalIds
import com.cheeke.surfy.network.model.NetworkTMDBImageList
import com.cheeke.surfy.network.model.NetworkTMDBLanguageItem
import com.cheeke.surfy.network.model.NetworkTMDBMovie
import com.cheeke.surfy.network.model.NetworkTMDBMovieGenres
import com.cheeke.surfy.network.model.NetworkTMDBMovieList
import com.cheeke.surfy.network.model.NetworkTMDBMovieReviews
import com.cheeke.surfy.network.model.NetworkTMDBMovieSeries
import com.cheeke.surfy.network.model.NetworkTMDBMovieWatchProvider
import com.cheeke.surfy.network.model.NetworkTMDBPeopleDetail
import com.cheeke.surfy.network.model.NetworkTMDBRegion
import com.cheeke.surfy.network.model.NetworkTMDBSearch
import com.cheeke.surfy.network.model.NetworkTMDBSearchKeywordData
import com.cheeke.surfy.network.model.NetworkTMDBSimilarMedia
import com.cheeke.surfy.network.model.NetworkTMDBTrendingMedia
import com.cheeke.surfy.network.model.NetworkTMDBTv
import com.cheeke.surfy.network.model.NetworkTMDBTvEpisode
import com.cheeke.surfy.network.model.NetworkTMDBTvReviews
import com.cheeke.surfy.network.model.NetworkTMDBTvSeasons
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SettingApis {
    @GET(value = "/3/configuration")
    fun getConfiguration(): Single<ApiResponse<NetworkTMDBConfiguration>>

    @GET(value = "/3/certification/movie/list")
    fun getCertification(): Single<ApiResponse<NetworkTMDBCertificationData>>

    @GET(value = "/3/configuration/languages")
    fun getAvailableLanguage(): Single<ApiResponse<List<NetworkTMDBLanguageItem>>>

    @GET(value = "/3/watch/providers/regions")
    fun getAvailableRegion(): Single<ApiResponse<NetworkTMDBRegion>>

    @GET(value = "/3/genre/movie/list")
    fun getMovieGenres(
        @Query(value = "language") language: String = "ko-KR"
    ): Single<ApiResponse<NetworkTMDBMovieGenres>>

    @GET(value = "/3/genre/tv/list")
    fun getTvGenres(
        @Query(value = "language") language: String = "ko-KR"
    ): Single<ApiResponse<NetworkTMDBMovieGenres>>
}

interface MovieApis {
    @GET(value = "/3/movie/{movie_id}")
    fun getMovie(
        @Path(value = "movie_id") id: Int,
        @Query(value = "append_to_response") appendToResponse: String = "images,videos,credits,releases,alternative_titles",
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "include_image_language") includeImageLanguage: String = "ko",
        @Query(value = "region") region: String = "KR"
    ): Single<ApiResponse<NetworkTMDBMovie>>

    @GET(value = "/3/movie/{movie_id}/similar")
    fun getSimilarMovies(
        @Path(value = "movie_id") id: Int,
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "page") page: Int = 1,
    ): Single<ApiResponse<NetworkTMDBSimilarMedia>>

    @GET(value = "/3/movie/{movie_id}/watch/providers")
    fun getMovieWatchProvider(
        @Path(value = "movie_id") movieId: Int
    ): Single<ApiResponse<NetworkTMDBMovieWatchProvider>>

    @GET(value = "/3/movie/{movie_id}/reviews")
    fun getMovieReview(
        @Path(value = "movie_id") movieId: Int,
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "page") page: Int = 1
    ): Single<ApiResponse<NetworkTMDBMovieReviews>>
}

interface PeopleApis {
    @GET(value = "/3/person/{person_id}")
    fun getPeopleDetail(
        @Path(value = "person_id") personId: Int,
        @Query(value = "append_to_response") appendToResponse: String = "images, combined_credits, external_ids",
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "include_image_language") includeImageLanguage: String = "ko"
    ): Single<ApiResponse<NetworkTMDBPeopleDetail>>

    @GET(value = "/3/person/{person_id}/combined_credits")
    fun getCombineCredits(
        @Path(value = "person_id") personId: Int,
        @Query(value = "language") language: String = "ko-KR"
    ): Single<ApiResponse<NetworkTMDBCombineCredits>>

    @GET(value = "/3/person/{person_id}/external_ids")
    fun getExternalIds(
        @Path(value = "person_id") personId: Int
    ): Single<ApiResponse<NetworkTMDBExternalIds>>
}

interface TvApis {
    @GET(value = "/3/tv/{series_id}/similar")
    fun getSimilarTv(
        @Path(value = "series_id") id: Int,
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "page") page: Int = 1,
    ): Single<ApiResponse<NetworkTMDBSimilarMedia>>

    @GET(value = "/3/tv/{series_id}")
    fun getTv(
        @Path(value = "series_id") id: Int,
        @Query(value = "append_to_response") appendToResponse: String = "images,videos,credits,releases,alternative_titles",
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "include_image_language") includeImageLanguage: String = "ko"
    ): Single<ApiResponse<NetworkTMDBTv>>

    @GET(value = "/3/tv/{series_id}/season/{season_number}")
    fun getTvSeasons(
        @Path(value = "series_id") seriesId: Int,
        @Path(value = "season_number") seasonNumber: Int,
        @Query(value = "append_to_response") appendToResponse: String = "images,videos,credits,releases,alternative_titles",
        @Query(value = "language") language: String = "ko-KR"
    ): Single<ApiResponse<NetworkTMDBTvSeasons>>

    @GET(value = "/3/tv/{series_id}/season/{season_number}/episode/{episode_number}")
    fun getTvEpisode(
        @Path(value = "series_id") seriesId: Int,
        @Path(value = "season_number") seasonNumber: Int,
        @Path(value = "episode_number") episodeNumber: Int,
        @Query(value = "append_to_response") appendToResponse: String = "images,videos,credits,releases,alternative_titles",
        @Query(value = "language") language: String = "ko-KR"
    ): Single<ApiResponse<NetworkTMDBTvEpisode>>

    @GET(value = "/3/tv/{series_id}/reviews")
    fun getTvReview(
        @Path(value = "series_id") seriesId: Int,
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "page") page: Int = 1
    ): Single<ApiResponse<NetworkTMDBTvReviews>>
}

interface SeriesApis {
    @GET(value = "/3/collection/{collection_id}")
    fun getMovieSeries(
        @Path(value = "collection_id") collectionId: Int,
        @Query(value = "language") language: String = "ko-KR",
    ): Single<ApiResponse<NetworkTMDBMovieSeries>>

    @GET(value = "/3/collection/{collection_id}/images")
    fun getSeriesImages(
        @Path(value = "collection_id") collectionId: Int,
        @Query(value = "include_image_language") includeImageLanguage: String = "ko-KR,null",
        @Query(value = "language") language: String = "ko-KR"
    ): Single<ApiResponse<NetworkTMDBImageList>>
}

interface SearchApis {
    @GET(value = "/3/search/multi")
    suspend fun searchMulti(
        @Query(value = "query") query: String,
        @Query(value = "include_adult") includeAdult: Boolean = true,
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "page") page: Int = 1
    ): ApiResponse<NetworkTMDBSearch>

    @GET(value = "/3/search/movie")
    suspend fun searchMovies(
        @Query(value = "query") query: String,
        @Query(value = "include_adult") includeAdult: Boolean = true,
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "region") region: String = "KR",
        @Query(value = "page") page: Int = 1
    ): ApiResponse<NetworkTMDBSearch>

    @GET(value = "/3/search/tv")
    suspend fun searchTv(
        @Query(value = "query") query: String,
        @Query(value = "include_adult") includeAdult: Boolean = true,
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "region") region: String = "KR",
        @Query(value = "page") page: Int = 1
    ): ApiResponse<NetworkTMDBSearch>

    @GET(value = "/3/search/person")
    suspend fun searchPeople(
        @Query(value = "query") query: String,
        @Query(value = "include_adult") includeAdult: Boolean = true,
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "region") region: String = "KR",
        @Query(value = "page") page: Int = 1
    ): ApiResponse<NetworkTMDBSearch>

    @GET(value = "/3/search/collection")
    suspend fun searchMovieSeries(
        @Query(value = "query") query: String,
        @Query(value = "include_adult") includeAdult: Boolean = true,
        @Query(value = "page") page: Int = 1,
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "region") region: String = "KR"
    ): ApiResponse<NetworkTMDBSearch>

    @GET(value = "/3/search/keyword")
    suspend fun getSearchKeyword(
        @Query(value = "query") query: String,
        @Query(value = "page") page: Int
    ): ApiResponse<NetworkTMDBSearchKeywordData>
}

interface SyncApis {
    @GET(value = "/3/movie/now_playing")
    fun getNowPlaying(
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "region") region: String = "KR",
        @Query(value = "page") page: Int = 1
    ): Single<ApiResponse<NetworkTMDBMovieList>>

    @GET(value = "/3/movie/upcoming")
    fun getUpcomingMovie(
        @Query(value = "language") language: String = "ko-KR",
        @Query(value = "region") region: String = "KR",
        @Query(value = "page") page: Int = 1
    ): Single<ApiResponse<NetworkTMDBMovieList>>
}

interface TrendingApis {
    @GET(value = "/3/trending/movie/{time_window}")
    fun getTrendingMovie(
        @Path(value = "time_window") timeWindow: String,
        @Query(value = "language") language: String,
        @Query(value = "page") page: Int = 1
    ): Single<ApiResponse<NetworkTMDBTrendingMedia>>

    @GET(value = "/3/trending/person/{time_window}")
    fun getTrendingPeople(
        @Path(value = "time_window") timeWindow: String,
        @Query(value = "language") language: String,
        @Query(value = "page") page: Int = 1
    ): Single<ApiResponse<NetworkTMDBTrendingMedia>>

    @GET(value = "/3/trending/tv/{time_window}")
    fun getTrendingTv(
        @Path(value = "time_window") timeWindow: String,
        @Query(value = "language") language: String,
        @Query(value = "page") page: Int = 1
    ): Single<ApiResponse<NetworkTMDBTrendingMedia>>
}