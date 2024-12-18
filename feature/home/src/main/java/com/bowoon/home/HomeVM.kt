package com.bowoon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.KobisRepository
import com.bowoon.model.BoxOffice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val kobisRepository: KobisRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    val boxOfficeState: StateFlow<BoxOfficeState> =
        getDailyBoxOffice(kobisRepository, BuildConfig.KOBIS_OPEN_API_KEY, "20230517")
            .stateIn(
                scope = viewModelScope,
                initialValue = BoxOfficeState.Loading,
                started = SharingStarted.WhileSubscribed(5000)
            )
}

sealed interface BoxOfficeState {
    data object Loading : BoxOfficeState
    data class Success(val boxOffice: BoxOffice) : BoxOfficeState
    data class Error(val throwable: Throwable) : BoxOfficeState
}

fun getDailyBoxOffice(
    kobisRepository: KobisRepository,
    key: String,
    targetDt: String
): Flow<BoxOfficeState> =
    kobisRepository.getDailyBoxOffice(key, targetDt)
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> BoxOfficeState.Loading
                is Result.Success -> BoxOfficeState.Success(it.data)
                is Result.Error -> BoxOfficeState.Error(it.throwable)
            }
        }