package com.bowoon.network

import com.bowoon.model.CertificationData
import com.bowoon.model.CombineCredits
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalIds
import com.bowoon.model.Genres
import com.bowoon.model.Language
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieList
import com.bowoon.model.MovieSeries
import com.bowoon.model.PeopleDetail
import com.bowoon.model.Regions
import com.bowoon.model.SearchData
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

    suspend fun getNowPlayingMovie(language: String, region: String, page: Int): MovieList
    suspend fun getUpComingMovie(language: String, region: String, page: Int): MovieList

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
    ): MovieSeries

    suspend fun getMovieDetail(
        id: Int,
        appendToResponse: String = "images,videos,credits,reviews,releases,translations,lists,keywords,alternative_titles,changes,similar",
        language: String = "ko-KR",
        includeImageLanguage: String = "ko",
        region: String = "KR"
    ): MovieDetail

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
    ): PeopleDetail

    suspend fun getCombineCredits(
        personId: Int,
        language: String = "ko-KR"
    ): CombineCredits

    suspend fun getExternalIds(
        personId: Int
    ): ExternalIds
}