package com.bowoon.detail.tv

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.domain.GetTvDetailUseCase
import com.bowoon.domain.TvWithFavorite
import com.bowoon.model.Tv
import com.bowoon.model.TvEpisode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TvVM.Factory::class)
class TvVM @AssistedInject constructor(
    @Assisted(value = "id") val id: Int,
    private val getTvDetailUseCase: GetTvDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository
) : ViewModel() {
    companion object {
        private const val TAG = "TvVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(value = "id") id: Int
        ): TvVM
    }

    private val reload = MutableSharedFlow<Unit>(replay = 1)
    @OptIn(ExperimentalCoroutinesApi::class)
    val tv = reload.flatMapLatest {
        trace("GetTvDetail") { getTvDetailUseCase(id = id).asResult() }
    }.map { result ->
        when (result) {
            is Result.Loading -> TvState.Loading
            is Result.Success -> TvState.Success(tv = result.data)
            is Result.Error -> TvState.Error(throwable = result.throwable)
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = TvState.Loading,
        started = SharingStarted.Lazily
    )
    val similarTvs = Pager(
        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
        initialKey = 1,
        pagingSourceFactory = { pagingRepository.getSimilarTvPagingSource(id = id) }
    ).flow.cachedIn(scope = viewModelScope)
    private val _selectedEpisode = MutableStateFlow<TvEpisode?>(value = null)
    val selectedEpisode = _selectedEpisode.asStateFlow()

    init {
        viewModelScope.launch {
            reload.emit(value = Unit)
        }
    }

    fun restart() {
        viewModelScope.launch {
            reload.emit(value = Unit)
        }
    }

    fun insertTv(tv: Tv) {
        viewModelScope.launch {
            databaseRepository.insertTv(tv = tv)
        }
    }

    fun deleteTv(tv: Tv) {
        viewModelScope.launch {
            databaseRepository.deleteTv(tv = tv)
        }
    }

    fun showEpisodeDetail(episode: TvEpisode) {
        viewModelScope.launch {
            _selectedEpisode.emit(value = episode)
        }
    }

    fun hideEpisodeDetail() {
        viewModelScope.launch {
            _selectedEpisode.emit(value = null)
        }
    }
}

sealed interface TvState {
    data object Loading : TvState
    data class Success(val tv: TvWithFavorite) : TvState
    data class Error(val throwable: Throwable) : TvState
}