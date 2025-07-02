package com.bowoon.domain

import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MainMenu
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMainMenuUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val movieAppDataRepository: MovieAppDataRepository
) {
    operator fun invoke(): Flow<MainMenu> = combine(
        movieAppDataRepository.movieAppData,
        userDataRepository.internalData
    ) { movieAppData, internalData ->
        internalData.mainMenu.copy(
            nowPlaying = internalData.mainMenu.nowPlaying.map { movie ->
                movie.copy(imagePath = "${movieAppData.getImageUrl()}${movie.imagePath}")
            },
            upComingMovies = internalData.mainMenu.upComingMovies.map { movie ->
                movie.copy(imagePath = "${movieAppData.getImageUrl()}${movie.imagePath}")
            }
        )
    }
}