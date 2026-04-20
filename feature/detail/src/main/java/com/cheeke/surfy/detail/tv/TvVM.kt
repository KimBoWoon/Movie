package com.cheeke.surfy.detail.tv

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.util.trace
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
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
import com.cheeke.surfy.detail.tv.navigation.TvScreen
import com.cheeke.surfy.domain.GetTvDetailUseCase
import com.cheeke.surfy.domain.TvScreenData
import com.cheeke.surfy.domain.TvSeasonLoadState
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeason
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class TvRepository @Inject constructor(
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope,
    private val getTvDetailUseCase: GetTvDetailUseCase,
    private val databaseRepository: DatabaseRepository,
    private val pagingRepository: PagingRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val userDataRepository: UserDataRepository
) {
    companion object {
        private const val TAG = "TvRepository"
    }

    private val reload = MutableSharedFlow<Unit>(replay = 1)
    private val _selectedEpisode = MutableStateFlow<TvEpisode?>(value = null)
    val selectedEpisode = _selectedEpisode.asStateFlow()
    private val selectedSeason = MutableStateFlow<TvSeason?>(value = null)

    init {
        scope.launch {
            reload.emit(value = Unit)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTvData(id: Int): Flow<TvScreenData> {
        return reload.flatMapLatest {
            trace(sectionName = "GetTvDetail") { getTvDetailUseCase(id = id, selectedSeason = selectedSeason) }
        }
    }

    fun getTv(id: Int): Flow<TvState> {
        return combine(
            getTvData(id = id),
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
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSimilarTvs(id: Int): Flow<PagingData<SimilarMedia>> {
        return userDataRepository.internalData
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
                ).flow.cachedIn(scope = scope)
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
    @Assisted(value = "navigator") private val navigator: Navigator,
    @Assisted(value = "screen") private val screen: TvScreen,
    @Assisted(value = "goToTv") private val goToTv: (Int) -> Unit,
    @Assisted(value = "goToPeople") private val goToPeople: (Int) -> Unit,
    private val seriesRepository: TvRepository
) : Presenter<TvUiState> {
    @Composable
    override fun present(): TvUiState {
        val tv by produceRetainedState<TvState>(initialValue = TvState.Loading) {
            seriesRepository.getTv(id = screen.id).collect { tvState ->
                value = tvState
            }
        }
        val similarTvs = seriesRepository.getSimilarTvs(id = screen.id).collectAsLazyPagingItems()
        val selectedEpisode by seriesRepository.selectedEpisode.collectAsStateWithLifecycle()

        return TvUiState(
            tv = tv,
            similarTvs = similarTvs,
            selectedEpisode = selectedEpisode
        ) { event ->
            Log.d("HomePresenter", "$event")
            when (event) {
                is TvEvent.GoToTv -> goToTv(event.id)
                is TvEvent.GoToPeople -> goToPeople(event.id)
                is TvEvent.Restart -> seriesRepository.restart()
                is TvEvent.GoToBack -> navigator.pop()
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
            @Assisted(value = "navigator") navigator: Navigator,
            @Assisted(value = "screen") screen: TvScreen,
            @Assisted(value = "goToTv") goToTv: ((Int) -> Unit) = {},
            @Assisted(value = "goToPeople") goToPeople: ((Int) -> Unit) = {}
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

//@HiltViewModel(assistedFactory = TvVM.Factory::class)
//class TvVM @AssistedInject constructor(
//    @Assisted(value = "id") val id: Int,
//    private val getTvDetailUseCase: GetTvDetailUseCase,
//    private val databaseRepository: DatabaseRepository,
//    private val pagingRepository: PagingRepository,
//    private val analyticsHelper: AnalyticsHelper,
//    userDataRepository: UserDataRepository
//) : ViewModel() {
//    companion object {
//        private const val TAG = "TvVM"
//    }
//
//    @AssistedFactory
//    interface Factory {
//        fun create(
//            @Assisted(value = "id") id: Int
//        ): TvVM
//    }
//
//    private val reload = MutableSharedFlow<Unit>(replay = 1)
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val similarTvs = userDataRepository.internalData
//        .map { it.language to it.region }
//        .flatMapLatest {
//            Pager(
//                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
//                initialKey = 1,
//                pagingSourceFactory = {
//                    pagingRepository.getSimilarTvPagingSource(
//                        id = id,
//                        language = it.first,
//                        region = it.second
//                    )
//                }
//            ).flow.cachedIn(scope = viewModelScope)
//        }
//    private val _selectedEpisode = MutableStateFlow<TvEpisode?>(value = null)
//    val selectedEpisode = _selectedEpisode.asStateFlow()
//    private val selectedSeason = MutableStateFlow<TvSeason?>(value = null)
//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val tv = reload.flatMapLatest {
//        trace(sectionName = "GetTvDetail") { getTvDetailUseCase(id = id, selectedSeason = selectedSeason) }
//    }
//    val uiState: StateFlow<TvState> =
//        combine(
//            tv,
//            selectedSeason,
//        ) { twf, selectedSeason/*, seasonMap, episodeState*/ ->
//            val tv = twf.tv
//            val seasons = tv.seasons
//            val initialSeason = selectedSeason ?: seasons?.sortedBy { it.seasonNumber }?.firstOrNull()
//
//            if (selectedSeason == null && initialSeason != null) {
//                this@TvVM.selectedSeason.value = initialSeason
//            }
//
//            TvUiState(
//                tv = twf.tv,
//                seasons = seasons.orEmpty(),
//                episodeState = twf.seasonLoadState,
//                episodesBySeason = twf.episodesBySeason,
//                isFavorite = twf.isFavorite,
//                autoPlayTrailer = twf.autoPlayTrailer
//            )
//        }.asResult()
//            .map { result ->
//                when (result) {
//                    is Result.Loading -> TvState.Loading
//                    is Result.Success -> {
//                        analyticsHelper.logSelectContent(contentType = "tv", media = result.data.tv)
//                        TvState.Success(tvUiState = result.data)
//                    }
//                    is Result.Error -> TvState.Error(message = result.throwable.message ?: "something wrong...")
//                }
//            }.stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.Lazily,
//                initialValue = TvState.Loading
//            )
//
//    init {
//        viewModelScope.launch {
//            reload.emit(value = Unit)
//        }
//    }
//
//    fun onSelectSeason(season: TvSeason) {
//        viewModelScope.launch {
//            selectedSeason.emit(value = season)
//        }
//    }
//
//    fun restart() {
//        viewModelScope.launch {
//            reload.emit(value = Unit)
//        }
//    }
//
//    fun insertTv(tv: Tv) {
//        viewModelScope.launch {
//            databaseRepository.insertTv(tv = tv)
//        }
//    }
//
//    fun deleteTv(tv: Tv) {
//        viewModelScope.launch {
//            databaseRepository.deleteTv(tv = tv)
//        }
//    }
//
//    fun showEpisodeDetail(episode: TvEpisode) {
//        viewModelScope.launch {
//            _selectedEpisode.emit(value = episode)
//        }
//    }
//
//    fun hideEpisodeDetail() {
//        viewModelScope.launch {
//            _selectedEpisode.emit(value = null)
//        }
//    }
//}
//
//sealed interface TvState {
//    data object Loading : TvState
//    data class Success(val tvUiState: TvUiState) : TvState
//    data class Error(val message: String) : TvState
//}
//
//data class TvUiState(
//    val tv: Tv,
//    val seasons: List<TvSeason>,
//    val episodeState: TvSeasonLoadState,
//    val episodesBySeason: Map<String, List<TvEpisode>>,
//    val isFavorite: Boolean,
//    val autoPlayTrailer: Boolean
//)