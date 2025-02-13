package com.bowoon.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InternalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyVM @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "MyVM"
    }

    fun updateUserData(userData: InternalData, isSync: Boolean) {
        viewModelScope.launch {
            userDataRepository.updateUserData(userData, isSync)
        }
    }
}