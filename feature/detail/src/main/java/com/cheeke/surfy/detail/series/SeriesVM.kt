package com.cheeke.surfy.detail.series

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSelectContent
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.domain.GetSeriesDetailUseCase
import com.cheeke.surfy.domain.SeriesWithImages
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.network.model.SurfyNetworkException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.processors.BehaviorProcessor
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = SeriesVM.Factory::class)
class SeriesVM @AssistedInject constructor(
    @Assisted val id: Int,
    private val getSeriesDetailUseCase: GetSeriesDetailUseCase,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {
    companion object {
        internal const val TAG = "SeriesVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): SeriesVM
    }

    private val reload = BehaviorProcessor.createDefault<Unit>(Unit)
    private val detail = reload
        .switchMap {
            trace("GetSeriesDetail") {
                getSeriesDetailUseCase(id)
                    .map<Result<SeriesWithImages>> { Result.Success(it) }
                    .startWithItem(Result.Loading)
                    .onErrorReturn { Result.Error(it) }
            }
        }
    val series = detail.map { result ->
        when (result) {
            Result.Loading -> SeriesState.Loading
            is Result.Success -> {
                analyticsHelper.logSelectContent(contentType = "series", media = result.data.series)
                SeriesState.Success(series = result.data.series, imageList = result.data.imageList)
            }
            is Result.Error -> SeriesState.Error(result.throwable as SurfyNetworkException)
        }
    }.replay(1)
        .refCount()

    init {
        viewModelScope.launch {
            reload.onNext(Unit)
        }
    }

    fun restart() {
        viewModelScope.launch {
            reload.onNext(Unit)
        }
    }
}

sealed interface SeriesState {
    data object Loading : SeriesState
    data class Success(val series: Series, val imageList: ImageList) : SeriesState
    data class Error(val throwable: SurfyNetworkException) : SeriesState
}