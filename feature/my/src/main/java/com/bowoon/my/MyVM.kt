package com.bowoon.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.domain.GetInitDataUseCase
import com.bowoon.model.InitData
import com.bowoon.model.InternalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyVM @Inject constructor(
    getInitDataUseCase: GetInitDataUseCase,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "MyVM"
    }

    val myData = getInitDataUseCase()
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

    fun updateUserData(userData: InternalData, isSync: Boolean) {
        viewModelScope.launch {
            userDataRepository.updateUserData(userData, isSync)
        }
    }
}

sealed interface MyDataState {
    data object Loading : MyDataState
    data class Success(val initData: InitData) : MyDataState
    data class Error(val throwable: Throwable) : MyDataState
}