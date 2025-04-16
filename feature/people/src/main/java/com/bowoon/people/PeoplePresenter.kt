package com.bowoon.people

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.domain.GetPeopleDetailUseCase
import com.bowoon.model.Favorite
import com.bowoon.model.PeopleDetail
import com.bowoon.people.navigation.People
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PeoplePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: People,
    @Assisted private val goToMovie: ((Int) -> Unit) = {},
    private val getPeopleDetail: GetPeopleDetailUseCase,
    private val databaseRepository: DatabaseRepository
) : Presenter<PeopleUiState> {
    @Composable
    override fun present(): PeopleUiState {
        val scope = rememberCoroutineScope()
        val people by produceRetainedState<PeopleState>(initialValue = PeopleState.Loading) {
            getPeopleDetail(personId = screen.id)
                .asResult()
                .map {
                    when (it) {
                        is Result.Loading -> PeopleState.Loading
                        is Result.Success -> PeopleState.Success(it.data)
                        is Result.Error -> PeopleState.Error(it.throwable)
                    }
                }.collect { value = it }
        }

        return PeopleUiState(
            people = people
        ) { event ->
            when (event) {
                is PeopleEvent.GoToBack -> navigator.pop()
                is PeopleEvent.GoToMovie -> goToMovie(event.id)
                is PeopleEvent.AddFavorite -> scope.launch { databaseRepository.insertPeople(people = event.favorite) }
                is PeopleEvent.RemoveFavorite -> scope.launch { databaseRepository.deletePeople(people = event.favorite) }
            }
        }
    }

    @CircuitInject(People::class, ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            navigator: Navigator,
            screen: People,
            goToMovie: ((Int) -> Unit) = {}
        ): PeoplePresenter
    }
}

data class PeopleUiState(
    val people: PeopleState,
    val eventSink: (PeopleEvent) -> Unit
) : CircuitUiState

sealed interface PeopleEvent : CircuitUiEvent {
    data object GoToBack : PeopleEvent
    data class GoToMovie(val id: Int) : PeopleEvent
    data class AddFavorite(val favorite: Favorite) : PeopleEvent
    data class RemoveFavorite(val favorite: Favorite) : PeopleEvent
}

sealed interface PeopleState {
    data object Loading : PeopleState
    data class Success(val data: PeopleDetail) : PeopleState
    data class Error(val throwable: Throwable) : PeopleState
}