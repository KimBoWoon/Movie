package com.bowoon.domain

import com.bowoon.common.Log
import com.bowoon.common.toEpochDayOrMax
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Movie
import com.bowoon.model.Series
import com.bowoon.model.SeriesPart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val databaseRepository: DatabaseRepository,
    private val detailRepository: DetailRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: Int): Flow<MovieWithFavorite> {
        val base = combine(
            detailRepository.getMovie(id = id),
            userDataRepository.internalData,
            databaseRepository.isFavoriteMovie(id = id),
//        detailRepository.getMovieWatchProviders(movieId = id)
        ) { movie, internalData, isFavorite/*, watchProviders*/ ->
            val localizedRelease = movie.releases?.countries?.find { it.iso31661.equals(other = internalData.region, ignoreCase = true) }

            MovieWithFavorite(
                movie = movie.copy(
                    releaseDate = localizedRelease?.releaseDate ?: movie.releaseDate,
                    certification = localizedRelease?.certification ?: movie.certification
                ),
                isFavorite = isFavorite,
                autoPlayTrailer = internalData.isAutoPlayTrailer,
//            watchProviders = watchProviders.results?.get(key = internalData.region)
            )
        }
        val seriesFlow = base
            .map { it.movie.belongsToCollection?.id }
            .distinctUntilChanged()
            .flatMapLatest { seriesId ->
                if (seriesId == null) {
                    flowOf(value = null)
                } else {
                    detailRepository.getMovieSeries(collectionId = seriesId)
                        .map { series ->
                            series.copy(
                                parts = series.parts?.sortedWith(
                                    comparator = compareBy<SeriesPart> { it.releaseDate.toEpochDayOrMax() }
                                        .thenBy { it.title.orEmpty() }
                                )
                            )
                        }.catch { e ->
                            Log.printStackTrace(tr = e)
                            emit(value = Series())
                        }
                }
            }

        return combine(base, seriesFlow) { mwf, series ->
            if (series == null || series.id == null) {
                mwf
            } else {
                mwf.copy(movie = mwf.movie.copy(series = series))
            }
        }
    }
}

data class MovieWithFavorite(
    val movie: Movie,
    val isFavorite: Boolean,
    val autoPlayTrailer: Boolean,
//    val watchProviders: MovieWatchProviderResult?
)