package com.bowoon.testing.utils

import androidx.annotation.VisibleForTesting
import com.bowoon.data.util.ApplicationData
import com.bowoon.model.MovieAppData
import kotlinx.coroutines.flow.MutableStateFlow

class TestMovieAppDataManager : ApplicationData {
    override val movieAppData = MutableStateFlow(value = MovieAppData())

    @VisibleForTesting
    fun setMovieAppData(movieAppData: MovieAppData) {
        this@TestMovieAppDataManager.movieAppData.value = movieAppData
    }
}