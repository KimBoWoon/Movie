package com.bowoon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.domain.GetMainMenuUseCase
import com.bowoon.model.MainMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val getMainUseCase: GetMainMenuUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    val mainMenu = getMainUseCase(
        targetDt = LocalDate.now(),
        kobisOpenApiKey = BuildConfig.KOBIS_OPEN_API_KEY
    ).asResult()
        .map {
            when (it) {
                is Result.Loading -> HomeUiState.Loading
                is Result.Success -> HomeUiState.Success(it.data)
                is Result.Error -> HomeUiState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = HomeUiState.Loading,
            started = SharingStarted.Eagerly
        )
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val mainMenu: MainMenu) : HomeUiState
    data class Error(val throwable: Throwable) : HomeUiState
}