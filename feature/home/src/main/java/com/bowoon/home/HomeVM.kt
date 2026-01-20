package com.bowoon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.model.asExternalModel
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.database.model.NowPlayingMovieEntity
import com.bowoon.database.model.UpComingMovieEntity
import com.bowoon.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    val mainMenu = combine(
        flowOf(
            value = Pager(
                config = PagingConfig(pageSize = 20, prefetchDistance = 5),
                pagingSourceFactory = { databaseRepository.getNowPlayingMovies() }
            ).flow.map { pagingData ->
                pagingData.map(transform = NowPlayingMovieEntity::asExternalModel)
            }.cachedIn(scope = viewModelScope)
        ),
        flowOf(
            value = Pager(
                config = PagingConfig(pageSize = 20, prefetchDistance = 5),
                pagingSourceFactory = { databaseRepository.getUpComingMovies() }
            ).flow.map { pagingData ->
                pagingData.map(transform = UpComingMovieEntity::asExternalModel)
            }.cachedIn(scope = viewModelScope)
        )
    ) { nowPlayingMovies, upComingMovies ->
        nowPlayingMovies to upComingMovies
    }.asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> MainMenuState.Loading
                is Result.Success -> MainMenuState.Success(nowPlayingMoviePager = result.data.first, upComingMoviePager = result.data.second)
                is Result.Error -> MainMenuState.Error(throwable = result.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MainMenuState.Loading
        )
}

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(
        val nowPlayingMoviePager: Flow<PagingData<Movie>>,
        val upComingMoviePager: Flow<PagingData<Movie>>
    ) : MainMenuState
    data class Error(val throwable: Throwable) : MainMenuState
}