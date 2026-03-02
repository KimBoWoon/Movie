package com.bowoon.detail.series

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.analytics.AnalyticsHelper
import com.bowoon.analytics.logSelectContent
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.toEpochDayOrMax
import com.bowoon.data.repository.DetailRepository
import com.bowoon.model.Series
import com.bowoon.model.SeriesPart
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
    detailRepository: DetailRepository,
    private val analyticsHelper: AnalyticsHelper
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
            trace(sectionName = "GetSeriesDetail") {
                detailRepository.getMovieSeries(collectionId = id)
                    .map { series ->
                        series.copy(
                            parts = series.parts?.sortedWith(
                                comparator = compareBy<SeriesPart> { it.releaseDate.toEpochDayOrMax() }
                                    .thenBy { it.title.orEmpty() }
                            )
                        )
                    }
            }.asResult()
        }.map { result ->
            when (result) {
                is Result.Loading -> SeriesState.Loading
                is Result.Success -> {
                    analyticsHelper.logSelectContent(contentType = "series", media = result.data)
                    SeriesState.Success(series = result.data)
                }
                is Result.Error -> SeriesState.Error(throwable = result.throwable)
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