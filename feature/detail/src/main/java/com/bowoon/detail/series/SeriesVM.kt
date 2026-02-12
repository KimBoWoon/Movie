package com.bowoon.detail.series

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DetailRepository
import com.bowoon.model.Series
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    private val reload = MutableSharedFlow<Unit>(replay = 1)
    @OptIn(ExperimentalCoroutinesApi::class)
    val series = reload
        .flatMapLatest {
            trace("GetSeriesDetail") { detailRepository.getMovieSeries(collectionId = id).asResult() }
        }.map {
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

    init {
        viewModelScope.launch {
            reload.emit(value = Unit)
        }
    }

    fun restart() {
        viewModelScope.launch {
            reload.emit(value = Unit)
        }
    }
}

sealed interface SeriesState {
    data object Loading : SeriesState
    data class Success(val series: Series) : SeriesState
    data class Error(val throwable: Throwable) : SeriesState
}