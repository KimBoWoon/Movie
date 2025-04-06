package com.bowoon.series

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DetailRepository
import com.bowoon.model.MovieSeries
import com.bowoon.series.navigation.SeriesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SeriesVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    detailRepository: DetailRepository
) : ViewModel() {
    companion object {
        private const val TAG = "SeriesVM"
    }

    private val collectionId = savedStateHandle.toRoute<SeriesRoute>().id
    val series = detailRepository.getMovieSeries(collectionId)
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> SeriesState.Loading
                is Result.Success -> SeriesState.Success(it.data)
                is Result.Error -> SeriesState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SeriesState.Loading
        )
}

sealed interface SeriesState {
    data object Loading : SeriesState
    data class Success(val series: MovieSeries) : SeriesState
    data class Error(val throwable: Throwable) : SeriesState
}