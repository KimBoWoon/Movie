package com.bowoon.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.KmdbRepository
import com.bowoon.data.repository.KobisRepository
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.model.MovieDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val kobisRepository: KobisRepository,
    private val kmdbRepository: KmdbRepository
) : ViewModel() {
    companion object {
        private const val TAG = "DetailVM"
    }

    private val openDt = savedStateHandle.toRoute<DetailRoute>().openDt
    private val movieCd = savedStateHandle.toRoute<DetailRoute>().movieCd
    private val title = savedStateHandle.toRoute<DetailRoute>().title
    val movieInfo = getMovieInfo(
        kobisRepository = kobisRepository,
        kmdbRepository = kmdbRepository,
        movieCd = movieCd,
        kmdbUrl = "https://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&detail=Y&title=$title&releaseDts=$openDt&listCount=1&ServiceKey=${BuildConfig.KMDB_OPEN_API_KEY}"
    ).stateIn(
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

fun getMovieInfo(
    kobisRepository: KobisRepository,
    kmdbRepository: KmdbRepository,
    movieCd: String,
    kmdbUrl: String
): Flow<MovieDetailState> =
    combine(
        kobisRepository.getMovieInfo(BuildConfig.KOBIS_OPEN_API_KEY, movieCd),
        kmdbRepository.getMovieInfo(kmdbUrl)
    ) { kobisMovieInfo, kmdbMovieInfo ->
        MovieDetail(
            title = kobisMovieInfo.movieInfoResult?.movieInfo?.movieNm,
            titleEng = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.titleEng,
            genre = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.genre,
            plots = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.plots,
            rating = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.rating,
            posters = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.getPosterList(),
            stlls = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.getStllList(),
            vods = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.vods,
            staffs = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.staffs,
            repRlsDate = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.repRlsDate,
            runtime = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.runtime,
            salesAcc = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.salesAcc,
            audiAcc = kmdbMovieInfo.data?.firstOrNull()?.result?.firstOrNull()?.audiAcc
        )
    }.asResult()
        .map {
            when (it) {
                is Result.Loading -> MovieDetailState.Loading
                is Result.Success -> MovieDetailState.Success(it.data)
                is Result.Error -> MovieDetailState.Error(it.throwable)
            }
        }