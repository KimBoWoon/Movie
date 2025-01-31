package com.bowoon.movie.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {
    val initDataLoad = userDataRepository.userData
        .map { InitDataState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InitDataState.Loading
        )
}

sealed interface InitDataState {
    data object Loading : InitDataState
    data class Success(val data: UserData) : InitDataState
    data class Error(val throwable: Throwable) : InitDataState

    fun shouldKeepSplashScreen() = this is Loading
}