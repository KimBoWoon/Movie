package com.bowoon.network

import com.bowoon.model.CertificationData
import com.bowoon.model.CombineCredits
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalIds
import com.bowoon.model.LanguageItem
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieGenreList
import com.bowoon.model.MovieSearchData
import com.bowoon.model.PeopleDetail
import com.bowoon.model.PeopleSearchData
import com.bowoon.model.RegionList
import com.bowoon.model.SimilarMovies

interface MovieNetworkDataSource {
    suspend fun getConfiguration(): Configuration

    suspend fun getCertification(): CertificationData

    suspend fun getGenres(language: String = "ko-KR"): MovieGenreList

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
    ): MovieSearchData

    suspend fun searchPeople(
        query: String,
        includeAdult: Boolean = true,
        language: String = "ko-KR",
        region: String = "KR",
        page: Int = 1
    ): PeopleSearchData

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
        region: String = "KR"
    ): MovieSearchData

    suspend fun getAvailableLanguage(): List<LanguageItem>

    suspend fun getAvailableRegion(): RegionList

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