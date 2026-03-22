package com.cheeke.surfy.detail.tv

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSelectContent
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.repository.TvDetailRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.domain.GetTvDetailUseCase
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeason
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TvVM.Factory::class)
class TvVM @AssistedInject constructor(
    @Assisted(value = "id") val id: Int,
    private val getTvDetailUseCase: GetTvDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val detailRepository: TvDetailRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val userDataRepository: UserDataRepository
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
    val similarTvs = userDataRepository.internalData
        .map { it.language to it.region }
        .flatMapLatest {
            Pager(
                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                initialKey = 1,
                pagingSourceFactory = {
                    pagingRepository.getSimilarTvPagingSource(
                        id = id,
                        language = it.first,
                        region = it.second
                    )
                }
            ).flow.cachedIn(scope = viewModelScope)
        }
    private val _selectedEpisode = MutableStateFlow<TvEpisode?>(value = null)
    val selectedEpisode = _selectedEpisode.asStateFlow()
    private val selectedSeason = MutableStateFlow<TvSeason?>(value = null)
    private val episodesCache = MutableStateFlow<Map<String, List<TvEpisode>>>(value = emptyMap())
    @OptIn(ExperimentalCoroutinesApi::class)
    private val episodesLoadState: StateFlow<EpisodesLoadState> = selectedSeason
        .flatMapLatest { season ->
            if (season == null) {
                flowOf(value = EpisodesLoadState.Idle)
            } else {
                val cached = episodesCache.value[season.name]

                if (cached != null) {
                    flowOf(value = EpisodesLoadState.Idle)
                } else {
                    detailRepository.getTvSeasons(seriesId = id, seasonNumber = season.seasonNumber ?: -1)
                        .onEach { tvSeasons ->
                            val episodes = tvSeasons.episodes
                            episodesCache.update { it + ((tvSeasons.name ?: "") to (episodes ?: emptyList())) }
                        }.asResult()
                        .map { result ->
                            when (result) {
                                is Result.Loading -> EpisodesLoadState.Loading(message = "${season.name}을 불러오고 있습니다.")
                                is Result.Success -> EpisodesLoadState.Idle
                                is Result.Error -> EpisodesLoadState.Error(message = "${season.name}을 불러오지 못했습니다.")
                            }
                        }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = EpisodesLoadState.Idle
        )
    @OptIn(ExperimentalCoroutinesApi::class)
    private val tv = reload.flatMapLatest {
        trace(sectionName = "GetTvDetail") { getTvDetailUseCase(id = id) }
    }
    val uiState: StateFlow<TvState> =
        combine(
            tv,
            selectedSeason,
            episodesCache,
            episodesLoadState
        ) { twf, selectedSeason, seasonMap, episodeState ->
            val tv = twf.tv
            val seasons = tv.seasons
            val initialSeason = selectedSeason ?: seasons?.sortedBy { it.seasonNumber }?.firstOrNull()

            if (selectedSeason == null && initialSeason != null) {
                this@TvVM.selectedSeason.value = initialSeason
            }

            TvUiState(
                tv = twf.tv,
                seasons = seasons.orEmpty(),
                episodeState = episodeState,
                episodesBySeason = seasonMap,
                isFavorite = twf.isFavorite,
                autoPlayTrailer = twf.autoPlayTrailer
            )
        }.asResult()
            .map { result ->
                when (result) {
                    is Result.Loading -> TvState.Loading
                    is Result.Success -> {
                        analyticsHelper.logSelectContent(contentType = "tv", media = result.data.tv)
                        TvState.Success(tvUiState = result.data)
                    }
                    is Result.Error -> TvState.Error(message = result.throwable.message ?: "something wrong...")
                }
            }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TvState.Loading
        )

    init {
        viewModelScope.launch {
            reload.emit(value = Unit)
        }
    }

    fun onSelectSeason(season: TvSeason) {
        viewModelScope.launch {
            selectedSeason.emit(value = season)
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
    data class Success(val tvUiState: TvUiState) : TvState
    data class Error(val message: String) : TvState
}

sealed interface EpisodesLoadState {
    data object Idle : EpisodesLoadState
    data class Loading(val message: String) : EpisodesLoadState
    data class Error(val message: String) : EpisodesLoadState
}

data class TvUiState(
    val tv: Tv,
    val seasons: List<TvSeason>,
    val episodeState: EpisodesLoadState,
    val episodesBySeason: Map<String, List<TvEpisode>>,
    val isFavorite: Boolean,
    val autoPlayTrailer: Boolean
)