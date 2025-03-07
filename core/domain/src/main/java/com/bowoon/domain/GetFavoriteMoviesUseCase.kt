package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFavoriteMoviesUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<List<MovieDetail>> = combine(
        databaseRepository.getMovies(),
        userDataRepository.internalData
    ) { favoriteMovies, userData ->
        favoriteMovies.map { movie ->
            movie.copy(
                releaseDate = movie.releases?.countries?.find {
                    it.iso31661.equals(userData.region, true)
                }?.releaseDate,
                certification = movie.releases?.countries?.find {
                    it.iso31661.equals(userData.region, true)
                }?.certification
            )
        }
    }
}