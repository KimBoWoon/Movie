package com.bowoon.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.domain.GetMainMenuUseCase
import com.bowoon.model.MainMenu
import com.bowoon.model.UpComingResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val getMainMenuUseCase: GetMainMenuUseCase,
    private val userDataRepository: UserDataRepository,
    private val tmdbRepository: TMDBRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HomeVM"
    }

    val upcomingMovies = MutableStateFlow<PagingData<UpComingResult>>(PagingData.empty())
    private val isUpdate = userDataRepository.userData
        .map { userData ->
            val targetDt = LocalDate.now().minusDays(1)
            val updateDate = when (userData.updateDate.isNotEmpty()) {
                true -> LocalDate.parse(userData.updateDate)
                false -> LocalDate.MIN
            }
            userData.updateDate.isEmpty() || targetDt.isAfter(updateDate)
        }
    @OptIn(ExperimentalCoroutinesApi::class)
    val mainMenu = isUpdate.flatMapMerge { isUpdate ->
        when (isUpdate) {
            true -> getMainMenuUseCase(
                targetDt = LocalDate.now().minusDays(1),
                kobisOpenApiKey = BuildConfig.KOBIS_OPEN_API_KEY
            )
            false -> userDataRepository.userData.map { it.mainMenu }
        }
    }.asResult()
        .map {
            when (it) {
                is Result.Loading -> HomeUiState.Loading
                is Result.Success -> {
                    userDataRepository.updateMainMenu(it.data)
                    userDataRepository.updateMainOfDate(LocalDate.now().minusDays(1).toString())
                    HomeUiState.Success(it.data)
                }
                is Result.Error -> HomeUiState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = HomeUiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    fun getUpcomingMoviesPaging() {
        viewModelScope.launch {
            tmdbRepository.getUpcomingMovies()
                .cachedIn(viewModelScope)
                .collect {
                    upcomingMovies.value = it
                }
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val mainMenu: MainMenu) : HomeUiState
    data class Error(val throwable: Throwable) : HomeUiState
}