package com.cheeke.surfy.detail.tv

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.util.trace
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSelectContent
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.common.di.ActivityRetainedScopeCoroutine
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.domain.GetTvDetailUseCase
import com.cheeke.surfy.domain.TvSeasonLoadState
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeason
import com.cheeke.surfy.navigation.LocalAppNavigator
import com.cheeke.surfy.navigation.TvScreen
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TvRepository @AssistedInject constructor(
    @Assisted private val id: Int = 0,
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope,
    private val getTvDetailUseCase: GetTvDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val userDataRepository: UserDataRepository
) {
    @AssistedFactory
    interface Factory {
        fun create(id: Int): TvRepository
    }

    companion object {
        private const val TAG = "TvRepository"
    }

    private val reload = MutableSharedFlow<Unit>(replay = 1)
    private val _selectedEpisode = MutableStateFlow<TvEpisode?>(value = null)
    val selectedEpisode = _selectedEpisode.asStateFlow()
    private val selectedSeason = MutableStateFlow<TvSeason?>(value = null)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val tvData = reload.flatMapLatest {
        trace(sectionName = "GetTvDetail") { getTvDetailUseCase(id = id, selectedSeason = selectedSeason) }
    }
    val tv = combine(
        tvData,
        selectedSeason,
    ) { twf, selectedSeason/*, seasonMap, episodeState*/ ->
        val tv = twf.tv
        val seasons = tv.seasons
        val initialSeason = selectedSeason ?: seasons?.sortedBy { it.seasonNumber }?.firstOrNull()

        if (selectedSeason == null && initialSeason != null) {
            this@TvRepository.selectedSeason.value = initialSeason
        }

        TvInfo(
            tv = twf.tv,
            seasons = seasons.orEmpty(),
            episodeState = twf.seasonLoadState,
            episodesBySeason = twf.episodesBySeason,
            isFavorite = twf.isFavorite,
            autoPlayTrailer = twf.autoPlayTrailer
        )
    }.asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> TvState.Loading
                is Result.Success -> {
                    analyticsHelper.logSelectContent(contentType = "tv", media = result.data.tv)
                    TvState.Success(tvInfo = result.data)
                }
                is Result.Error -> TvState.Error(message = result.throwable.message ?: "something wrong...")
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = TvState.Loading
        )
    @OptIn(ExperimentalCoroutinesApi::class)
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
            ).flow
        }.cachedIn(scope = scope)

    init {
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    fun onSelectSeason(season: TvSeason) {
        scope.launch {
            selectedSeason.emit(value = season)
        }
    }

    fun restart() {
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    fun insertTv(tv: Tv) {
        scope.launch {
            databaseRepository.insertTv(tv = tv)
        }
    }

    fun deleteTv(tv: Tv) {
        scope.launch {
            databaseRepository.deleteTv(tv = tv)
        }
    }

    fun showEpisodeDetail(episode: TvEpisode) {
        scope.launch {
            _selectedEpisode.emit(value = episode)
        }
    }

    fun hideEpisodeDetail() {
        scope.launch {
            _selectedEpisode.emit(value = null)
        }
    }
}

class TvPresenter @AssistedInject constructor(
    @Assisted(value = "screen") private val screen: TvScreen,
    private val seriesRepositoryFactory: TvRepository.Factory
) : Presenter<TvUiState> {
    @Composable
    override fun present(): TvUiState {
        val navigator = LocalAppNavigator.current
        val seriesRepository = rememberRetained(screen.id) {
            seriesRepositoryFactory.create(id = screen.id)
        }
        val tv by seriesRepository.tv.collectAsStateWithLifecycle()
        val similarTvs = seriesRepository.similarTvs.collectAsLazyPagingItems()
        val selectedEpisode by seriesRepository.selectedEpisode.collectAsStateWithLifecycle()

        return TvUiState(
            tv = tv,
            similarTvs = similarTvs,
            selectedEpisode = selectedEpisode
        ) { event ->
            Log.d("HomePresenter", "$event")
            when (event) {
                is TvEvent.GoToTv -> navigator.goToTv(event.id)
                is TvEvent.GoToPeople -> navigator.goToPeople(event.id)
                is TvEvent.Restart -> seriesRepository.restart()
                is TvEvent.GoToBack -> navigator.back()
                is TvEvent.InsertTv -> seriesRepository.insertTv(tv = event.tv)
                is TvEvent.DeleteTv -> seriesRepository.deleteTv(tv = event.tv)
                is TvEvent.ShowEpisodeDetail -> seriesRepository.showEpisodeDetail(episode = event.episode)
                is TvEvent.HideEpisodeDetail -> seriesRepository.hideEpisodeDetail()
                is TvEvent.SelectSeason -> seriesRepository.onSelectSeason(season = event.season)
            }
        }
    }

    @CircuitInject(screen = TvScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(value = "screen") screen: TvScreen
        ): TvPresenter
    }
}

data class TvInfo(
    val tv: Tv,
    val seasons: List<TvSeason>,
    val episodeState: TvSeasonLoadState,
    val episodesBySeason: Map<String, List<TvEpisode>>,
    val isFavorite: Boolean,
    val autoPlayTrailer: Boolean
)

data class TvUiState(
    val tv: TvState,
    val similarTvs: LazyPagingItems<SimilarMedia>,
    val selectedEpisode: TvEpisode?,
    val eventSink: (TvEvent) -> Unit
) : CircuitUiState

sealed interface TvEvent {
    object GoToBack : TvEvent
    object Restart : TvEvent
    data class GoToTv(val id: Int) : TvEvent
    data class GoToPeople(val id: Int) : TvEvent
    data class InsertTv(val tv: Tv) : TvEvent
    data class DeleteTv(val tv: Tv) : TvEvent
    data class ShowEpisodeDetail(val episode: TvEpisode) : TvEvent
    data object HideEpisodeDetail : TvEvent
    data class SelectSeason(val season: TvSeason) : TvEvent
}

sealed interface TvState {
    data object Loading : TvState
    data class Success(val tvInfo: TvInfo) : TvState
    data class Error(val message: String) : TvState
}