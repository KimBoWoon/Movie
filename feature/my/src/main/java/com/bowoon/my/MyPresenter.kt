package com.bowoon.my

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InternalData
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyPresenter @Inject constructor(
    private val userDataRepository: UserDataRepository
) : Presenter<MyUiState> {
    @Composable
    override fun present(): MyUiState {
        val scope = rememberCoroutineScope()
        val internalData by produceRetainedState<InternalData>(initialValue = InternalData()) {
            userDataRepository.internalData.collect { value = it }
        }

        return MyUiState(
            internalData = internalData
        ) { state ->
            when (state) {
                is MyEvent.UpdateInternalData -> {
                    scope.launch {
                        userDataRepository.updateUserData(userData = state.internalData, isSync = state.isSync)
                    }
                }
            }
        }
    }

//    @CircuitInject(My::class, ActivityRetainedComponent::class)
//    @AssistedFactory
//    fun interface Factory {
//        fun create(navigator: Navigator): MyPresenter
//    }
}

data class MyUiState(
    val internalData: InternalData,
    val eventSink: (MyEvent) -> Unit
) : CircuitUiState

sealed interface MyEvent : CircuitUiEvent {
    data class UpdateInternalData(val internalData: InternalData, val isSync: Boolean) : MyEvent
}

enum class Menu {
    MAIN_UPDATE_DATE,
    DARK_MODE_SETTING,
    IS_ADULT,
    IS_AUTO_PLAYING_TRAILER,
    LANGUAGE,
    REGION,
    IMAGE_QUALITY
}