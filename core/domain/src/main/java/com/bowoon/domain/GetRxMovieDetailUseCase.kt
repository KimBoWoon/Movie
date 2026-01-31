package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.RxDetailRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InternalData
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetailInfo
import com.bowoon.model.Series
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class GetRxMovieDetailUseCase @Inject constructor(
    userDataRepository: UserDataRepository,
    private val databaseRepository: DatabaseRepository,
    private val detailRepository: RxDetailRepository
) {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        close(message = throwable.message ?: "something wrong...", cause = throwable)
    }
    private val backgroundScope = CoroutineScope(context = Dispatchers.IO + coroutineExceptionHandler)
//    private val seriesId = MutableStateFlow<Int?>(value = null)
    private val internalData = userDataRepository.internalData
        .stateIn(
            scope = backgroundScope,
            started = SharingStarted.Eagerly,
            initialValue = InternalData()
        )
    private val seriesIdSubject = BehaviorSubject.createDefault(-1)

    operator fun invoke(id: Int): Observable<MovieDetailInfo> {
        val movieObservable = detailRepository.getMovie(id) // Observable<Movie>
            .map { movie ->
                val region = internalData.value.region
                val countryRelease = movie.releases?.countries?.find { it.iso31661.equals(region, ignoreCase = true) }

                movie.copy(
                    releaseDate = countryRelease?.releaseDate ?: movie.releaseDate,
                    certification = countryRelease?.certification ?: movie.certification
                )
            }.toObservable().doOnNext { movie ->
                movie.belongsToCollection?.id?.let { seriesId ->
                    seriesIdSubject.onNext(seriesId)
                }
            }
        val seriesObservable = seriesIdSubject
                .switchMap<Series> { seriesId ->
                    if (seriesId != -1) {
                        detailRepository.getMovieSeries(seriesId).toObservable() // Observable<Series>
                    } else {
                        Observable.just(Series())
                    }
                }
        val favoriteMoviesObservable = Observable.just(emptyList<Movie>()) // Observable<List<Movie>>

        return Observable.combineLatest(
            movieObservable,
            seriesObservable,
            favoriteMoviesObservable
        ) { movie, series, favoriteMovies ->
            MovieDetailInfo(
                detail = movie.copy(isFavorite = favoriteMovies.any { it.id == movie.id }),
                series = series,
                autoPlayTrailer = internalData.value.isAutoPlayTrailer
            )
        }
    }

    fun close(message: String, cause: Throwable?) {
        backgroundScope.cancel(message = message, cause = cause)
    }
}