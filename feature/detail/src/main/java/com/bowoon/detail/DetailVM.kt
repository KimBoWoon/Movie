package com.bowoon.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.restartableStateIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.model.Favorite
import com.bowoon.model.MovieDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMovieDetail: GetMovieDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "DetailVM"
    }

    private val id = savedStateHandle.toRoute<DetailRoute>().id
    val movieInfo = getMovieDetail(id)
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> MovieDetailState.Loading
                is Result.Success -> MovieDetailState.Success(it.data)
                is Result.Error -> MovieDetailState.Error(it.throwable)
            }
        }.restartableStateIn(
            scope = viewModelScope,
            initialValue = MovieDetailState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )
    val similarMovies = userDataRepository.internalData
        .flatMapLatest {
            Pager(
                config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
                pagingSourceFactory = {
                    pagingRepository.getSimilarMovies(
                        id = id,
                        language = "${it.language}-${it.region}",
                        region = it.region
                    )
                }
            ).flow.cachedIn(viewModelScope)
        }

    fun restart() {
        movieInfo.restart()
    }

    fun insertMovie(movie: Favorite) {
        viewModelScope.launch {
            databaseRepository.insertMovie(movie)
        }
    }

    fun deleteMovie(movie: Favorite) {
        viewModelScope.launch {
            databaseRepository.deleteMovie(movie)
        }
    }
}

sealed interface MovieDetailState {
    data object Loading : MovieDetailState
    data class Success(val movieDetail: MovieDetail) : MovieDetailState
    data class Error(val throwable: Throwable) : MovieDetailState
}