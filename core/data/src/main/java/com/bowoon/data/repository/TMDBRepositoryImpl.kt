package com.bowoon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.UpComingResult
import com.bowoon.model.tmdb.TMDBCombineCredits
import com.bowoon.model.tmdb.TMDBExternalIds
import com.bowoon.model.tmdb.TMDBMovieDetail
import com.bowoon.model.tmdb.TMDBPeopleDetail
import com.bowoon.model.tmdb.TMDBSearch
import com.bowoon.model.tmdb.TMDBSearchResult
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import org.threeten.bp.LocalDate
import javax.inject.Inject

class TMDBRepositoryImpl @Inject constructor(
    private val apis: Apis,
    private val datastore: InternalDataSource,
    private val myDataRepository: MyDataRepositoryImpl
) : TMDBRepository {
    override suspend fun searchMovies(
        query: String
    ): Flow<PagingData<TMDBSearchResult>> {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

        return Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
            pagingSourceFactory = {
                TMDBSearchPagingSource(
                    apis = apis,
                    query = query,
                    language = language,
                    region = region,
                    posterUrl = myDataRepository.posterUrl
                )
            }
        ).flow
    }

    override fun getUpcomingMovies(): Flow<List<UpComingResult>> = flow {
        val result = mutableListOf<UpComingResult>()
        var page = 1
        var totalPage = 1
        val language = "${datastore.getLanguage()}-${datastore.getRegion()}"
        val region = datastore.getRegion()

        do {
            when (val response = apis.tmdbApis.getUpcomingMovie(language = language, region = region, page = page)) {
                is ApiResponse.Failure -> throw response.throwable
                is ApiResponse.Success -> {
                    page = ((response.data.page ?: 1) + 1)
                    totalPage = response.data.totalPages ?: Int.MAX_VALUE
                    result.addAll(
                        response.data.asExternalModel().results?.map {
                            UpComingResult(
                                adult = it.adult,
                                backdropPath = it.backdropPath,
                                genreIds = it.genreIds,
                                id = it.id,
                                originalLanguage = it.originalLanguage,
                                originalTitle = it.originalTitle,
                                overview = it.overview,
                                popularity = it.popularity,
                                posterPath = "${myDataRepository.posterUrl.firstOrNull()}${it.posterPath}",
                                releaseDate = it.releaseDate,
                                title = it.title,
                                video = it.video,
                                voteAverage = it.voteAverage,
                                voteCount = it.voteCount
                            )
                        } ?: emptyList()
                    )
                }
            }
        } while (page <= totalPage && page < 5)

        emit(result.filter { (it.releaseDate ?: "") > LocalDate.now().toString() }.distinctBy { it.id }.sortedBy { it.releaseDate })
    }

    override suspend fun getUpcomingMoviesTemp(): List<UpComingResult> {
        val result = mutableListOf<UpComingResult>()
        var page = 1
        var totalPage = 1
        val language = "${datastore.getLanguage()}-${datastore.getRegion()}"
        val region = datastore.getRegion()

        do {
            when (val response = apis.tmdbApis.getUpcomingMovie(language = language, region = region, page = page)) {
                is ApiResponse.Failure -> throw response.throwable
                is ApiResponse.Success -> {
                    page = ((response.data.page ?: 1) + 1)
                    totalPage = response.data.totalPages ?: Int.MAX_VALUE
                    result.addAll(
                        response.data.asExternalModel().results?.map {
                            UpComingResult(
                                adult = it.adult,
                                backdropPath = it.backdropPath,
                                genreIds = it.genreIds,
                                id = it.id,
                                originalLanguage = it.originalLanguage,
                                originalTitle = it.originalTitle,
                                overview = it.overview,
                                popularity = it.popularity,
                                posterPath = "${myDataRepository.posterUrl.firstOrNull()}${it.posterPath}",
                                releaseDate = it.releaseDate,
                                title = it.title,
                                video = it.video,
                                voteAverage = it.voteAverage,
                                voteCount = it.voteCount
                            )
                        } ?: emptyList()
                    )
                }
            }
        } while (page <= totalPage && page < 5)

        return result.filter { (it.releaseDate ?: "") > LocalDate.now().toString() }.distinctBy { it.id }.sortedBy { it.releaseDate }
    }

    override fun getMovieDetail(id: Int): Flow<TMDBMovieDetail> = flow {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

        when (val response = apis.tmdbApis.getMovieDetail(id = id, language = "$language-$region", region = region, includeImageLanguage = "$language,null")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<TMDBSearch> = flow {
        val language = "${datastore.getLanguage()}-${datastore.getRegion()}"
        val region = datastore.getRegion()

        when (val response = apis.tmdbApis.discoverMovie(releaseDateGte = releaseDateGte, releaseDateLte = releaseDateLte, language = language, region = region)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getPeople(personId: Int): Flow<TMDBPeopleDetail> = flow {
        val language = datastore.getLanguage()
        val region = datastore.getRegion()

        when (val response = apis.tmdbApis.getPeopleDetail(personId = personId, language = "$language-$region", includeImageLanguage = "$language,null")) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getCombineCredits(personId: Int): Flow<TMDBCombineCredits> = flow {
        val language = "${datastore.getLanguage()}-${datastore.getRegion()}"

        when (val response = apis.tmdbApis.getCombineCredits(personId = personId, language = language)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getExternalIds(personId: Int): Flow<TMDBExternalIds> = flow {
        when (val response = apis.tmdbApis.getExternalIds(personId = personId)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }
}