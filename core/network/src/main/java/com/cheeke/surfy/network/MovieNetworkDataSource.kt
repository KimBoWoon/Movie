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
import com.cheeke.surfy.model.SimilarMovies
import com.cheeke.surfy.model.SimilarTvs
import com.cheeke.surfy.model.TrendingMovie
import com.cheeke.surfy.model.TrendingPeople
import com.cheeke.surfy.model.TrendingTv
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeasons

interface MovieNetworkDataSource {
    suspend fun getConfiguration(): Configuration

    suspend fun getCertification(): CertificationData

    suspend fun getMovieGenres(language: String = "ko-KR"): Genres

    suspend fun getTvGenres(language: String): Genres

    suspend fun getNowPlaying(
        language: String = "ko-KR",
        region: String = "KR",
        page: Int = 1
    ): List<Movie>

    suspend fun getUpcomingMovie(
        language: String = "ko-KR",
        region: String = "KR",
        page: Int = 1
    ): List<Movie>

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

    suspend fun getMovieSeries(
        collectionId: Int,
        language: String = "ko-KR"
    ): Series

    suspend fun getMovie(
        id: Int,
        appendToResponse: String = "images,videos,credits,releases,keywords,alternative_titles",
        language: String = "ko-KR",
        includeImageLanguage: String = "ko",
        region: String = "KR"
    ): Movie

    suspend fun getSimilarMovies(
        id: Int,
        language: String = "ko-KR",
        page: Int = 1
    ): SimilarMovies

    suspend fun getSimilarTv(
        id: Int,
        language: String = "ko-KR",
        page: Int = 1
    ): SimilarTvs

    suspend fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String,
        includeAdult: Boolean = true,
        language: String = "ko-KR",
        region: String = "KR",
        page: Int = 1,
        sortBy: String = "primary_release_date.asc",
        withReleaseType: String = "2|3"
    ): SearchData

    suspend fun getAvailableLanguage(): List<Language>

    suspend fun getAvailableRegion(): Regions

    suspend fun getPeopleDetail(
        personId: Int,
        appendToResponse: String = "images, combined_credits, external_ids",
        language: String = "ko-KR",
        includeImageLanguage: String = "ko"
    ): People

    suspend fun getCombineCredits(
        personId: Int,
        language: String = "ko-KR"
    ): CombineCredits

    suspend fun getExternalIds(
        personId: Int
    ): ExternalIds

    suspend fun getSearchKeyword(
        query: String,
        page: Int
    ): SearchKeywordData

    suspend fun getMovieReviews(
        movieId: Int,
        language: String = "ko-KR",
        page: Int = 1
    ): Reviews

    suspend fun getTvReviews(
        seriesId: Int,
        language: String = "ko-KR",
        page: Int = 1
    ): Reviews

    suspend fun getTrendingMovie(timeWindow: String, language: String, page: Int): TrendingMovie

    suspend fun getTrendingPeople(
        timeWindow: String,
        language: String,
        page: Int = 1
    ): TrendingPeople

    suspend fun getTrendingTv(timeWindow: String, language: String, page: Int): TrendingTv

    suspend fun getTv(
        id: Int,
        language: String,
        appendToResponse: String = "images,videos,credits,releases,keywords,alternative_titles",
        includeImageLanguage: String = "ko"
    ): Tv

    suspend fun getTvSeasons(
        seriesId: Int,
        seasonNumber: Int,
        appendToResponse: String = "images,videos,credits,releases,keywords,alternative_titles",
        language: String = "ko-KR"
    ): TvSeasons

    suspend fun getTvEpisode(
        seriesId: Int,
        seasonNumber: Int,
        episodeNumber: Int,
        appendToResponse: String = "images,videos,credits,releases,keywords,alternative_titles",
        language: String = "ko-KR"
    ): TvEpisode

    suspend fun getMovieWatchProvider(
        movieId: Int
    ): MovieWatchProvider

    suspend fun getSeriesImages(
        collectionId: Int,
        includeImageLanguage: String,
        language: String
    ): ImageList
}