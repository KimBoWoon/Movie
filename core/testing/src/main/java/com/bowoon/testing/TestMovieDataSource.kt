package com.bowoon.testing

import com.bowoon.model.CertificationData
import com.bowoon.model.CombineCredits
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalIds
import com.bowoon.model.Genres
import com.bowoon.model.Language
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.Regions
import com.bowoon.model.Reviews
import com.bowoon.model.SearchData
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchKeywordData
import com.bowoon.model.Series
import com.bowoon.model.SimilarMovies
import com.bowoon.model.SimilarTvs
import com.bowoon.model.TrendingMovie
import com.bowoon.model.TrendingMovieResult
import com.bowoon.model.TrendingPeople
import com.bowoon.model.TrendingPeopleResult
import com.bowoon.model.TrendingTv
import com.bowoon.model.TrendingTvResult
import com.bowoon.model.Tv
import com.bowoon.model.TvEpisode
import com.bowoon.model.TvSeasons
import com.bowoon.network.MovieNetworkDataSource
import com.bowoon.testing.model.certificationTestData
import com.bowoon.testing.model.combineCreditsTestData
import com.bowoon.testing.model.configurationTestData
import com.bowoon.testing.model.externalIdsTestData
import com.bowoon.testing.model.favoriteMovieDetailTestData
import com.bowoon.testing.model.genreListTestData
import com.bowoon.testing.model.languageListTestData
import com.bowoon.testing.model.movieSearchTestData
import com.bowoon.testing.model.movieSeriesTestData
import com.bowoon.testing.model.nowPlayingMoviesTestData
import com.bowoon.testing.model.peopleDetailTestData
import com.bowoon.testing.model.peopleSearchTestData
import com.bowoon.testing.model.regionTestData
import com.bowoon.testing.model.seriesSearchTestData
import com.bowoon.testing.model.similarMoviesTestData
import com.bowoon.testing.model.similarTvTestData
import com.bowoon.testing.model.testMovieReviews
import com.bowoon.testing.model.tvSearchTestData
import com.bowoon.testing.model.upcomingMoviesTestData

class TestMovieDataSource : MovieNetworkDataSource {
    override suspend fun getConfiguration(): Configuration = configurationTestData

    override suspend fun getCertification(): CertificationData = certificationTestData

    override suspend fun getGenres(language: String): Genres = genreListTestData

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
    ): SearchData = movieSearchTestData

    override suspend fun searchPeople(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): SearchData = peopleSearchTestData

    override suspend fun searchSeries(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): SearchData = seriesSearchTestData

    override suspend fun getMovieSeries(collectionId: Int, language: String): Series = movieSeriesTestData

    override suspend fun getMovie(
        id: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String,
        region: String
    ): Movie = favoriteMovieDetailTestData

    override suspend fun getSimilarMovies(id: Int, language: String, page: Int): SimilarMovies = similarMoviesTestData

    override suspend fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int,
        sortBy: String,
        withReleaseType: String
    ): SearchData = movieSearchTestData

    override suspend fun getAvailableLanguage(): List<Language> = languageListTestData

    override suspend fun getAvailableRegion(): Regions = regionTestData

    override suspend fun getPeopleDetail(
        personId: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String
    ): People = peopleDetailTestData

    override suspend fun getCombineCredits(personId: Int, language: String): CombineCredits = combineCreditsTestData

    override suspend fun getExternalIds(personId: Int): ExternalIds = externalIdsTestData

    override suspend fun getSearchKeyword(query: String, page: Int): SearchKeywordData = SearchKeywordData(
        page = 1,
        results = listOf(
            SearchKeyword(id = 0, name = "mission0"),
            SearchKeyword(id = 1, name = "mission1"),
            SearchKeyword(id = 2, name = "mission2"),
            SearchKeyword(id = 3, name = "mission3"),
            SearchKeyword(id = 4, name = "mission4"),
            SearchKeyword(id = 5, name = "mission5")
        ),
        totalPages = 1,
        totalResults = 5
    )

    override suspend fun getMovieReviews(
        movieId: Int,
        language: String,
        page: Int
    ): Reviews = Reviews(
        id = 0,
        page = 1,
        results = testMovieReviews,
        totalPages = 1,
        totalResults = 0
    )

    override suspend fun searchTv(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): SearchData = tvSearchTestData

    override suspend fun getSimilarTv(
        id: Int,
        language: String,
        page: Int
    ): SimilarTvs = similarTvTestData

    override suspend fun getTvReviews(
        seriesId: Int,
        language: String,
        page: Int
    ): Reviews = Reviews(
        id = 0,
        page = 1,
        results = testMovieReviews,
        totalPages = 1,
        totalResults = 0
    )

    override suspend fun getTrendingMovie(
        timeWindow: String,
        language: String,
        page: Int
    ): TrendingMovie = TrendingMovie(
        page = 1,
        results = (0..100).map {
            TrendingMovieResult(
                id = it,
                title = "title_$it"
            )
        },
        totalPages = 1,
        totalResults = 0
    )

    override suspend fun getTrendingPeople(
        timeWindow: String,
        language: String,
        page: Int
    ): TrendingPeople = TrendingPeople(
        page = 1,
        results = (0..100).map {
            TrendingPeopleResult(
                id = it,
                title = "title_$it"
            )
        },
        totalPages = 1,
        totalResults = 0
    )

    override suspend fun getTrendingTv(
        timeWindow: String,
        language: String,
        page: Int
    ): TrendingTv = TrendingTv(
        page = 1,
        results = (0..100).map {
            TrendingTvResult(
                id = it,
                title = "title_$it"
            )
        },
        totalPages = 1,
        totalResults = 0
    )

    override suspend fun getTv(
        id: Int,
        language: String,
        appendToResponse: String,
        includeImageLanguage: String
    ): Tv = Tv(
        id = 0,
        title = "name_0"
    )

    override suspend fun getTvSeasons(
        seriesId: Int,
        seasonNumber: Int,
        appendToResponse: String,
        language: String
    ): TvSeasons = TvSeasons(
        id = 0,
        name = "name_0"
    )

    override suspend fun getTvEpisode(
        seriesId: Int,
        seasonNumber: Int,
        episodeNumber: Int,
        appendToResponse: String,
        language: String
    ): TvEpisode = TvEpisode(
        id = 0,
        name = "name_0"
    )
}