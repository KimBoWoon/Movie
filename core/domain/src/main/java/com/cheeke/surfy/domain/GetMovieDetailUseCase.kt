package com.cheeke.surfy.domain

import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.toEpochDayOrMax
import com.cheeke.surfy.data.repository.MovieDetailRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.SeriesPart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val detailRepository: MovieDetailRepository
) {
    private suspend fun getCollection(collectionId: Int): Series =
        detailRepository.getMovieSeries(collectionId = collectionId)
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
            }.first()

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: Int): Flow<Movie> =
        combine(
            detailRepository.getData(id = id),
            userDataRepository.internalData,
        ) { movie, internalData ->
            val country = movie.releases?.countries?.filter { country ->
                country.iso31661.equals(other = internalData.region, ignoreCase = true)
            }?.maxByOrNull { LocalDate.parse(it.releaseDate) }

            movie.copy(
                releaseDate = country?.releaseDate ?: movie.releaseDate,
                certification = country?.certification ?: movie.certification
            )
        }.map { movie ->
            movie.belongsToCollection?.id?.let { collectionId ->
                movie.copy(series = getCollection(collectionId = collectionId))
            } ?: movie
        }
}