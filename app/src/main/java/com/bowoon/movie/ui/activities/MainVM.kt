package com.bowoon.movie.ui.activities

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(

) : ViewModel() {
    val initDataLoad = MutableStateFlow<InitDataState>(InitDataState.Load)
}

sealed interface InitDataState {
    data object Load : InitDataState
    data object Success : InitDataState
    data object Error : InitDataState
}