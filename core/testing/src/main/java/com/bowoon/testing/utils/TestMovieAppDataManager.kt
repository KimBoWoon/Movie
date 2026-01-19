package com.bowoon.testing.utils

import androidx.annotation.VisibleForTesting
import com.bowoon.data.util.DataManager
import com.bowoon.data.util.MovieAppDataState
import com.bowoon.model.MovieAppData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestMovieAppDataManager : DataManager {
//    override val movieAppData = MutableStateFlow(value = MovieAppData())
//
//    @VisibleForTesting
//    fun setMovieAppData(movieAppData: MovieAppData) {
//        this@TestMovieAppDataManager.movieAppData.value = movieAppData
//    }

    // 내부에서 값을 변경할 수 있는 MutableStateFlow
    private val _movieAppData = MutableStateFlow<MovieAppDataState>(value = MovieAppDataState.Loading)

    // 외부(인터페이스)에 노출되는 읽기 전용 StateFlow
    override val movieAppData: StateFlow<MovieAppDataState> = _movieAppData.asStateFlow()

    @VisibleForTesting
    fun setMovieAppData(movieAppData: MovieAppData) {
        // 내부의 Mutable 변수를 통해 값을 업데이트
        _movieAppData.value = MovieAppDataState.Success(data = movieAppData)
    }

    // 에러 상태도 테스트하고 싶다면 추가할 수 있습니다.
    @VisibleForTesting
    fun setError(throwable: Throwable) {
        _movieAppData.value = MovieAppDataState.Error(throwable = throwable)
    }
}