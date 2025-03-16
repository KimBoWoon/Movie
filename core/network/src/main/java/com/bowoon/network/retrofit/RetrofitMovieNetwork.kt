package com.bowoon.network.retrofit

import com.bowoon.model.CertificationData
import com.bowoon.model.CombineCredits
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalIds
import com.bowoon.model.LanguageItem
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieGenreList
import com.bowoon.model.MovieResult
import com.bowoon.model.MovieSearchData
import com.bowoon.model.PeopleDetail
import com.bowoon.model.PeopleSearchData
import com.bowoon.model.RegionList
import com.bowoon.model.SimilarMovies
import com.bowoon.model.asExternalMovie
import com.bowoon.network.ApiResponse
import com.bowoon.network.CustomCallAdapter
import com.bowoon.network.MovieNetworkDataSource
import com.bowoon.network.TMDBApis
import com.bowoon.network.model.asExternalModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import org.threeten.bp.LocalDate
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 데이터를 가져오는 Api
 * @param retrofit 레트로핏 모듈
 */
@Singleton
class RetrofitMovieNetwork @Inject constructor(
    client: OkHttpClient,
    customCallAdapter: CustomCallAdapter,
    serialization: Json,
    jsonMediaType: MediaType
) : MovieNetworkDataSource {
    private val tmdbApis = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/")
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

    override suspend fun getGenres(language: String): MovieGenreList =
        when (val response = tmdbApis.getGenres(language = language)) {
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
        var totalPage = 1

        do {
            when (val response = tmdbApis.getNowPlaying(language = "$language-$region", region = region, page = currentPage)) {
                is ApiResponse.Failure -> throw response.throwable
                is ApiResponse.Success -> {
                    currentPage = ((response.data.page ?: 1) + 1)
                    totalPage = response.data.totalPages ?: Int.MAX_VALUE
                    result.addAll(
                        response.data.asExternalModel().results?.map(MovieResult::asExternalMovie) ?: emptyList()
                    )
                }
            }
        } while (currentPage <= totalPage && currentPage <= 5)

        return result.distinctBy { it.id }.sortedWith { o1, o2 ->
            if (o1 != null && o2 != null) {
                if (o1.voteAverage == o2.voteAverage) {
                    o1.title?.compareTo(o2.title ?: "") ?: 0
                } else {
                    o2.voteAverage?.compareTo(o1.voteAverage ?: 0.0) ?: 0
                }
            } else 0
        }
    }

    override suspend fun getUpcomingMovie(
        language: String,
        region: String,
        page: Int
    ): List<Movie> {
        val result = mutableListOf<Movie>()
        var currentPage = 1
        var totalPage = 1

        do {
            when (val response = tmdbApis.getUpcomingMovie(language = "$language-$region", region = region, page = currentPage)) {
                is ApiResponse.Failure -> throw response.throwable
                is ApiResponse.Success -> {
                    currentPage = ((response.data.page ?: 1) + 1)
                    totalPage = response.data.totalPages ?: Int.MAX_VALUE
                    result.addAll(
                        response.data.asExternalModel().results?.map(MovieResult::asExternalMovie) ?: emptyList()
                    )
                }
            }
        } while (currentPage <= totalPage && currentPage <= 5)

        return result.filter { (it.releaseDate ?: "") > LocalDate.now().toString() }.distinctBy { it.id }.sortedBy { it.releaseDate }
    }

    override suspend fun searchMovies(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): MovieSearchData = when (
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

    override suspend fun searchPeople(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): PeopleSearchData = when (
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

    override suspend fun getMovieDetail(
        id: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String,
        region: String
    ): MovieDetail = when (
        val response = tmdbApis.getMovieDetail(
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

    override suspend fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String,
        includeAdult: Boolean,
        language: String,
        region: String
    ): MovieSearchData = when (
        val response = tmdbApis.discoverMovie(
            releaseDateGte = releaseDateGte,
            releaseDateLte = releaseDateLte,
            includeAdult = includeAdult,
            language = language,
            region = region
        )
    ) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getAvailableLanguage(): List<LanguageItem> = when (val response = tmdbApis.getAvailableLanguage()) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getAvailableRegion(): RegionList = when (val response = tmdbApis.getAvailableRegion()) {
        is ApiResponse.Failure -> throw response.throwable
        is ApiResponse.Success -> response.data.asExternalModel()
    }

    override suspend fun getPeopleDetail(
        personId: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String
    ): PeopleDetail = when (
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
}