package com.cheeke.surfy.detail.series

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.util.trace
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSelectContent
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.common.di.ActivityRetainedScopeCoroutine
import com.cheeke.surfy.detail.series.navigation.SeriesScreen
import com.cheeke.surfy.domain.GetSeriesDetailUseCase
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Series
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class SeriesRepository @Inject constructor(
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope,
    private val getSeriesDetailUseCase: GetSeriesDetailUseCase,
    private val analyticsHelper: AnalyticsHelper
) {
    companion object {
        private const val TAG = "SeriesRepository"
    }

    private val reload = MutableSharedFlow<Unit>(replay = 1)

    init {
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSeries(id: Int): Flow<SeriesState> {
        return reload.flatMapLatest {
            trace(sectionName = "GetSeriesDetail") { getSeriesDetailUseCase(id = id) }.asResult()
        }.map { result ->
            when (result) {
                is Result.Loading -> SeriesState.Loading
                is Result.Success -> {
                    analyticsHelper.logSelectContent(contentType = "series", media = result.data.series)
                    SeriesState.Success(series = result.data.series, imageList = result.data.imageList)
                }
                is Result.Error -> SeriesState.Error(throwable = result.throwable)
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = SeriesState.Loading
        )
    }

    fun restart() {
        scope.launch {
            reload.emit(value = Unit)
        }
    }
}

class SeriesPresenter @AssistedInject constructor(
    @Assisted(value = "navigator") private val navigator: Navigator,
    @Assisted(value = "screen") private val screen: SeriesScreen,
    @Assisted(value = "goToMovie") private val goToMovie: (Int) -> Unit,
    private val seriesRepository: SeriesRepository
) : Presenter<SeriesUiState> {
    @Composable
    override fun present(): SeriesUiState {
        val series by produceRetainedState<SeriesState>(initialValue = SeriesState.Loading) {
            seriesRepository.getSeries(id = screen.id).collect { seriesState ->
                value = seriesState
            }
        }

        return SeriesUiState(
            series = series,
        ) { event ->
            Log.d("HomePresenter", "$event")
            when (event) {
                is SeriesEvent.GoToMovie -> goToMovie(event.id)
                is SeriesEvent.Restart -> seriesRepository.restart()
                is SeriesEvent.GoToBack -> navigator.pop()
            }
        }
    }

    @CircuitInject(screen = SeriesScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(value = "navigator") navigator: Navigator,
            @Assisted(value = "screen") screen: SeriesScreen,
            @Assisted(value = "goToMovie") goToMovie: ((Int) -> Unit) = {}
        ): SeriesPresenter
    }
}

data class SeriesUiState(
    val series: SeriesState,
    val eventSink: (SeriesEvent) -> Unit
) : CircuitUiState

sealed interface SeriesEvent {
    object GoToBack : SeriesEvent
    object Restart : SeriesEvent
    data class GoToMovie(val id: Int) : SeriesEvent
}

sealed interface SeriesState {
    data object Loading : SeriesState
    data class Success(val series: Series, val imageList: ImageList) : SeriesState
    data class Error(val throwable: Throwable) : SeriesState
}