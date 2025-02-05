package com.bowoon.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.LanguageItem
import com.bowoon.model.MyData
import com.bowoon.model.PosterSize
import com.bowoon.model.Region
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyVM @Inject constructor(
    private val userDataRepository: UserDataRepository,
    myDataRepository: MyDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "MyVM"
    }

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
            initialValue = MyDataState.Loading,
            started = SharingStarted.Eagerly
        )

    fun updateDarkTheme(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userDataRepository.updateDarkModeTheme(darkThemeConfig)
        }
    }

    fun updateLanguage(language: LanguageItem) {
        viewModelScope.launch {
            language.iso6391?.let {
                userDataRepository.updateLanguage(it)
            }
        }
    }

    fun updateRegion(region: Region) {
        viewModelScope.launch {
            region.iso31661?.let {
                userDataRepository.updateRegion(it)
            }
        }
    }

    fun updateImageQuality(posterSize: PosterSize) {
        viewModelScope.launch {
            posterSize.size?.let {
                userDataRepository.updateImageQuality(it)
            }
        }
    }

    fun updateIsAdult(isAdult: Boolean) {
        viewModelScope.launch {
            userDataRepository.updateIsAdult(isAdult)
        }
    }

    fun updateIsAutoPlayTrailer(isAutoPlayTrailer: Boolean) {
        viewModelScope.launch {
            userDataRepository.updateIsAutoPlayTrailer(isAutoPlayTrailer)
        }
    }
}

sealed interface MyDataState {
    data object Loading : MyDataState
    data class Success(val myData: MyData?) : MyDataState
    data class Error(val throwable: Throwable) : MyDataState
}