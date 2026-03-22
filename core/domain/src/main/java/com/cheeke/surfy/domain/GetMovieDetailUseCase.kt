package com.cheeke.surfy.domain

import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.toEpochDayOrMax
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.data.repository.MovieDetailRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.SeriesPart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val databaseRepository: DatabaseRepository,
    private val detailRepository: MovieDetailRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: Int): Flow<MovieWithFavorite> {
        val base = combine(
            detailRepository.getData(id = id),
            userDataRepository.internalData,
            databaseRepository.isFavoriteMovie(id = id),
//        detailRepository.getMovieWatchProviders(movieId = id)
        ) { movie, internalData, isFavorite/*, watchProviders*/ ->
            val country = movie.releases?.countries?.filter { country ->
                country.iso31661.equals(other = internalData.region, ignoreCase = true)
            }?.maxByOrNull { LocalDate.parse(it.releaseDate) }

            MovieWithFavorite(
                movie = movie.copy(
                    releaseDate = country?.releaseDate ?: movie.releaseDate,
                    certification = country?.certification ?: movie.certification
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