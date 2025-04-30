package com.bowoon.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
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
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMovieDetail: GetMovieDetailUseCase,
    userDataRepository: UserDataRepository,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val detailRepository: DetailRepository
) : ViewModel() {
    companion object {
        private const val TAG = "DetailVM"
    }

    private val internalData = userDataRepository.internalData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    private val id = savedStateHandle.toRoute<DetailRoute>().id
    private var seriesId = MutableStateFlow<Int?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val detail = combine(
        getMovieDetail(id)
            .asResult()
            .onEach { state ->
                when (state) {
                    is Result.Loading, is Result.Error -> {}
                    is Result.Success -> {
                        state.data.belongsToCollection?.id?.let { seriesId ->
                            this@DetailVM.seriesId.emit(seriesId)
                        }
                    }
                }
            },
        flowOf(
            Pager(
                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                initialKey = 1,
                pagingSourceFactory = {
                    pagingRepository.getSimilarMovies(
                        id = id,
                        language = "${internalData.value?.language}-${internalData.value?.region}"
                    )
                }
            ).flow.cachedIn(viewModelScope)
        ),
        seriesId.flatMapLatest { id ->
            id?.let { detailRepository.getMovieSeries(it) } ?: flowOf(null)
        }
    ) { movieDetail, similarMovie, series ->
        when (movieDetail) {
            is Result.Loading -> DetailState.Loading
            is Result.Success -> DetailState.Success(movieDetail.data, series, similarMovie)
            is Result.Error -> DetailState.Error(movieDetail.throwable)
        }
    }.restartableStateIn(
        scope = viewModelScope,
        initialValue = DetailState.Loading,
        started = SharingStarted.Lazily
    )

    fun restart() {
        detail.restart()
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

sealed interface DetailState {
    data object Loading : DetailState
    data class Success(
        val detail: MovieDetail?,
        val series: MovieSeries?,
        val similarMovies: Flow<PagingData<Movie>>
    ) : DetailState
    data class Error(val throwable: Throwable) : DetailState
}