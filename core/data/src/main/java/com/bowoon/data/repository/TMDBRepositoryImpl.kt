package com.bowoon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.data.util.suspendRunCatching
import com.bowoon.model.MyData
import com.bowoon.model.PosterSize
import com.bowoon.model.TMDBCombineCredits
import com.bowoon.model.TMDBConfiguration
import com.bowoon.model.TMDBExternalIds
import com.bowoon.model.TMDBLanguageItem
import com.bowoon.model.TMDBMovieDetail
import com.bowoon.model.TMDBPeopleDetail
import com.bowoon.model.TMDBRegion
import com.bowoon.model.TMDBRegionResult
import com.bowoon.model.TMDBSearch
import com.bowoon.model.TMDBSearchResult
import com.bowoon.model.UpComingResult
import com.bowoon.network.ApiResponse
import com.bowoon.network.model.asExternalModel
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TMDBRepositoryImpl @Inject constructor(
    private val apis: Apis,
    private val userDataRepository: UserDataRepository
) : TMDBRepository {
    override val posterUrl: Flow<String> = userDataRepository.userData.map { "${it.myData?.secureBaseUrl}${it.imageQuality}" }

    override suspend fun syncWith(): Boolean = suspendRunCatching {
        val configuration = getConfiguration().first()
        val region = getAvailableRegion().first()
        val language = getAvailableLanguage().first()

        MyData(
            secureBaseUrl = configuration.images?.secureBaseUrl,
            region = region.results?.map {
                TMDBRegionResult(
                    englishName = it.englishName,
                    iso31661 = it.iso31661,
                    nativeName = it.nativeName,
                    isSelected = userDataRepository.getRegion() == it.iso31661
                )
            },
            language = language.map {
                TMDBLanguageItem(
                    englishName = it.englishName,
                    iso6391 = it.iso6391,
                    name = it.name,
                    isSelected = userDataRepository.getLanguage() == it.iso6391
                )
            },
            posterSize = configuration.images?.posterSizes?.map {
                PosterSize(
                    size = it,
                    isSelected = userDataRepository.getImageQuality() == it
                )
            } ?: emptyList(),
        ).also {
            userDataRepository.updateMyData(it)
        }
    }.isSuccess

    override suspend fun searchMovies(
        query: String
    ): Flow<PagingData<TMDBSearchResult>> = Pager(
        config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
        pagingSourceFactory = {
            TMDBSearchPagingSource(
                apis = apis,
                query = query,
                userDataRepository = userDataRepository
            )
        }
    ).flow

    override fun getUpcomingMovies(): Flow<List<UpComingResult>> = flow {
        val result = mutableListOf<UpComingResult>()
        var page = 1
        var totalPage = 1
        val language = "${userDataRepository.getLanguage()}-${userDataRepository.getRegion()}"
        val region = userDataRepository.getRegion()

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
                                posterPath = "${posterUrl.firstOrNull()}${it.posterPath}",
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

        emit(result.distinctBy { it.id })
    }

    override fun getMovieDetail(id: Int): Flow<TMDBMovieDetail> = flow {
        val language = "${userDataRepository.getLanguage()}-${userDataRepository.getRegion()}"
        val region = userDataRepository.getRegion()

        when (val response = apis.tmdbApis.getMovieDetail(id = id, language = language, region = region)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<TMDBSearch> = flow {
        val language = "${userDataRepository.getLanguage()}-${userDataRepository.getRegion()}"
        val region = userDataRepository.getRegion()

        when (val response = apis.tmdbApis.discoverMovie(releaseDateGte = releaseDateGte, releaseDateLte = releaseDateLte, language = language, region = region)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getConfiguration(): Flow<TMDBConfiguration> = flow {
        when (val response = apis.tmdbApis.getConfiguration()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getAvailableLanguage(): Flow<List<TMDBLanguageItem>> = flow {
        when (val response = apis.tmdbApis.getAvailableLanguage()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getAvailableRegion(): Flow<TMDBRegion> = flow {
        when (val response = apis.tmdbApis.getAvailableRegion()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getPeople(personId: Int): Flow<TMDBPeopleDetail> = flow {
        val language = "${userDataRepository.getLanguage()}-${userDataRepository.getRegion()}"

        when (val response = apis.tmdbApis.getPeopleDetail(personId = personId, language = language)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun getCombineCredits(personId: Int): Flow<TMDBCombineCredits> = flow {
        val language = "${userDataRepository.getLanguage()}-${userDataRepository.getRegion()}"

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