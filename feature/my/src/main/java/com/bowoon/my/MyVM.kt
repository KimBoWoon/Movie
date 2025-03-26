package com.bowoon.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InternalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyVM @Inject constructor(
    private val userDataRepository: UserDataRepository
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

enum class Menu(val label: String) {
    MAIN_UPDATE_DATE("메인 업데이트 날짜"),
    DARK_MODE_SETTING("다크모드 설정"),
    IS_ADULT("성인"),
    IS_AUTO_PLAYING_TRAILER("예고편 자동 재생"),
    LANGUAGE("언어"),
    REGION("지역"),
    IMAGE_QUALITY("이미지 퀄리티")
}