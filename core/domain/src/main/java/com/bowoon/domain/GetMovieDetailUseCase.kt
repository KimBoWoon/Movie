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
import com.bowoon.model.MovieInfo
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
        close(message = throwable.message ?: "something wrong...", cause = throwable)
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
        detailRepository.getMovie(id)
            .map { movie ->
                movie.copy(
                    releaseDate = movie.releases?.countries?.find {
                        it.iso31661.equals(internalData.value.region, true)
                    }?.releaseDate ?: movie.releaseDate,
                    certification = movie.releases?.countries?.find {
                        it.iso31661.equals(internalData.value.region, true)
                    }?.certification ?: movie.certification,
                    isFavorite = favoriteMovies.value.find { it.id == movie.id } != null,
                    backdropPath = "${movieAppData.value.getImageUrl()}${movie.backdropPath}",
                    belongsToCollection = movie.belongsToCollection?.copy(
                        backdropPath = "${movieAppData.value.getImageUrl()}${movie.belongsToCollection?.backdropPath}",
                        posterPath = "${movieAppData.value.getImageUrl()}${movie.belongsToCollection?.posterPath}"
                    ),
                    credits = movie.credits?.copy(
                        cast = movie.credits?.cast?.map { it.copy(profilePath = "${movieAppData.value.getImageUrl()}${it.profilePath}") },
                        crew = movie.credits?.crew?.map { it.copy(profilePath = "${movieAppData.value.getImageUrl()}${it.profilePath}") }
                    ),
                    images = movie.images?.copy(
                        backdrops = movie.images?.backdrops?.map { it.copy(filePath = "${movieAppData.value.getImageUrl()}${it.filePath}") },
                        logos = movie.images?.logos?.map { it.copy(filePath = "${movieAppData.value.getImageUrl()}${it.filePath}") },
                        posters = movie.images?.posters?.map { it.copy(filePath = "${movieAppData.value.getImageUrl()}${it.filePath}") }
                    ),
                    posterPath = "${movieAppData.value.getImageUrl()}${movie.posterPath}",
                    productionCompanies = movie.productionCompanies?.map { it.copy(logoPath = "${movieAppData.value.getImageUrl()}${it.logoPath}") }
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
                    movie.copy(imagePath = "${movieAppData.value.getImageUrl()}${movie.imagePath}")
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
    ) { movie, similarMovie, series ->
        MovieInfo(detail = movie, series = series, similarMovies = similarMovie)
    }

    fun close(message: String, cause: Throwable?) {
        backgroundScope.cancel(message = message, cause = cause)
    }
}