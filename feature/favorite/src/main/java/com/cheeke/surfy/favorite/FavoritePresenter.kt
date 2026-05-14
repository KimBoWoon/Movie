package com.cheeke.surfy.favorite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.di.ActivityRetainedScopeCoroutine
import com.cheeke.surfy.data.repository.MovieDataBaseRepository
import com.cheeke.surfy.data.repository.PeopleDataBaseRepository
import com.cheeke.surfy.data.repository.TvDataBaseRepository
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.feature.favorite.R
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.navigation.FavoriteScreen
import com.cheeke.surfy.navigation.LocalAppNavigator
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class FavoriteRepository @Inject constructor(
    private val movieDataBaseRepository: MovieDataBaseRepository,
    private val peopleDataBaseRepository: PeopleDataBaseRepository,
    private val tvDataBaseRepository: TvDataBaseRepository,
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "FavoriteRepository"
    }

    private val _tabIndex = MutableStateFlow(value = 0)
    val tabIndex = _tabIndex.asStateFlow()
    val favoriteMovies = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { movieDataBaseRepository.getFavorite() }
    ).flow.map { pagingData ->
        pagingData.map(transform = MovieEntity::asExternalModel)
    }.cachedIn(scope = scope)
    val favoritePeoples = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { peopleDataBaseRepository.getFavorite() }
    ).flow.map { pagingData ->
        pagingData.map(transform = PeopleEntity::asExternalModel)
    }.cachedIn(scope = scope)
    val favoriteTvs = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { tvDataBaseRepository.getFavorite() }
    ).flow.map { pagingData ->
        pagingData.map(transform = TvEntity::asExternalModel)
    }.cachedIn(scope = scope)

    fun setTabIndex(index: Int) {
        _tabIndex.value = index
    }

    fun deleteMovie(movie: Movie) {
        scope.launch {
            movieDataBaseRepository.delete(media = movie)
        }
    }

    fun deleteTv(tv: Tv) {
        scope.launch {
            tvDataBaseRepository.delete(media = tv)
        }
    }

    fun deletePeople(people: People) {
        scope.launch {
            peopleDataBaseRepository.delete(media = people)
        }
    }
}

class FavoritePresenter @AssistedInject constructor(
    @Assisted private val screen: FavoriteScreen,
    private val favoriteRepository: FavoriteRepository
) : Presenter<FavoriteUiState> {
    @Composable
    override fun present(): FavoriteUiState {
        val navigator = LocalAppNavigator.current
        val tabIndex by favoriteRepository.tabIndex.collectAsStateWithLifecycle(initialValue = screen.index)
        val favoriteMap = FavoriteTab.entries.associateWith { favoriteTab ->
            when (favoriteTab) {
                FavoriteTab.MOVIE -> favoriteRepository.favoriteMovies.collectAsLazyPagingItems()
                FavoriteTab.TV -> favoriteRepository.favoriteTvs.collectAsLazyPagingItems()
                FavoriteTab.PEOPLE -> favoriteRepository.favoritePeoples.collectAsLazyPagingItems()
            }
        }
        val selectedTab = FavoriteTab.entries[tabIndex]

        return FavoriteUiState(
            tabIndex = tabIndex,
            selectedTab = selectedTab,
            favoriteMap = favoriteMap,
        ) { event ->
            Log.d("FavoritePresenter", "$event")
            when (event) {
                is FavoriteEvent.UpdateTabIndex -> favoriteRepository.setTabIndex(index = event.index)
                is FavoriteEvent.GoTo -> {
                    when (event.favoriteTab) {
                        FavoriteTab.MOVIE -> navigator.goToMovie(id = event.media.id ?: -1)
                        FavoriteTab.PEOPLE -> navigator.goToPeople(id = event.media.id ?: -1)
                        FavoriteTab.TV -> navigator.goToTv(id = event.media.id ?: -1)
                    }
                }
                is FavoriteEvent.DeleteFavorite -> {
                    when (event.favoriteTab) {
                        FavoriteTab.MOVIE -> favoriteRepository.deleteMovie(movie = event.media as Movie)
                        FavoriteTab.PEOPLE -> favoriteRepository.deletePeople(people = event.media as People)
                        FavoriteTab.TV -> favoriteRepository.deleteTv(tv = event.media as Tv)
                    }
                }
            }
        }
    }

    @CircuitInject(screen = FavoriteScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted screen: FavoriteScreen
        ): FavoritePresenter
    }
}

data class FavoriteUiState(
    val tabIndex: Int,
    val selectedTab: FavoriteTab,
    val favoriteMap: Map<FavoriteTab, LazyPagingItems<out Media>>,
    val eventSink: (FavoriteEvent) -> Unit,
) : CircuitUiState

sealed interface FavoriteEvent {
    data class UpdateTabIndex(val index: Int) : FavoriteEvent
    data class GoTo(val favoriteTab: FavoriteTab, val media: Media) : FavoriteEvent
    data class DeleteFavorite(val favoriteTab: FavoriteTab, val media: Media) : FavoriteEvent
}

enum class FavoriteTab(val stringId: Int) {
    MOVIE(stringId = R.string.movie),
    TV(stringId = R.string.tv),
    PEOPLE(stringId = R.string.people)
}