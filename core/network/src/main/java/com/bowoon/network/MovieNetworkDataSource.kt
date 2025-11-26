package com.bowoon.network

import com.bowoon.model.CertificationData
import com.bowoon.model.CombineCredits
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalIds
import com.bowoon.model.Genres
import com.bowoon.model.Language
import com.bowoon.model.Movie
import com.bowoon.model.MovieReviews
import com.bowoon.model.People
import com.bowoon.model.Regions
import com.bowoon.model.SearchData
import com.bowoon.model.SearchKeywordData
import com.bowoon.model.Series
import com.bowoon.model.SimilarMovies

interface MovieNetworkDataSource {
    suspend fun getConfiguration(): Configuration

    suspend fun getCertification(): CertificationData

    suspend fun getGenres(language: String = "ko-KR"): Genres

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

    suspend fun searchMovies(
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
    ): MovieReviews
}