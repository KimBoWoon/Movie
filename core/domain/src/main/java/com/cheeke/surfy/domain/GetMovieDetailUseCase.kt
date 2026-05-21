package com.cheeke.surfy.domain

import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.toEpochDayOrMax
import com.cheeke.surfy.data.repository.MovieDetailRepository
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.SeriesPart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val detailRepository: MovieDetailRepository
) {
    private fun getCollection(collectionId: Int): Flow<Series> {
        return detailRepository.getMovieSeries(collectionId = collectionId)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: Int): Flow<Movie> =
        detailRepository.getData(id = id)
            .map { movie ->
                movie.belongsToCollection?.id?.let { collectionId ->
                    movie.copy(series = getCollection(collectionId = collectionId).firstOrNull())
                } ?: movie
            }
}