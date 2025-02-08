package com.bowoon.movie.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.MyData
import com.bowoon.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    userDataRepository: UserDataRepository,
    myDataRepository: MyDataRepository
) : ViewModel() {
    val userdata = userDataRepository.userData
        .map { UserdataState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserdataState.Loading
        )
    val myData = myDataRepository.myData
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> MyDataState.Loading
                is Result.Success -> MyDataState.Success(it.data)
                is Result.Error -> MyDataState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MyDataState.Loading
        )
}

sealed interface UserdataState {
    data object Loading : UserdataState
    data class Success(val data: UserData) : UserdataState {
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (data.isDarkMode) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }
    }
    data class Error(val throwable: Throwable) : UserdataState

    fun shouldKeepSplashScreen() = this is Loading
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean = isSystemDarkTheme
}

sealed interface MyDataState {
    data object Loading : MyDataState
    data class Success(val myData: MyData) : MyDataState
    data class Error(val throwable: Throwable) : MyDataState
}