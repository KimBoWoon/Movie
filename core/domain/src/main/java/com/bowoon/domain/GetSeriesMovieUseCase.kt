package com.bowoon.domain

import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.model.MovieSeries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSeriesMovieUseCase @Inject constructor(
    private val detailRepository: DetailRepository,
    private val movieAppDataRepository: MovieAppDataRepository
) {
    private val movieAppData = movieAppDataRepository.movieAppData

    operator fun invoke(collectionId: Int): Flow<MovieSeries> = detailRepository.getMovieSeries(collectionId)
        .map { movieSeries ->
            movieSeries.copy(
                backdropPath = "${movieAppData.value.getImageUrl()}${movieSeries.backdropPath}",
                parts = movieSeries.parts?.map { part ->
                    part.copy(
                        backdropPath = "${movieAppData.value.getImageUrl()}${part.backdropPath}",
                        posterPath = "${movieAppData.value.getImageUrl()}${part.posterPath}"
                    )
                },
                posterPath ="${movieAppData.value.getImageUrl()}${movieSeries.posterPath}"
            )
        }
}