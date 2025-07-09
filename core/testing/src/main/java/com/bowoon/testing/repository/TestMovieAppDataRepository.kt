package com.bowoon.testing.repository

import androidx.annotation.VisibleForTesting
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.model.MovieAppData
import kotlinx.coroutines.flow.MutableStateFlow

class TestMovieAppDataRepository : MovieAppDataRepository {
    override val movieAppData = MutableStateFlow<MovieAppData>(MovieAppData())

    @VisibleForTesting
    fun setMovieAppData(movieAppData: MovieAppData) {
        this@TestMovieAppDataRepository.movieAppData.tryEmit(movieAppData)
    }
}