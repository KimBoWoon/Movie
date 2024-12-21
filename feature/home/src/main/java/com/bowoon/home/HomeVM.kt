package com.bowoon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.domain.GetDailyBoxOfficeUseCase
import com.bowoon.model.DailyBoxOffice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val getBoxOfficeUseCase: GetDailyBoxOfficeUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    val boxOfficeState: StateFlow<BoxOfficeState> =
        getBoxOfficeUseCase(
            kobisOpenApiKey = BuildConfig.KOBIS_OPEN_API_KEY,
            targetDt = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            kmdbOpenApiKey = BuildConfig.KMDB_OPEN_API_KEY
        ).asResult()
            .map {
                when (it) {
                    is Result.Loading -> BoxOfficeState.Loading
                    is Result.Success -> BoxOfficeState.Success(it.data)
                    is Result.Error -> BoxOfficeState.Error(it.throwable)
                }
            }
            .stateIn(
            scope = viewModelScope,
            initialValue = BoxOfficeState.Loading,
//            started = SharingStarted.WhileSubscribed(5000)
            started = SharingStarted.Eagerly
        )
}

sealed interface BoxOfficeState {
    data object Loading : BoxOfficeState
    data class Success(val boxOffice: List<DailyBoxOffice>) : BoxOfficeState
    data class Error(val throwable: Throwable) : BoxOfficeState
}