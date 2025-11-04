package com.bowoon.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InternalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyVM @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val movieAppDataRepository: MovieAppDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "MyVM"
    }

    val myData = userDataRepository.internalData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = InternalData()
        )
    val movieAppData = movieAppDataRepository.movieAppData

    fun updateUserData(userData: InternalData, isSync: Boolean) {
        viewModelScope.launch {
            userDataRepository.updateUserData(userData, isSync)
        }
    }
}

enum class Menu {
    MAIN_UPDATE_DATE,
    DARK_MODE_SETTING,
    IS_ADULT,
    IS_AUTO_PLAYING_TRAILER,
    LANGUAGE,
    IMAGE_QUALITY,
    VERSION
}