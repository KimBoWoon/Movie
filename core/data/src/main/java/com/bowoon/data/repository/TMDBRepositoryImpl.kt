package com.bowoon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.data.util.suspendRunCatching
import com.bowoon.model.MyData
import com.bowoon.model.PosterSize
import com.bowoon.model.TMDBConfiguration
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TMDBRepositoryImpl @Inject constructor(
    private val apis: Apis,
    private val userDataRepository: UserDataRepository
) : TMDBRepository {
    override val posterUrl: Flow<String> = userDataRepository.userData.map { "${it.myData?.configuration?.images?.secureBaseUrl}${it.imageQuality}" }

    override suspend fun syncWith(): Boolean = suspendRunCatching {
        val networkConfiguration = getConfiguration().first()
        val networkRegion = availableRegion().first()
        val networkLanguage = availableLanguage().first()
        val imageQuality = userDataRepository.userData.map { it.imageQuality }.first()

        val region = TMDBRegion(
            results = networkRegion.results?.map {
                TMDBRegionResult(
                    englishName = it.englishName,
                    iso31661 = it.iso31661,
                    nativeName = it.nativeName,
                    isSelected = userDataRepository.getRegion() == it.iso31661
                )
            }
        )
        val language = networkLanguage.map {
            TMDBLanguageItem(
                englishName = it.englishName,
                iso6391 = it.iso6391,
                name = it.name,
                isSelected = userDataRepository.getLanguage() == it.iso6391
            )
        }
        val posterSize = networkConfiguration.images?.posterSizes?.map {
            PosterSize(
                size = it,
                isSelected = imageQuality == it
            )
        } ?: emptyList()

        userDataRepository.updateMyData(
            MyData(
                configuration = networkConfiguration,
                region = region,
                language = language,
                posterSize = posterSize
            )
        )
    }.isSuccess

    override fun getConfiguration(): Flow<TMDBConfiguration> = flow {
        when (val response = apis.tmdbApis.getConfiguration()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

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

//    override suspend fun getUpcomingMovies(): Flow<PagingData<UpComingResult>> = Pager(
//        config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
//        pagingSourceFactory = {
//            TMDBUpcomingPagingSource(
//                apis = apis,
//                userDataRepository = userDataRepository
//            )
//        }
//    ).flow

    override fun getUpcomingMovies(): Flow<List<UpComingResult>> = flow {
        val result = mutableListOf<UpComingResult>()
        var page = 1
        var totalPage = 1
        val imageQuality = userDataRepository.getImageQuality()

        do {
            when (val response = apis.tmdbApis.getUpcomingMovie(page = page)) {
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
                                posterPath = "https://image.tmdb.org/t/p/$imageQuality${it.posterPath}",
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
        } while (page <= totalPage)

        emit(result)
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
        when (val response = apis.tmdbApis.discoverMovie(releaseDateGte = releaseDateGte, releaseDateLte = releaseDateLte)) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun availableLanguage(): Flow<List<TMDBLanguageItem>> = flow {
        when (val response = apis.tmdbApis.getAvailableLanguage()) {
            is ApiResponse.Failure -> throw response.throwable
            is ApiResponse.Success -> emit(response.data.asExternalModel())
        }
    }

    override fun availableRegion(): Flow<TMDBRegion> = flow {
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
}