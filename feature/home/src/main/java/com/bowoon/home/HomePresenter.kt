package com.bowoon.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.bowoon.common.Log
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.SyncManager
import com.bowoon.domain.GetMovieAppDataUseCase
import com.bowoon.home.navigation.Home
import com.bowoon.model.MainMenu
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.flow.combine

class HomePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val goToMovie: (Int) -> Unit,
    private val syncManager: SyncManager,
    private val userDataRepository: UserDataRepository,
    private val getMovieAppDataUseCase: GetMovieAppDataUseCase
) : Presenter<HomeUiState> {
    @Composable
    override fun present(): HomeUiState {
        val isSyncing by syncManager.isSyncing.collectAsRetainedState(initial = false)

        val mainMenu by produceRetainedState<MainMenuState>(MainMenuState.Loading) {
            combine(
                getMovieAppDataUseCase(),
                userDataRepository.internalData
            ) { movieAppData, internalData ->
                MainMenuState.Success(
                    MainMenu(
                        nowPlaying = internalData.mainMenu.nowPlaying.map { it.copy(posterPath = "${movieAppData.getImageUrl()}${it.posterPath}") },
                        upComingMovies = internalData.mainMenu.upComingMovies.map { it.copy(posterPath = "${movieAppData.getImageUrl()}${it.posterPath}") }
                    )
                )
            }.collect {
                value = it
            }
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

sealed interface HomeEvent : CircuitUiEvent {
    data class GoToMovie(val id: Int) : HomeEvent
}

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(val mainMenu: MainMenu) : MainMenuState
    data class Error(val throwable: Throwable) : MainMenuState
}