package com.cheeke.surfy.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.feature.favorite.R
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Tv
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = FavoriteVM.Factory::class)
class FavoriteVM @AssistedInject constructor(
    @Assisted initialTabIndex: Int,
    private val databaseRepository: DatabaseRepository
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
        pagingSourceFactory = { databaseRepository.getFavoriteMovie() }
    ).flow.map { pagingData ->
        pagingData.map(transform = MovieEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)
    val favoritePeoples = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { databaseRepository.getFavoritePeople() }
    ).flow.map { pagingData ->
        pagingData.map(transform = PeopleEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)
    val favoriteTvs = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { databaseRepository.getFavoriteTv() }
    ).flow.map { pagingData ->
        pagingData.map(transform = TvEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)

    fun updateTabIndex(index: Int) {
        _tabIndex.value = index
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            databaseRepository.deleteMovie(movie = movie)
        }
    }

    fun deleteTv(tv: Tv) {
        viewModelScope.launch {
            databaseRepository.deleteTv(tv = tv)
        }
    }

    fun deletePeople(people: People) {
        viewModelScope.launch {
            databaseRepository.deletePeople(people = people)
        }
    }
}

enum class FavoriteTab(val stringId: Int) {
    MOVIE(stringId = R.string.movie),
    TV(stringId = R.string.tv),
    PEOPLE(stringId = R.string.people)
}