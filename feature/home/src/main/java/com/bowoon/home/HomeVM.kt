package com.bowoon.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.bowoon.common.Log
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.SyncManager
import com.bowoon.home.navigation.Home
import com.bowoon.model.MainMenu
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.flow.map

class HomePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val goToMovie: (Int) -> Unit,
    private val syncManager: SyncManager,
    private val userDataRepository: UserDataRepository
) : Presenter<HomeUiState> {
    @Composable
    override fun present(): HomeUiState {
        val isSyncing by produceRetainedState<Boolean>(false) {
            syncManager.isSyncing.collect { value = it }
        }

        val mainMenu by produceRetainedState<MainMenuState>(initialValue = MainMenuState.Loading) {
            userDataRepository.internalData
                .map { it.mainMenu }
                .asResult()
                .map {
                    when (it) {
                        is Result.Loading -> MainMenuState.Loading
                        is Result.Success -> MainMenuState.Success(it.data)
                        is Result.Error -> MainMenuState.Error(it.throwable)
                    }
                }.collect { value = it }
        }

        return HomeUiState(
            isSyncing = isSyncing,
            mainMenuState = mainMenu
        ) { event ->
            Log.d("HomePresenter", "$event")
            when (event) {
                is HomeEvent.GoToMovie -> goToMovie(event.id)
            }
        }
    }

    @CircuitInject(Home::class, ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            navigator: Navigator,
            goToMovie: ((Int) -> Unit) = {}
        ): HomePresenter
    }
}

class HomeUiState(
    val isSyncing: Boolean,
    val mainMenuState: MainMenuState,
    val eventSink: (HomeEvent) -> Unit
) : CircuitUiState

sealed interface HomeEvent {
    data class GoToMovie(val id: Int) : HomeEvent
}

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(val mainMenu: MainMenu) : MainMenuState
    data class Error(val throwable: Throwable) : MainMenuState
}