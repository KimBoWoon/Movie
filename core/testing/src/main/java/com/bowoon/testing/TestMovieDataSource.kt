package com.bowoon.testing

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
import com.bowoon.network.MovieNetworkDataSource
import com.bowoon.testing.model.certificationTestData
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.nowPlayingMoviesTestData
import com.bowoon.testing.model.peopleSearchTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.model.similarMoviesTestData
import com.bowoon.testing.model.upcomingMoviesTestData
import com.bowoon.testing.repository.combineCreditsTestData
import com.bowoon.testing.repository.externalIdsTestData
import com.bowoon.testing.repository.movieDetailTestData
import com.bowoon.testing.repository.movieSearchTestData
import com.bowoon.testing.repository.peopleDetailTestData

class TestMovieDataSource : MovieNetworkDataSource {
    override suspend fun getConfiguration(): Configuration = configurationTestData

    override suspend fun getCertification(): CertificationData = certificationTestData

    override suspend fun getGenres(language: String): MovieGenreList = genreListTestData

    override suspend fun getNowPlaying(language: String, region: String, page: Int): List<Movie> = nowPlayingMoviesTestData

    override suspend fun getUpcomingMovie(
        language: String,
        region: String,
        page: Int
    ): List<Movie> = upcomingMoviesTestData

    override suspend fun searchMovies(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): MovieSearchData = movieSearchTestData

    override suspend fun searchPeople(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): PeopleSearchData = peopleSearchTestData

    override suspend fun getMovieDetail(
        id: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String,
        region: String
    ): MovieDetail = movieDetailTestData

    override suspend fun getSimilarMovies(id: Int, language: String, page: Int): SimilarMovies = similarMoviesTestData

    override suspend fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String,
        includeAdult: Boolean,
        language: String,
        region: String
    ): MovieSearchData = movieSearchTestData

    override suspend fun getAvailableLanguage(): List<LanguageItem> = languageListTestData

    override suspend fun getAvailableRegion(): RegionList = regionTestData

    override suspend fun getPeopleDetail(
        personId: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String
    ): PeopleDetail = peopleDetailTestData

    override suspend fun getCombineCredits(personId: Int, language: String): CombineCredits = combineCreditsTestData

    override suspend fun getExternalIds(personId: Int): ExternalIds = externalIdsTestData
}