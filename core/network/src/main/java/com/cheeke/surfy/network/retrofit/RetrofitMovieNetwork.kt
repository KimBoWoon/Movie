package com.cheeke.surfy.network.retrofit

import com.cheeke.surfy.model.CertificationData
import com.cheeke.surfy.model.CombineCredits
import com.cheeke.surfy.model.Configuration
import com.cheeke.surfy.model.ExternalIds
import com.cheeke.surfy.model.Genres
import com.cheeke.surfy.model.Language
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.MovieResult
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
import com.cheeke.surfy.model.asExternalMovie
import com.cheeke.surfy.network.ApiResponse
import com.cheeke.surfy.network.CustomCallAdapter
import com.cheeke.surfy.network.MovieNetworkDataSource
import com.cheeke.surfy.network.TMDBApis
import com.cheeke.surfy.network.model.asExternalModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 데이터를 가져오는 Api
 * @param retrofit 레트로핏 모듈
 */
@Singleton
class RetrofitMovieNetwork @Inject constructor(
    tmdbUrl: String,
    client: OkHttpClient,
    customCallAdapter: CustomCallAdapter,
    serialization: Json,
    jsonMediaType: MediaType
) : MovieNetworkDataSource {
    private val tmdbApis = Retrofit.Builder()
        .baseUrl(tmdbUrl)
        .addCallAdapterFactory(customCallAdapter)
        .addConverterFactory(serialization.asConverterFactory(jsonMediaType))
        .client(client)
        .build()
        .create(TMDBApis::class.java)

    override suspend fun getConfiguration(): Configuration =
        when (val response = tmdbApis.getConfiguration()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }

    override suspend fun getCertification(): CertificationData =
        when (val response = tmdbApis.getCertification()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }

    override suspend fun getMovieGenres(language: String): Genres =
        when (val response = tmdbApis.getMovieGenres(language = language)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }

    override suspend fun getTvGenres(language: String): Genres = when (val response = tmdbApis.getTvGenres(language = language)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getNowPlaying(
        language: String,
        region: String,
        page: Int
    ): List<Movie> {
        val result = mutableListOf<Movie>()
        var currentPage = page
        var totalPage: Int

        do {
            when (val response = tmdbApis.getNowPlaying(language = "$language-$region", region = region, page = currentPage)) {
                is ApiResponse.Failure -> throw response.throwable
                is ApiResponse.Success -> {
                    currentPage = (response.data.page ?: 1) + 1
                    totalPage = response.data.totalPages ?: Int.MAX_VALUE
                    result.addAll(
                        response.data.asExternalModel().results?.map(MovieResult::asExternalMovie) ?: emptyList()
                    )
                }
            }
        } while (currentPage <= totalPage)

        return result.distinctBy { it.id }
    }

    override suspend fun getUpcomingMovie(
        language: String,
        region: String,
        page: Int
    ): List<Movie> {
        val result = mutableListOf<Movie>()
        var currentPage = 1
        var totalPage: Int

        do {
            when (val response = tmdbApis.getUpcomingMovie(language = "$language-$region", region = region, page = currentPage)) {
                is ApiResponse.Failure -> throw response.throwable
                is ApiResponse.Success -> {
                    currentPage = (response.data.page ?: 1) + 1
                    totalPage = response.data.totalPages ?: Int.MAX_VALUE
                    result.addAll(
                        response.data.asExternalModel().results?.map(MovieResult::asExternalMovie) ?: emptyList()
                    )
                }
            }
        } while (currentPage <= totalPage)

        return result.filter { (it.releaseDate ?: "") > LocalDate.now().toString() }.distinctBy { it.id }.sortedBy { it.releaseDate }
    }

    override suspend fun searchMulti(
        query: String,
        includeAdult: Boolean,
        language: String,
        page: Int
    ): SearchData = when (
        val response = tmdbApis.searchMulti(
            query = query,
            includeAdult = includeAdult,
            language = language,
            page = page
        )
    ) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun searchMovies(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): SearchData = when (
        val response = tmdbApis.searchMovies(
            query = query,
            includeAdult = includeAdult,
            language = language,
            region = region,
            page = page
        )
    ) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun searchTv(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): SearchData = when (
        val response = tmdbApis.searchTv(
            query = query,
            includeAdult = includeAdult,
            language = language,
            region = region,
            page = page
        )
    ) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun searchPeople(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): SearchData = when (
        val response = tmdbApis.searchPeople(
            query = query,
            includeAdult = includeAdult,
            language = language,
            region = region,
            page = page
        )
    ) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun searchSeries(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): SearchData = when (
        val response = tmdbApis.searchMovieSeries(
            query = query,
            includeAdult = includeAdult,
            language = language,
            region = region,
            page = page
        )
    ) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getMovieSeries(collectionId: Int, language: String): Series =
        when (val response = tmdbApis.getMovieSeries(collectionId = collectionId, language = language)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }

    override suspend fun getMovie(
        id: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String,
        region: String
    ): Movie = when (
        val response = tmdbApis.getMovie(
            id = id,
            appendToResponse = appendToResponse,
            language = language,
            includeImageLanguage = includeImageLanguage
        )
    ) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getSimilarMovies(
        id: Int,
        language: String,
        page: Int
    ): SimilarMovies = when (val response = tmdbApis.getSimilarMovies(id = id, language = language, page = page)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getSimilarTv(
        id: Int,
        language: String,
        page: Int
    ): SimilarTvs = when (val response = tmdbApis.getSimilarTv(id = id, language = language, page = page)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int,
        sortBy: String,
        withReleaseType: String
    ): SearchData = when (
        val response = tmdbApis.discoverMovie(
            releaseDateGte = releaseDateGte,
            releaseDateLte = releaseDateLte,
            includeAdult = includeAdult,
            language = language,
            region = region,
            page = page,
            sortBy = sortBy,
            withReleaseType = withReleaseType
        )
    ) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getAvailableLanguage(): List<Language> = when (val response = tmdbApis.getAvailableLanguage()) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getAvailableRegion(): Regions = when (val response = tmdbApis.getAvailableRegion()) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getPeopleDetail(
        personId: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String
    ): People = when (
        val response = tmdbApis.getPeopleDetail(
            personId = personId,
            appendToResponse = appendToResponse,
            language = language,
            includeImageLanguage = includeImageLanguage
        )
    ) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getCombineCredits(
        personId: Int,
        language: String
    ): CombineCredits = when (val response = tmdbApis.getCombineCredits(personId = personId, language = language)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getExternalIds(personId: Int): ExternalIds = when (val response = tmdbApis.getExternalIds(personId)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getSearchKeyword(query: String, page: Int): SearchKeywordData = when (val response = tmdbApis.getSearchKeyword(query = query, page = page)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getMovieReviews(
        movieId: Int,
        language: String,
        page: Int
    ): Reviews = when (val response = tmdbApis.getMovieReview(movieId = movieId, language = language, page = page)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getTvReviews(
        seriesId: Int,
        language: String,
        page: Int
    ): Reviews = when (val response = tmdbApis.getTvReview(seriesId = seriesId, language = language, page = page)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getTrendingMovie(timeWindow: String, language: String, page: Int): TrendingMovie =
        when (val response = tmdbApis.getTrendingMovie(timeWindow = timeWindow, language = language, page = page)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }

    override suspend fun getTrendingPeople(timeWindow: String, language: String, page: Int): TrendingPeople =
        when (val response = tmdbApis.getTrendingPeople(timeWindow = timeWindow, language = language, page = page)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }

    override suspend fun getTrendingTv(timeWindow: String, language: String, page: Int): TrendingTv =
        when (val response = tmdbApis.getTrendingTv(timeWindow = timeWindow, language = language, page = page)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> response.data.asExternalModel()
        }

    override suspend fun getTv(
        id: Int,
        language: String,
        appendToResponse: String,
        includeImageLanguage: String
    ): Tv = when (val response = tmdbApis.getTv(id = id, appendToResponse = appendToResponse, language = language, includeImageLanguage = includeImageLanguage)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getTvSeasons(
        seriesId: Int,
        seasonNumber: Int,
        appendToResponse: String,
        language: String
    ): TvSeasons = when (val response = tmdbApis.getTvSeasons(seriesId = seriesId, seasonNumber = seasonNumber, appendToResponse = appendToResponse, language = language)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getTvEpisode(
        seriesId: Int,
        seasonNumber: Int,
        episodeNumber: Int,
        appendToResponse: String,
        language: String
    ): TvEpisode = when (val response = tmdbApis.getTvEpisode(seriesId = seriesId, seasonNumber = seasonNumber, episodeNumber = episodeNumber, appendToResponse = appendToResponse, language = language)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getMovieWatchProvider(movieId: Int): MovieWatchProvider = when (val response = tmdbApis.getMovieWatchProvider(movieId = movieId)) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }
}