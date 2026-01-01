package com.bowoon.detail.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.restartableStateIn
import com.bowoon.data.repository.DetailRepository
import com.bowoon.model.Series
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map

@HiltViewModel(assistedFactory = SeriesVM.Factory::class)
class SeriesVM @AssistedInject constructor(
    @Assisted val id: Int,
    detailRepository: DetailRepository
) : ViewModel() {
    companion object {
        internal const val TAG = "SeriesVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): SeriesVM
    }

    val series = detailRepository.getMovieSeries(collectionId = id)
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> SeriesState.Loading
                is Result.Success -> SeriesState.Success(it.data)
                is Result.Error -> SeriesState.Error(it.throwable)
            }
        }.restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SeriesState.Loading
        )

    fun restart() {
        series.restart()
    }
}

sealed interface SeriesState {
    data object Loading : SeriesState
    data class Success(val series: Series) : SeriesState
    data class Error(val throwable: Throwable) : SeriesState
}