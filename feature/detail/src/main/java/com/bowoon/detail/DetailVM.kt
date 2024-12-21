package com.bowoon.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.domain.GetMovieDetailUseCase
import com.bowoon.model.MovieDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMovieDetailUseCase: GetMovieDetailUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "DetailVM"
    }

    private val openDt = savedStateHandle.toRoute<DetailRoute>().openDt
    private val movieCd = savedStateHandle.toRoute<DetailRoute>().movieCd
    private val title = savedStateHandle.toRoute<DetailRoute>().title
    val movieInfo = getMovieDetailUseCase(
        kobisApiKey = BuildConfig.KOBIS_OPEN_API_KEY,
        movieCd = movieCd,
        kmdbUrl = "https://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&detail=Y&title=$title&releaseDts=$openDt&listCount=1&ServiceKey=${BuildConfig.KMDB_OPEN_API_KEY}"
    ).asResult()
    .map {
        when (it) {
            is Result.Loading -> MovieDetailState.Loading
            is Result.Success -> MovieDetailState.Success(it.data)
            is Result.Error -> MovieDetailState.Error(it.throwable)
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieDetailState.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )
}

sealed interface MovieDetailState {
    data object Loading : MovieDetailState
    data class Success(val movieDetail: MovieDetail) : MovieDetailState
    data class Error(val throwable: Throwable) : MovieDetailState
}