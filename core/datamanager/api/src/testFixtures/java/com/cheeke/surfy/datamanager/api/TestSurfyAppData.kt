package com.cheeke.surfy.datamanager.api

import androidx.annotation.VisibleForTesting
import com.cheeke.surfy.model.SurfyAppData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestSurfyAppData : DataManager {
    private val _movieAppData = MutableStateFlow<SurfyAppDataState>(value = SurfyAppDataState.Loading)
    override val surfyAppData: StateFlow<SurfyAppDataState> = _movieAppData.asStateFlow()
    private val _localeFlow = MutableStateFlow<Locale>(value = Locale(language = "en", region = "US"))
    override val localeFlow: StateFlow<Locale> = _localeFlow.asStateFlow()

    @VisibleForTesting
    fun setMovieAppData(surfyAppData: SurfyAppData) {
        _movieAppData.value = SurfyAppDataState.Success(data = surfyAppData)
    }

    @VisibleForTesting
    fun setError(throwable: Throwable) {
        _movieAppData.value = SurfyAppDataState.Error(throwable = throwable)
    }
}