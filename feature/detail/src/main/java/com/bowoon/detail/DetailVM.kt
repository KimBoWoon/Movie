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
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.model.Favorite
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMovieDetail: GetMovieDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val userDataRepository: UserDataRepository,
    private val detailRepository: DetailRepository
) : ViewModel() {
    companion object {
        private const val TAG = "DetailVM"
    }

    init {
        viewModelScope.launch {
            userDataRepository.internalData.collect {
                language = it.language
                region = it.region
            }
        }
    }

    private var language: String = ""
    private var region: String = ""
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
            started = SharingStarted.Lazily
        )
    val similarMovies = Pager(
        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
        initialKey = 1,
        pagingSourceFactory = {
            pagingRepository.getSimilarMovies(
                id = id,
                language = "$language-$region",
                region = region
            )
        }
    ).flow.cachedIn(viewModelScope)

    fun getMovieSeries(collectionId: Int): Flow<MovieSeries> =
        detailRepository.getMovieSeries(collectionId = collectionId)

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