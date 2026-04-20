package com.cheeke.surfy.favorite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.di.ActivityRetainedScopeCoroutine
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.favorite.navigation.FavoriteScreen
import com.cheeke.surfy.feature.favorite.R
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Tv
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class FavoriteRepository @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "FavoriteRepository"
    }

//    private val _tabIndex = MutableStateFlow(value = initialTabIndex)
    private val _tabIndex = MutableStateFlow(value = 0)
    val tabIndex = _tabIndex.asStateFlow()
    val favoriteMovies = databaseRepository.getMovies()
        .stateIn(
            scope = scope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed()
        )
    val favoritePeoples = databaseRepository.getPeople()
        .stateIn(
            scope = scope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed()
        )
    val favoriteTvs = databaseRepository.getTv()
        .stateIn(
            scope = scope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed()
        )

    fun updateTabIndex(index: Int) {
        _tabIndex.value = index
    }

    fun deleteMovie(movie: Movie) {
        scope.launch {
            databaseRepository.deleteMovie(movie = movie)
        }
    }

    fun deleteTv(tv: Tv) {
        scope.launch {
            databaseRepository.deleteTv(tv = tv)
        }
    }

    fun deletePeople(people: People) {
        scope.launch {
            databaseRepository.deletePeople(people = people)
        }
    }
}

class FavoritePresenter @AssistedInject constructor(
    @Assisted(value = "goToMovie") private val goToMovie: (Int) -> Unit,
    @Assisted(value = "goToPeople") private val goToPeople: (Int) -> Unit,
    @Assisted(value = "goToTv") private val goToTv: (Int) -> Unit,
    private val favoriteRepository: FavoriteRepository
) : Presenter<FavoriteUiState> {
    @Composable
    override fun present(): FavoriteUiState {
        val tabIndex by produceRetainedState(initialValue = 0) {
            favoriteRepository.tabIndex.collect { tabIndex ->
                value = tabIndex
            }
        }
        val favoriteMovies by produceRetainedState(initialValue = emptyList()) {
            favoriteRepository.favoriteMovies.collect { favoriteMovies ->
                value = favoriteMovies
            }
        }
        val favoritePeoples by produceRetainedState(initialValue = emptyList()) {
            favoriteRepository.favoritePeoples.collect { favoritePeoples ->
                value = favoritePeoples
            }
        }
        val favoriteTvs by produceRetainedState(initialValue = emptyList()) {
            favoriteRepository.favoriteTvs.collect { favoriteTvs ->
                value = favoriteTvs
            }
        }

        return FavoriteUiState(
            tabIndex = tabIndex,
            favoriteMovies = favoriteMovies,
            favoritePeoples = favoritePeoples,
            favoriteTvs = favoriteTvs
        ) { event ->
            Log.d("HomePresenter", "$event")
            when (event) {
                is FavoriteEvent.GoToMovie -> goToMovie(event.id)
                is FavoriteEvent.GoToPeople -> goToPeople(event.id)
                is FavoriteEvent.GoToTv -> goToTv(event.id)
                is FavoriteEvent.UpdateTabIndex -> favoriteRepository.updateTabIndex(index = event.index)
                is FavoriteEvent.DeleteFavoriteMovie -> favoriteRepository.deleteMovie(movie = event.movie)
                is FavoriteEvent.DeleteFavoritePeople -> favoriteRepository.deletePeople(people = event.people)
                is FavoriteEvent.DeleteFavoriteTv -> favoriteRepository.deleteTv(tv = event.tv)
            }
        }
    }

    @CircuitInject(FavoriteScreen::class, ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(value = "goToMovie") goToMovie: ((Int) -> Unit) = {},
            @Assisted(value = "goToPeople") goToPeople: ((Int) -> Unit) = {},
            @Assisted(value = "goToTv") goToTv: ((Int) -> Unit) = {}
        ): FavoritePresenter
    }
}

class FavoriteUiState(
    val tabIndex: Int,
    val favoriteMovies: List<Movie>,
    val favoritePeoples: List<People>,
    val favoriteTvs: List<Tv>,
    val eventSink: (FavoriteEvent) -> Unit
) : CircuitUiState

sealed interface FavoriteEvent {
    data class GoToMovie(val id: Int) : FavoriteEvent
    data class GoToPeople(val id: Int) : FavoriteEvent
    data class GoToTv(val id: Int) : FavoriteEvent
    data class UpdateTabIndex(val index: Int) : FavoriteEvent
    data class DeleteFavoriteMovie(val movie: Movie) : FavoriteEvent
    data class DeleteFavoritePeople(val people: People) : FavoriteEvent
    data class DeleteFavoriteTv(val tv: Tv) : FavoriteEvent
}

enum class FavoriteTab(val stringId: Int) {
    MOVIE(stringId = R.string.movie),
    TV(stringId = R.string.tv),
    PEOPLE(stringId = R.string.people)
}