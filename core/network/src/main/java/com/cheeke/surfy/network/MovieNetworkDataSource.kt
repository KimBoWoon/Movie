package com.cheeke.surfy.network

import com.cheeke.surfy.model.CertificationData
import com.cheeke.surfy.model.CombineCredits
import com.cheeke.surfy.model.Configuration
import com.cheeke.surfy.model.ExternalIds
import com.cheeke.surfy.model.Genres
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Language
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.MovieWatchProvider
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Regions
import com.cheeke.surfy.model.Reviews
import com.cheeke.surfy.model.SearchData
import com.cheeke.surfy.model.SearchKeywordData
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.SimilarMedias
import com.cheeke.surfy.model.TrendingMedia
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeasons
import io.reactivex.rxjava3.core.Single

interface SettingRemoteDataSource {
    fun getConfiguration(): Single<Configuration>
    fun getCertification(): Single<CertificationData>
    fun getAvailableLanguage(): Single<List<Language>>
    fun getAvailableRegion(): Single<Regions>
    fun getMovieGenres(language: String = "ko-KR"): Single<Genres>
    fun getTvGenres(language: String): Single<Genres>
}

interface MovieRemoteDataSource {
    fun getMovie(
        id: Int,
        appendToResponse: String = "images,videos,credits,releases,keywords,alternative_titles,similar,reviews",
        language: String = "ko-KR",
        includeImageLanguage: String = "ko",
        region: String = "KR"
    ): Single<Movie>

    fun getSimilarMovies(
        id: Int,
        language: String = "ko-KR",
        page: Int = 1
    ): Single<SimilarMedias>

    fun getMovieReviews(
        movieId: Int,
        language: String = "ko-KR",
        page: Int = 1
    ): Single<Reviews>

    fun getMovieWatchProvider(
        movieId: Int
    ): Single<MovieWatchProvider>
}

interface PeopleRemoteDataSource {
    fun getPeopleDetail(
        personId: Int,
        appendToResponse: String = "images, combined_credits, external_ids",
        language: String = "ko-KR",
        includeImageLanguage: String = "ko"
    ): Single<People>

    fun getCombineCredits(
        personId: Int,
        language: String = "ko-KR"
    ): Single<CombineCredits>

    fun getExternalIds(
        personId: Int
    ): Single<ExternalIds>
}

interface TvRemoteDataSource {
    fun getTv(
        id: Int,
        language: String,
        appendToResponse: String = "images,videos,credits,releases,keywords,alternative_titles,similar,reviews",
        includeImageLanguage: String = "ko"
    ): Single<Tv>

    fun getTvSeasons(
        seriesId: Int,
        seasonNumber: Int,
        appendToResponse: String = "images,videos,credits,releases,keywords,alternative_titles",
        language: String = "ko-KR"
    ): Single<TvSeasons>

    fun getTvEpisode(
        seriesId: Int,
        seasonNumber: Int,
        episodeNumber: Int,
        appendToResponse: String = "images,videos,credits,releases,keywords,alternative_titles",
        language: String = "ko-KR"
    ): Single<TvEpisode>

    fun getSimilarTv(
        id: Int,
        language: String = "ko-KR",
        page: Int = 1
    ): Single<SimilarMedias>

    fun getTvReviews(
        seriesId: Int,
        language: String = "ko-KR",
        page: Int = 1
    ): Single<Reviews>
}

interface SeriesRemoteDataSource {
    fun getMovieSeries(
        collectionId: Int,
        language: String = "ko-KR"
    ): Single<Series>

    fun getSeriesImages(
        collectionId: Int,
        includeImageLanguage: String,
        language: String
    ): Single<ImageList>
}

interface SyncRemoteDataSource {
    fun getNowPlaying(
        language: String = "ko-KR",
        region: String = "KR",
        page: Int = 1
    ): Single<List<Movie>>

    fun getUpcomingMovie(
        language: String = "ko-KR",
        region: String = "KR",
        page: Int = 1
    ): Single<List<Movie>>
}

interface TrendingRemoteDataSource {
    fun getTrendingMovie(timeWindow: String, language: String, page: Int): Single<TrendingMedia>

    fun getTrendingPeople(
        timeWindow: String,
        language: String,
        page: Int = 1
    ): Single<TrendingMedia>

    fun getTrendingTv(timeWindow: String, language: String, page: Int): Single<TrendingMedia>
}

interface SearchRemoteDataSource {
    suspend fun searchMulti(
        query: String,
        includeAdult: Boolean = true,
        language: String = "ko-KR",
        page: Int = 1
    ): SearchData

    suspend fun searchMovies(
        query: String,
        includeAdult: Boolean = true,
        language: String = "ko-KR",
        region: String = "KR",
        page: Int = 1
    ): SearchData

    suspend fun searchTv(
        query: String,
        includeAdult: Boolean = true,
        language: String = "ko-KR",
        region: String = "KR",
        page: Int = 1
    ): SearchData

    suspend fun searchPeople(
        query: String,
        includeAdult: Boolean = true,
        language: String = "ko-KR",
        region: String = "KR",
        page: Int = 1
    ): SearchData

    suspend fun searchSeries(
        query: String,
        includeAdult: Boolean = true,
        language: String = "ko-KR",
        region: String = "KR",
        page: Int = 1
    ): SearchData

    suspend fun getSearchKeyword(
        query: String,
        page: Int
    ): SearchKeywordData
}