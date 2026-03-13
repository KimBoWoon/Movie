package com.cheeke.surfy.testing

import com.cheeke.surfy.model.CertificationData
import com.cheeke.surfy.model.CombineCredits
import com.cheeke.surfy.model.Configuration
import com.cheeke.surfy.model.ExternalIds
import com.cheeke.surfy.model.Genres
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Language
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.MovieWatchProvider
import com.cheeke.surfy.model.MovieWatchProviderResult
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Regions
import com.cheeke.surfy.model.Reviews
import com.cheeke.surfy.model.SearchData
import com.cheeke.surfy.model.SearchKeyword
import com.cheeke.surfy.model.SearchKeywordData
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.SimilarMovies
import com.cheeke.surfy.model.SimilarTvs
import com.cheeke.surfy.model.TrendingMovie
import com.cheeke.surfy.model.TrendingMovieResult
import com.cheeke.surfy.model.TrendingPeople
import com.cheeke.surfy.model.TrendingPeopleResult
import com.cheeke.surfy.model.TrendingTv
import com.cheeke.surfy.model.TrendingTvResult
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeasons
import com.cheeke.surfy.network.MovieNetworkDataSource
import com.cheeke.surfy.testing.model.certificationTestData
import com.cheeke.surfy.testing.model.combineCreditsTestData
import com.cheeke.surfy.testing.model.configurationTestData
import com.cheeke.surfy.testing.model.externalIdsTestData
import com.cheeke.surfy.testing.model.favoriteMovieDetailTestData
import com.cheeke.surfy.testing.model.genreListTestData
import com.cheeke.surfy.testing.model.languageListTestData
import com.cheeke.surfy.testing.model.movieSearchTestData
import com.cheeke.surfy.testing.model.movieSeriesTestData
import com.cheeke.surfy.testing.model.nowPlayingMoviesTestData
import com.cheeke.surfy.testing.model.peopleDetailTestData
import com.cheeke.surfy.testing.model.peopleSearchTestData
import com.cheeke.surfy.testing.model.regionTestData
import com.cheeke.surfy.testing.model.seriesSearchTestData
import com.cheeke.surfy.testing.model.similarMoviesTestData
import com.cheeke.surfy.testing.model.similarTvTestData
import com.cheeke.surfy.testing.model.testImageList
import com.cheeke.surfy.testing.model.testMovieReviews
import com.cheeke.surfy.testing.model.tvSearchTestData
import com.cheeke.surfy.testing.model.upcomingMoviesTestData

class TestMovieDataSource : MovieNetworkDataSource {
    override suspend fun getConfiguration(): Configuration = configurationTestData

    override suspend fun getCertification(): CertificationData = certificationTestData

    override suspend fun getMovieGenres(language: String): Genres = genreListTestData

    override suspend fun getTvGenres(language: String): Genres = genreListTestData

    override suspend fun getNowPlaying(language: String, region: String, page: Int): List<Movie> =
        nowPlayingMoviesTestData

    override suspend fun getUpcomingMovie(
        language: String,
        region: String,
        page: Int
    ): List<Movie> = upcomingMoviesTestData

    override suspend fun searchMulti(
        query: String,
        includeAdult: Boolean,
        language: String,
        page: Int
    ): SearchData = movieSearchTestData

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

    override suspend fun getSeriesImages(
        collectionId: Int,
        includeImageLanguage: String,
        language: String
    ): ImageList = testImageList

    override suspend fun getMovieSeries(collectionId: Int, language: String): Series =
        movieSeriesTestData

    override suspend fun getMovie(
        id: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String,
        region: String
    ): Movie = favoriteMovieDetailTestData

    override suspend fun getSimilarMovies(id: Int, language: String, page: Int): SimilarMovies =
        similarMoviesTestData

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

    override suspend fun getCombineCredits(personId: Int, language: String): CombineCredits =
        combineCreditsTestData

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

    override suspend fun getMovieWatchProvider(movieId: Int): MovieWatchProvider {
        return MovieWatchProvider(
            id = 0,
            results = mapOf(
                "KR" to MovieWatchProviderResult(
                    link = "link",
                    flatrate = listOf(),
                    buy = listOf(),
                    rent = listOf()
                )
            )
        )
    }
}