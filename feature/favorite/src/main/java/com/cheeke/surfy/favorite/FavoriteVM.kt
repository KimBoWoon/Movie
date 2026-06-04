package com.cheeke.surfy.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
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
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = FavoriteVM.Factory::class)
class FavoriteVM @AssistedInject constructor(
    @Assisted initialTabIndex: Int,
    private val movieDataBaseRepository: MovieDataBaseRepository,
    private val peopleDataBaseRepository: PeopleDataBaseRepository,
    private val tvDataBaseRepository: TvDataBaseRepository
) : ViewModel() {
    companion object {
        internal const val TAG = "FavoriteVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(tab: Int): FavoriteVM
    }

    private val _tabIndex = MutableStateFlow(value = initialTabIndex)
    val tabIndex = _tabIndex.asStateFlow()
    val favoriteMovies = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { movieDataBaseRepository.getFavorite() }
    ).flow.map { pagingData ->
        pagingData.map(transform = MovieEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)
    val favoritePeoples = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { peopleDataBaseRepository.getFavorite() }
    ).flow.map { pagingData ->
        pagingData.map(transform = PeopleEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)
    val favoriteTvs = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { tvDataBaseRepository.getFavorite() }
    ).flow.map { pagingData ->
        pagingData.map(transform = TvEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)
    val currentPagingItems = tabIndex
        .map { index ->
            when (FavoriteTab.entries[index]) {
                FavoriteTab.MOVIE -> FavoriteUiState.MovieState(items = favoriteMovies)
                FavoriteTab.TV -> FavoriteUiState.TvState(items = favoriteTvs)
                FavoriteTab.PEOPLE -> FavoriteUiState.PeopleState(items = favoritePeoples)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = FavoriteUiState.MovieState(items = favoriteMovies)
        )

    fun updateTabIndex(index: Int) {
        _tabIndex.value = index
    }

    fun deleteFavorite(favoriteTab: FavoriteTab, media: Media) {
        viewModelScope.launch {
            when (favoriteTab) {
                FavoriteTab.MOVIE -> movieDataBaseRepository.delete(media = media)
                FavoriteTab.TV -> tvDataBaseRepository.delete(media = media)
                FavoriteTab.PEOPLE -> peopleDataBaseRepository.delete(media = media)
            }
        }
    }
}

enum class FavoriteTab(val stringId: Int) {
    MOVIE(stringId = R.string.movie),
    TV(stringId = R.string.tv),
    PEOPLE(stringId = R.string.people)
}

sealed interface FavoriteUiState {
    data class MovieState(val items: Flow<PagingData<Movie>>) : FavoriteUiState
    data class TvState(val items: Flow<PagingData<Tv>>) : FavoriteUiState
    data class PeopleState(val items: Flow<PagingData<People>>) : FavoriteUiState
}