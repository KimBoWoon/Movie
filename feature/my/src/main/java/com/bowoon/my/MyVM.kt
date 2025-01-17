package com.bowoon.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.domain.GetMyDataUseCase
import com.bowoon.model.MyData
import com.bowoon.model.PosterSize
import com.bowoon.model.tmdb.TMDBLanguageItem
import com.bowoon.model.tmdb.TMDBRegionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyVM @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val getMyDataUseCase: GetMyDataUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "MyVM"
    }

    val myData = getMyDataUseCase()
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
            started = SharingStarted.WhileSubscribed(5_000)
        )

    fun updateLanguage(language: TMDBLanguageItem) {
        viewModelScope.launch {
            language.iso6391?.let {
                userDataRepository.updateLanguage(it)
            }
        }
    }

    fun updateRegion(region: TMDBRegionResult) {
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
}

sealed interface MyDataState {
    data object Loading : MyDataState
    data class Success(val myData: MyData?) : MyDataState
    data class Error(val throwable: Throwable) : MyDataState
}