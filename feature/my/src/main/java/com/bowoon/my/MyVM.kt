package com.bowoon.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.ApplicationData
import com.bowoon.model.InternalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyVM @Inject constructor(
    private val userDataRepository: UserDataRepository,
    val movieAppData: ApplicationData
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

    fun updateUserData(userData: InternalData, isSync: Boolean) {
        viewModelScope.launch {
            userDataRepository.updateUserData(userData, isSync)
        }
    }
}

sealed interface MyMenu {
    data class Display(val label: String, val content: String) : MyMenu
    data class Switch(val label: String, val selected: Boolean, val onClick: ((Boolean) -> Unit)) : MyMenu
    data class Dialog(val label: String, val selected: Any, val list: List<Any>, val content: String, val updateLambda: (Any?) -> Unit) : MyMenu
}