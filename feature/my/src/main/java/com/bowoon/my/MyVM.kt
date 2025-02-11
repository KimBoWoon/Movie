package com.bowoon.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.TMDBConfiguration
import com.bowoon.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
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

    val myData = combine(
        myDataRepository.myData,
        userDataRepository.userData
    ) { myData, userData ->
        myData to userData
    }.asResult()
        .map {
            when (it) {
                is Result.Loading -> MyDataState.Loading
                is Result.Success -> MyDataState.Success(it.data.first, it.data.second)
                is Result.Error -> MyDataState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = MyDataState.Loading,
            started = SharingStarted.Eagerly
        )

    fun updateUserData(userData: UserData, isSync: Boolean) {
        viewModelScope.launch {
            userDataRepository.updateUserData(userData, isSync)
        }
    }
}

sealed interface MyDataState {
    data object Loading : MyDataState
    data class Success(val myData: TMDBConfiguration, val userData: UserData) : MyDataState
    data class Error(val throwable: Throwable) : MyDataState
}