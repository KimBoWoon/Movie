package com.cheeke.surfy.detail.people

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.util.trace
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSelectContent
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.common.di.ActivityRetainedScopeCoroutine
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.detail.people.navigation.PeopleScreen
import com.cheeke.surfy.domain.GetPeopleDetailUseCase
import com.cheeke.surfy.domain.PeopleWithFavorite
import com.cheeke.surfy.model.People
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class PeopleRepository @Inject constructor(
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope,
    private val getPeopleDetail: GetPeopleDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val analyticsHelper: AnalyticsHelper
) {
    companion object {
        private const val TAG = "PeopleRepository"
    }

    private val reload = MutableSharedFlow<Unit>(replay = 1)

    init {
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPeople(id: Int): Flow<PeopleState> {
        return reload
            .flatMapLatest {
                trace(sectionName = "GetPeopleDetail") { getPeopleDetail(personId = id) }.asResult()
            }.map { result ->
                when (result) {
                    is Result.Loading -> PeopleState.Loading
                    is Result.Success -> {
                        analyticsHelper.logSelectContent(contentType = "people", media = result.data.people)
                        PeopleState.Success(data = result.data)
                    }
                    is Result.Error -> PeopleState.Error(result.throwable)
                }
            }.stateIn(
                scope = scope,
                initialValue = PeopleState.Loading,
                started = SharingStarted.Lazily
            )
    }

    fun restart() {
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    fun insertPeople(people: People) {
        scope.launch {
            databaseRepository.insertPeople(people)
        }
    }

    fun deletePeople(people: People) {
        scope.launch {
            databaseRepository.deletePeople(people)
        }
    }
}

class PeoplePresenter @AssistedInject constructor(
    @Assisted(value = "navigator") private val navigator: Navigator,
    @Assisted(value = "screen") private val screen: PeopleScreen,
    @Assisted(value = "goToMovie") private val goToMovie: (Int) -> Unit,
    @Assisted(value = "goToTv") private val goToTv: (Int) -> Unit,
    private val peopleRepository: PeopleRepository
) : Presenter<PeopleUiState> {
    @Composable
    override fun present(): PeopleUiState {
        val people by produceRetainedState<PeopleState>(initialValue = PeopleState.Loading) {
            peopleRepository.getPeople(id = screen.id).collect { peopleState ->
                value = peopleState
            }
        }

        return PeopleUiState(
            people = people,
        ) { event ->
            Log.d("HomePresenter", "$event")
            when (event) {
                is PeopleEvent.DeleteFavoritePeople -> peopleRepository.deletePeople(people = event.people)
                is PeopleEvent.GoToMovie -> goToMovie(event.id)
                is PeopleEvent.GoToTv -> goToTv(event.id)
                is PeopleEvent.InsertFavoritePeople -> peopleRepository.insertPeople(people = event.people)
                is PeopleEvent.Restart -> peopleRepository.restart()
                is PeopleEvent.GoToBack -> navigator.pop()
            }
        }
    }

    @CircuitInject(screen = PeopleScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(value = "navigator") navigator: Navigator,
            @Assisted(value = "screen") screen: PeopleScreen,
            @Assisted(value = "goToMovie") goToMovie: ((Int) -> Unit) = {},
            @Assisted(value = "goToTv") goToTv: ((Int) -> Unit) = {},
        ): PeoplePresenter
    }
}

data class PeopleUiState(
    val people: PeopleState,
    val eventSink: (PeopleEvent) -> Unit
) : CircuitUiState

sealed interface PeopleEvent {
    object GoToBack : PeopleEvent
    object Restart : PeopleEvent
    data class GoToMovie(val id: Int) : PeopleEvent
    data class GoToTv(val id: Int) : PeopleEvent
    data class InsertFavoritePeople(val people: People) : PeopleEvent
    data class DeleteFavoritePeople(val people: People) : PeopleEvent
}

sealed interface PeopleState {
    data object Loading : PeopleState
    data class Success(val data: PeopleWithFavorite) : PeopleState
    data class Error(val throwable: Throwable) : PeopleState
}