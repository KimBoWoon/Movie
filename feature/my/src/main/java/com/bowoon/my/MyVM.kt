package com.bowoon.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.model.TMDBConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MyVM @Inject constructor(
    private val tmdbRepository: TMDBRepository
) : ViewModel() {
    companion object {
        private const val TAG = "MyVM"
    }

    val configuration = tmdbRepository.getConfiguration()
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> ConfigurationState.Loading
                is Result.Success -> ConfigurationState.Success(it.data)
                is Result.Error -> ConfigurationState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = ConfigurationState.Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )
}

sealed interface ConfigurationState {
    data object Loading : ConfigurationState
    data class Success(val configuration: TMDBConfiguration) : ConfigurationState
    data class Error(val throwable: Throwable) : ConfigurationState
}