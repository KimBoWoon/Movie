package com.bowoon.series

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DetailRepository
import com.bowoon.model.MovieSeries
import com.bowoon.series.navigation.Series
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.flow.map

class SeriesPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: Series,
    @Assisted private val goToMovie: (Int) -> Unit,
    private val detailRepository: DetailRepository
) : Presenter<SeriesUiState> {
    @Composable
    override fun present(): SeriesUiState {
        val series by produceRetainedState<SeriesState>(initialValue = SeriesState.Loading) {
            detailRepository.getMovieSeries(collectionId = screen.id)
                .asResult()
                .map {
                    when (it) {
                        is Result.Loading -> SeriesState.Loading
                        is Result.Success -> SeriesState.Success(it.data)
                        is Result.Error -> SeriesState.Error(it.throwable)
                    }
                }.collect { value = it }
        }

        return SeriesUiState(
            series = series
        ) { event ->
            when (event) {
                is SeriesEvent.GoToBack -> navigator.pop()
                is SeriesEvent.GoToMovie -> goToMovie(event.id)
            }
        }
    }

    @CircuitInject(Series::class, ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            navigator: Navigator,
            screen: Series,
            goToMovie: ((Int) -> Unit) = {}
        ): SeriesPresenter
    }
}

data class SeriesUiState(
    val series: SeriesState,
    val eventSink: (SeriesEvent) -> Unit
) : CircuitUiState

sealed interface SeriesEvent : CircuitUiEvent {
    data object GoToBack : SeriesEvent
    data class GoToMovie(val id: Int) : SeriesEvent
}

sealed interface SeriesState {
    data object Loading : SeriesState
    data class Success(val series: MovieSeries) : SeriesState
    data class Error(val throwable: Throwable) : SeriesState
}