package com.bowoon.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.model.asExternalModel
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.database.model.NowPlayingMovieEntity
import com.bowoon.database.model.UpComingMovieEntity
import com.bowoon.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    databaseRepository: DatabaseRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    val mainMenu = databaseRepository.getNextWeekReleaseMoviesFlow()
        .onEach {
            if (it.isEmpty()) {
                isShowNextWeekReleaseMovie.value = true
            }
        }.asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> MainMenuState.Loading
                is Result.Success -> MainMenuState.Success(nextWeekReleaseMovies = result.data)
                is Result.Error -> MainMenuState.Error(throwable = result.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MainMenuState.Loading
        )
    val nowPlayingMoviePager = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = {
            databaseRepository.getNowPlayingMovies()
        }
    ).flow.map { pagingData ->
        pagingData.map(transform = NowPlayingMovieEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)
    val upComingMoviePager = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = {
            databaseRepository.getUpComingMovies()
        }
    ).flow.map { pagingData ->
        pagingData.map(transform = UpComingMovieEntity::asExternalModel)
    }.cachedIn(scope = viewModelScope)
    val isShowNextWeekReleaseMovie = mutableStateOf(value = false)

    fun onNoShowToday() {
        viewModelScope.launch {
            userDataRepository.updateUserData(
                userData = userDataRepository.internalData.first().copy(noShowToday = LocalDate.now().toString()),
                isSync = false
            )
        }
    }
}

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(val nextWeekReleaseMovies: List<Movie>) : MainMenuState
    data class Error(val throwable: Throwable) : MainMenuState
}