package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.model.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoritePeopleListUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    movieAppDataRepository: MovieAppDataRepository
) {
    private val movieAppData = movieAppDataRepository.movieAppData

    operator fun invoke(): Flow<List<Favorite>> = databaseRepository.getPeople()
        .map { favoriteList ->
            favoriteList.map {
                it.copy(imagePath = "${movieAppData.value.getImageUrl()}${it.imagePath}")
            }
        }
}