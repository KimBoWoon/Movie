package com.bowoon.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InternalData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    userDataRepository: UserDataRepository,
    databaseRepository: DatabaseRepository,
    movieAppDataRepository: MovieAppDataRepository,
    private val detailRepository: DetailRepository,
    private val pagingRepository: PagingRepository
) {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        close(message = throwable.message ?: "something wrong...", throwable = throwable)
    }
    private val backgroundScope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)
    private val movieAppData = movieAppDataRepository.movieAppData
    private val seriesId = MutableStateFlow<Int?>(null)
    private val internalData = userDataRepository.internalData
        .stateIn(
            scope = backgroundScope,
            started = SharingStarted.Eagerly,
            initialValue = InternalData()
        )
    private val favoriteMovies = databaseRepository.getMovies()
        .stateIn(
            scope = backgroundScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    operator fun invoke(id: Int) = combine(
        detailRepository.getMovieDetail(id)
            .map { movieDetail ->
                movieDetail.copy(
                    autoPlayTrailer = internalData.value.autoPlayTrailer,
                    releaseDate = movieDetail.releases?.countries?.find {
                        it.iso31661.equals(internalData.value.region, true)
                    }?.releaseDate ?: movieDetail.releaseDate,
                    certification = movieDetail.releases?.countries?.find {
                        it.iso31661.equals(internalData.value.region, true)
                    }?.certification ?: movieDetail.certification,
                    isFavorite = favoriteMovies.value.find { it.id == movieDetail.id } != null,
                    backdropPath = "${movieAppData.value.getImageUrl()}${movieDetail.backdropPath}",
                    belongsToCollection = movieDetail.belongsToCollection?.copy(
                        backdropPath = "${movieAppData.value.getImageUrl()}${movieDetail.belongsToCollection?.backdropPath}",
                        posterPath = "${movieAppData.value.getImageUrl()}${movieDetail.belongsToCollection?.posterPath}"
                    ),
                    credits = movieDetail.credits?.copy(
                        cast = movieDetail.credits?.cast?.map { it.copy(profilePath = "${movieAppData.value.getImageUrl()}${it.profilePath}") },
                        crew = movieDetail.credits?.crew?.map { it.copy(profilePath = "${movieAppData.value.getImageUrl()}${it.profilePath}") }
                    ),
                    images = movieDetail.images?.copy(
                        backdrops = movieDetail.images?.backdrops?.map { it.copy(filePath = "${movieAppData.value.getImageUrl()}${it.filePath}") },
                        logos = movieDetail.images?.logos?.map { it.copy(filePath = "${movieAppData.value.getImageUrl()}${it.filePath}") },
                        posters = movieDetail.images?.posters?.map { it.copy(filePath = "${movieAppData.value.getImageUrl()}${it.filePath}") }
                    ),
                    posterPath = "${movieAppData.value.getImageUrl()}${movieDetail.posterPath}",
                    productionCompanies = movieDetail.productionCompanies?.map { it.copy(logoPath = "${movieAppData.value.getImageUrl()}${it.logoPath}") }
                )
            }
            .onEach {
                it.belongsToCollection?.id?.let { seriesId ->
                    this@GetMovieDetailUseCase.seriesId.emit(seriesId)
                }
            },
        flowOf(
            Pager(
                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                initialKey = 1,
                pagingSourceFactory = {
                    pagingRepository.getSimilarMoviePagingSource(
                        id = id,
                        language = "${internalData.value.language}-${internalData.value.region}"
                    )
                }
            ).flow.cachedIn(scope = backgroundScope).map { pagingData ->
                pagingData.map { movie ->
                    movie.copy(
                        backdropPath = "${movieAppData.value.getImageUrl()}${movie.backdropPath}",
                        posterPath = "${movieAppData.value.getImageUrl()}${movie.posterPath}",
                        imagePath = "${movieAppData.value.getImageUrl()}${movie.imagePath}"
                    )
                }
            },
        ),
        @OptIn(ExperimentalCoroutinesApi::class)
        seriesId.flatMapLatest { seriesId ->
            seriesId?.let {
                detailRepository.getMovieSeries(it).map { movieSeries ->
                    movieSeries.copy(
                        backdropPath = "${movieAppData.value.getImageUrl()}${movieSeries.backdropPath}",
                        posterPath = "${movieAppData.value.getImageUrl()}${movieSeries.posterPath}",
                        parts = movieSeries.parts?.map { part ->
                            part.copy(
                                backdropPath = "${movieAppData.value.getImageUrl()}${part.backdropPath}",
                                posterPath = "${movieAppData.value.getImageUrl()}${part.posterPath}"
                            )
                        }
                    )
                }
            } ?: flowOf(null)
        }
    ) { movieDetail, similarMovie, series ->
        Triple(movieDetail, series, similarMovie)
    }

    private fun close(message: String, throwable: Throwable?) {
        backgroundScope.cancel(message = message, cause = throwable)
    }
}