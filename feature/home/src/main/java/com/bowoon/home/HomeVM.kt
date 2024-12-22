package com.bowoon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.domain.GetDailyBoxOfficeUseCase
import com.bowoon.model.DailyBoxOffice
import com.bowoon.model.MovieDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val getBoxOfficeUseCase: GetDailyBoxOfficeUseCase,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    val boxOfficeState: StateFlow<BoxOfficeState> =
        getBoxOfficeUseCase(
            targetDt = LocalDate.now(),
            kobisOpenApiKey = BuildConfig.KOBIS_OPEN_API_KEY,
            kmdbOpenApiKey = BuildConfig.KMDB_OPEN_API_KEY
        ).asResult()
            .map {
                when (it) {
                    is Result.Loading -> BoxOfficeState.Loading
                    is Result.Success -> BoxOfficeState.Success(it.data)
                    is Result.Error -> BoxOfficeState.Error(it.throwable)
                }
            }
            .stateIn(
            scope = viewModelScope,
            initialValue = BoxOfficeState.Loading,
            started = SharingStarted.Eagerly
        )
    val favoriteMovies = userDataRepository.userData
        .map { it.favoriteMovies }
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> FavoriteMoviesState.Loading
                is Result.Success -> FavoriteMoviesState.Success(it.data)
                is Result.Error -> FavoriteMoviesState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = FavoriteMoviesState.Loading,
            started = SharingStarted.Eagerly
        )
}

sealed interface BoxOfficeState {
    data object Loading : BoxOfficeState
    data class Success(val boxOffice: List<DailyBoxOffice>) : BoxOfficeState
    data class Error(val throwable: Throwable) : BoxOfficeState
}

sealed interface FavoriteMoviesState {
    data object Loading : FavoriteMoviesState
    data class Success(val favoriteMovies: List<MovieDetail>) : FavoriteMoviesState
    data class Error(val throwable: Throwable) : FavoriteMoviesState
}