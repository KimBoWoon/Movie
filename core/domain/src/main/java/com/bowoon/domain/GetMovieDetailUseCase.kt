package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val detailRepository: DetailRepository,
    private val userDataRepository: UserDataRepository,
    private val databaseRepository: DatabaseRepository
) {
    operator fun invoke(id: Int): Flow<MovieDetail> =
        combine(
            detailRepository.getMovieDetail(id),
            userDataRepository.internalData,
            databaseRepository.getMovies(),
        ) { movie, userData, favoriteMovies ->
            movie.copy(
                autoPlayTrailer = userData.autoPlayTrailer,
                releaseDate = movie.releases?.countries?.find {
                    it.iso31661.equals(userData.region, true)
                }?.releaseDate ?: movie.releaseDate,
                certification = movie.releases?.countries?.find {
                    it.iso31661.equals(userData.region, true)
                }?.certification ?: movie.certification,
                isFavorite = favoriteMovies.find { it.id == movie.id } != null
            )
        }
}