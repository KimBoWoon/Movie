package com.cheeke.surfy.detail.people

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.trace
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSelectContent
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.common.di.ActivityRetainedScopeCoroutine
import com.cheeke.surfy.data.repository.PeopleDataBaseRepository
import com.cheeke.surfy.domain.GetPeopleDetailUseCase
import com.cheeke.surfy.feature.detail.R
import com.cheeke.surfy.model.People
import com.cheeke.surfy.navigation.PeopleScreen
import com.cheeke.surfy.navigation.goToMovie
import com.cheeke.surfy.navigation.goToTv
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PeopleRepository @AssistedInject constructor(
    @Assisted private val id: Int,
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope,
    private val getPeopleDetail: GetPeopleDetailUseCase,
    private val peopleDataBaseRepository: PeopleDataBaseRepository,
    private val analyticsHelper: AnalyticsHelper
) {
    @AssistedFactory
    interface Factory {
        fun create(id: Int): PeopleRepository
    }

    companion object {
        private const val TAG = "PeopleRepository"
    }

    private val reload = MutableSharedFlow<Unit>(replay = 1)
    @OptIn(ExperimentalCoroutinesApi::class)
    val peopleState = combine(
        reload.flatMapLatest {
            trace(sectionName = "GetPeopleDetail") { getPeopleDetail(personId = id) }.asResult()
        },
        peopleDataBaseRepository.isFavorite(id = id)
    ) { people, isFavorite ->
        people to isFavorite
    }.map { (result, isFavorite) ->
        when (result) {
            is Result.Loading -> PeopleStatus.Loading
            is Result.Success -> {
                analyticsHelper.logSelectContent(contentType = "people", media = result.data)
                PeopleStatus.Success(people = PeopleUiState(people = result.data, isFavorite = isFavorite))
            }
            is Result.Error -> PeopleStatus.Error(result.throwable)
        }
    }.stateIn(
        scope = scope,
        initialValue = PeopleStatus.Loading,
        started = SharingStarted.Lazily
    )

    init {
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    fun restart() {
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    fun insertPeople(people: People) {
        scope.launch {
            peopleDataBaseRepository.insert(media = people)
        }
    }

    fun deletePeople(people: People) {
        scope.launch {
            peopleDataBaseRepository.delete(media = people)
        }
    }
}

class PeoplePresenter @AssistedInject constructor(
    @Assisted private val screen: PeopleScreen,
    @Assisted private val navigator: Navigator,
    private val peopleRepositoryFactory: PeopleRepository.Factory
) : Presenter<PeopleState> {
    @Composable
    override fun present(): PeopleState {
        val effectFlow = remember { MutableSharedFlow<PeopleEffect>() }
        val scope = rememberCoroutineScope()
        val peopleRepository = rememberRetained(screen.id) {
            peopleRepositoryFactory.create(id = screen.id)
        }
        val peopleState by peopleRepository.peopleState.collectAsStateWithLifecycle()
        val deleteFavoriteMessage = stringResource(id = R.string.remove_favorite_people)
        val insertFavoriteMessage = stringResource(id = R.string.add_favorite_people)

        return PeopleState(
            people = peopleState,
            effect = effectFlow
        ) { event ->
            Log.d("PeoplePresenter", "$event")
            when (event) {
                is PeopleEvent.DeleteFavoritePeople -> {
                    peopleRepository.deletePeople(people = event.people)
                    scope.launch {
                        effectFlow.emit(value = PeopleEffect.ShowSnackbar(deleteFavoriteMessage))
                    }
                }
                is PeopleEvent.GoToMovie -> navigator.goToMovie(id = event.id)
                is PeopleEvent.GoToTv -> navigator.goToTv(id = event.id)
                is PeopleEvent.InsertFavoritePeople -> {
                    peopleRepository.insertPeople(people = event.people)
                    scope.launch {
                        effectFlow.emit(value = PeopleEffect.ShowSnackbar(insertFavoriteMessage))
                    }
                }
                is PeopleEvent.Restart -> peopleRepository.restart()
                is PeopleEvent.GoToBack -> navigator.pop()
            }
        }
    }

    @CircuitInject(screen = PeopleScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: PeopleScreen,
            navigator: Navigator
        ): PeoplePresenter
    }
}

data class PeopleUiState(
    val people: People,
    val isFavorite: Boolean
)

data class PeopleState(
    val people: PeopleStatus,
    val effect: Flow<PeopleEffect>,
    val eventSink: (PeopleEvent) -> Unit
) : CircuitUiState

sealed interface PeopleEvent : CircuitUiEvent {
    object GoToBack : PeopleEvent
    object Restart : PeopleEvent
    data class GoToMovie(val id: Int) : PeopleEvent
    data class GoToTv(val id: Int) : PeopleEvent
    data class InsertFavoritePeople(val people: People) : PeopleEvent
    data class DeleteFavoritePeople(val people: People) : PeopleEvent
}

sealed interface PeopleEffect {
    data class ShowSnackbar(
        val message: String
    ) : PeopleEffect
}

sealed interface PeopleStatus {
    data object Loading : PeopleStatus
    data class Success(val people: PeopleUiState) : PeopleStatus
    data class Error(val throwable: Throwable) : PeopleStatus
}