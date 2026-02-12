package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Movie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
    operator fun invoke(id: Int): Flow<MovieWithFavorite> = combine(
        detailRepository.getMovie(id = id),
        userDataRepository.internalData,
        databaseRepository.isFavoriteMovie(id = id)
    ) { movie, internalData, isFavorite ->
        val localizedRelease = movie.releases?.countries?.find { it.iso31661.equals(other = internalData.region, ignoreCase = true) }

        MovieWithFavorite(
            movie = movie.copy(
                releaseDate = localizedRelease?.releaseDate ?: movie.releaseDate,
                certification = localizedRelease?.certification ?: movie.certification
            ),
            isFavorite = isFavorite,
            autoPlayTrailer = internalData.isAutoPlayTrailer
        )
    }.flatMapLatest { movieWithFavorite ->
        movieWithFavorite.movie.belongsToCollection?.id?.let { seriesId ->
            detailRepository.getMovieSeries(collectionId = seriesId)
                .map { series -> movieWithFavorite.copy(movie = movieWithFavorite.movie.copy(series = series)) }
                .catch { emit(value = movieWithFavorite) }
        } ?: flowOf(value = movieWithFavorite)
    }
}

data class MovieWithFavorite(
    val movie: Movie,
    val isFavorite: Boolean,
    val autoPlayTrailer: Boolean
)