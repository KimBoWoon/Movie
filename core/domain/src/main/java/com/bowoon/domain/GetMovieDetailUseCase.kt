package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InternalData
import com.bowoon.model.MovieDetailInfo
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
    private val databaseRepository: DatabaseRepository,
    private val detailRepository: DetailRepository
) {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        close(message = throwable.message ?: "something wrong...", cause = throwable)
    }
    private val backgroundScope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)
    private val seriesId = MutableStateFlow<Int?>(null)
    private val internalData = userDataRepository.internalData
        .stateIn(
            scope = backgroundScope,
            started = SharingStarted.Eagerly,
            initialValue = InternalData()
        )

    operator fun invoke(id: Int) = combine(
        detailRepository.getMovie(id)
            .map { movie ->
                movie.copy(
                    releaseDate = movie.releases?.countries?.find { country ->
                        country.iso31661.equals(other = internalData.value.region, ignoreCase = true)
                    }?.releaseDate ?: movie.releaseDate,
                    certification = movie.releases?.countries?.find { country ->
                        country.iso31661.equals(other = internalData.value.region, ignoreCase = true)
                    }?.certification ?: movie.certification
                )
            }.onEach { movie ->
                movie.belongsToCollection?.id?.let { seriesId ->
                    this@GetMovieDetailUseCase.seriesId.emit(value = seriesId)
                }
            },
        @OptIn(ExperimentalCoroutinesApi::class)
        seriesId.flatMapLatest { seriesId ->
            seriesId?.let {
                detailRepository.getMovieSeries(collectionId = it)
            } ?: flowOf(null)
        },
        databaseRepository.getMovies()
    ) { movie, series, favoriteMovies ->
        MovieDetailInfo(
            detail = movie.copy(isFavorite = favoriteMovies.find { it.id == movie.id } != null),
            series = series,
            autoPlayTrailer = internalData.value.autoPlayTrailer
        )
    }

    fun close(message: String, cause: Throwable?) {
        backgroundScope.cancel(message = message, cause = cause)
    }
}