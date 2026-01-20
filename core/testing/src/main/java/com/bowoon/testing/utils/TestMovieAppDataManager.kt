package com.bowoon.testing.utils

import androidx.annotation.VisibleForTesting
import com.bowoon.data.util.DataManager
import com.bowoon.data.util.MovieAppDataState
import com.bowoon.model.MovieAppData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestMovieAppDataManager : DataManager {
    private val _movieAppData = MutableStateFlow<MovieAppDataState>(value = MovieAppDataState.Loading)
    override val movieAppData: StateFlow<MovieAppDataState> = _movieAppData.asStateFlow()

    @VisibleForTesting
    fun setMovieAppData(movieAppData: MovieAppData) {
        _movieAppData.value = MovieAppDataState.Success(data = movieAppData)
    }

    @VisibleForTesting
    fun setError(throwable: Throwable) {
        _movieAppData.value = MovieAppDataState.Error(throwable = throwable)
    }
}