package com.bowoon.detail.tv

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.domain.GetTvDetailUseCase
import com.bowoon.domain.TvWithFavorite
import com.bowoon.model.ReviewDataModel
import com.bowoon.model.Tv
import com.bowoon.model.TvEpisode
import com.bowoon.movie.feature.detail.R
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
    @Assisted(value = "initialTabIndex") val initialTabIndex: Int,
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
            @Assisted(value = "id") id: Int,
            @Assisted(value = "initialTabIndex") initialTabIndex: Int
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
    private val _tabIndex = MutableStateFlow(value = initialTabIndex)
    val tabIndex = _tabIndex.asStateFlow()
    val similarTvs = Pager(
        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
        initialKey = 1,
        pagingSourceFactory = { pagingRepository.getSimilarTvPagingSource(id = id) }
    ).flow.cachedIn(scope = viewModelScope)
    val tvReviews = Pager(
        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
        initialKey = 1,
        pagingSourceFactory = { pagingRepository.getTvReviews(seriesId = id) }
    ).flow.map {
        it.map { review -> ReviewDataModel.Item(review = review) }
            .insertSeparators { before, after ->
                if (before != null && after != null) {
                    ReviewDataModel.Separator
                } else {
                    null
                }
            }
    }.cachedIn(scope = viewModelScope)
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

    fun updateTabIndex(index: Int) {
        _tabIndex.value = index
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
        _selectedEpisode.tryEmit(value = episode)
    }

    fun hideEpisodeDetail() {
        _selectedEpisode.tryEmit(value = null)
    }
}

sealed interface TvState {
    data object Loading : TvState
    data class Success(val tv: TvWithFavorite) : TvState
    data class Error(val throwable: Throwable) : TvState
}

enum class TvTab(val stringId: Int) {
    TV_INFO(stringId = R.string.tv_detail),
    TV_SEASONS(stringId = R.string.tv_season),
    TV_EPISODES(stringId = R.string.tv_episode),
    TV_REVIEWS(stringId = R.string.tv_reviews),
    TV_ACTOR_AND_CREW(stringId = R.string.tv_actor_and_crew),
    TV_IMAGES(stringId = R.string.tv_images),
    TV_SIMILAR_TV(stringId = R.string.tv_similar_tv)
}