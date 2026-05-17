package com.cheeke.surfy.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.filter
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSearch
import com.cheeke.surfy.common.di.ActivityRetainedScopeCoroutine
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.util.DataManager
import com.cheeke.surfy.model.Genre
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.SearchKeyword
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.model.SurfyAppData
import com.cheeke.surfy.navigation.SearchScreen
import com.cheeke.surfy.navigation.goToMovie
import com.cheeke.surfy.navigation.goToPeople
import com.cheeke.surfy.navigation.goToSeries
import com.cheeke.surfy.navigation.goToTv
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchRepository @AssistedInject constructor(
    @Assisted private val initialQuery: String,
    @Assisted private val initialSearchType: SearchType,
    @Assisted private val genre: Genre?,
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope,
    private val dataManager: DataManager,
    private val pagingRepository: PagingRepository,
    private val analyticsHelper: AnalyticsHelper
) {
    @AssistedFactory
    interface Factory {
        fun create(
            initialQuery: String,
            initialSearchType: SearchType,
            genre: Genre?
        ): SearchRepository
    }

    companion object {
        private const val TAG = "SearchRepository"
    }

    private val _query = MutableStateFlow(value = TextFieldValue(text = initialQuery))
    val query = _query.asStateFlow()
    var selectedGenre = MutableStateFlow(value = genre)
    val searchType = MutableStateFlow(value = initialSearchType)
    val showSnackbar = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val recommendKeywordFlow = MutableStateFlow<String>(value = "")
    val surfyAppData = dataManager.surfyAppData
        .map { it.getMovieAppData() }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SurfyAppData()
        )
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val recommendKeywordPaging = recommendKeywordFlow
        .debounce(timeoutMillis = 300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.trim().isEmpty()) {
                flowOf(value = PagingData.empty())
            } else {
                Pager(
                    config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
                    initialKey = 1,
                    pagingSourceFactory = { pagingRepository.getRecommendKeywordPagingSource(query = query) }
                ).flow
            }
        }.cachedIn(scope = scope)
    private val searchTrigger: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 0, extraBufferCapacity = 1)
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResult = merge(
        searchType.map { SearchState.SearchHint },
        searchTrigger
            .onStart {
                if (query.value.text.trim().isNotEmpty()) emit(value = Unit)
            }.map {
                val currentQuery = query.value.text.trim()

                if (currentQuery.isEmpty()) {
                    showSnackbar.emit(value = Unit)
                }

                currentQuery
            }.filter { currentQuery: String ->
                currentQuery.isNotEmpty()
            }.flatMapLatest { currentQuery: String ->
                flow<SearchState> {
                    emit(
                        value = SearchState.Success(
                            pagingData = combine(
                                Pager(
                                    config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
                                    initialKey = 1,
                                    pagingSourceFactory = {
                                        pagingRepository.getSearchPagingSource(
                                            type = searchType.value,
                                            query = currentQuery,
                                            language = surfyAppData.value.language.find { it.isSelected }?.code.orEmpty(),
                                            region = surfyAppData.value.region.find { it.isSelected }?.code.orEmpty(),
                                            isAdult = surfyAppData.value.isAdult
                                        )
                                    }
                                ).flow.cachedIn(scope = scope),
                                selectedGenre
                            ) { pagingData: PagingData<Media>, genre: Genre? ->
                                if (genre != null) {
                                    pagingData.filter { media: Media ->
                                        genre.id in (media.genres?.map { it.id } ?: emptyList())
                                    }
                                } else {
                                    pagingData
                                }
                            }
                        )
                    )
                }.catch { throwable: Throwable ->
                    emit(value = SearchState.Error(throwable = throwable))
                }
            }
    ).stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = SearchState.SearchHint
    )

    fun updateGenre(genre: Genre?) {
        scope.launch {
            selectedGenre.emit(value = if (genre == selectedGenre) null else genre)
        }
    }

    fun updateQuery(value: TextFieldValue) {
        _query.value = value
        scope.launch { recommendKeywordFlow.emit(value = value.text) }
    }

    fun updateSearchType(searchType: SearchType) {
        scope.launch {
            this@SearchRepository.searchType.emit(value = searchType)
        }
    }

    fun search() {
        analyticsHelper.logSearch(searchType = searchType.value.label, query = query.value.text)
        scope.launch { searchTrigger.emit(value = Unit) }
    }
}

class SearchPresenter @AssistedInject constructor(
    @Assisted private val screen: SearchScreen,
    @Assisted private val navigator: Navigator,
    private val searchRepositoryFactory: SearchRepository.Factory,
    private val analyticsHelper: AnalyticsHelper
) : Presenter<SearchUiState> {
    @Composable
    override fun present(): SearchUiState {
        val searchRepository = rememberRetained(screen) {
            searchRepositoryFactory.create(initialQuery = screen.query, initialSearchType = screen.searchType, genre = null)
        }
        val surfyAppData by searchRepository.surfyAppData.collectAsStateWithLifecycle()
        val searchState by searchRepository.searchResult.collectAsStateWithLifecycle()
        val query by searchRepository.query.collectAsStateWithLifecycle()
        val searchType by searchRepository.searchType.collectAsStateWithLifecycle()
        val genre by searchRepository.selectedGenre.collectAsStateWithLifecycle()
        val recommendItems = searchRepository.recommendKeywordPaging.collectAsLazyPagingItems()

        return SearchUiState(
            surfyAppData = surfyAppData,
            searchState = searchState,
            query = query,
            searchType = searchType,
            genre = genre,
            recommendItems = recommendItems
        ) { event ->
            when (event) {
                is SearchEvent.UpdateQuery -> searchRepository.updateQuery(value = event.query)
                is SearchEvent.UpdateSearchType -> searchRepository.updateSearchType(searchType = event.searchType)
                is SearchEvent.UpdateGenre -> searchRepository.updateGenre(genre = event.genre)
                is SearchEvent.ClearQuery -> {
                    searchRepository.updateQuery(value = TextFieldValue(text = ""))
                }
                is SearchEvent.ClickRecommendKeyword -> {
                    searchRepository.updateQuery(value = TextFieldValue(text = event.keyword))
                    analyticsHelper.logSearch(searchType = searchType.label, query = event.keyword)
                    searchRepository.search()
                }
                SearchEvent.SearchMovies -> {
                    analyticsHelper.logSearch(searchType = searchType.label, query = query.text)
                    searchRepository.search()
                }
                is SearchEvent.GoToMovie -> navigator.goToMovie(id = event.id)
                is SearchEvent.GoToPeople -> navigator.goToPeople(id = event.id)
                is SearchEvent.GoToTv -> navigator.goToTv(id = event.id)
                is SearchEvent.GoToSeries -> navigator.goToSeries(id = event.id)
            }
        }
    }

    @CircuitInject(screen = SearchScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: SearchScreen,
            navigator: Navigator
        ): SearchPresenter
    }
}

data class SearchUiState(
    val surfyAppData: SurfyAppData,
    val searchState: SearchState,
    val query: TextFieldValue,
    val searchType: SearchType,
    val genre: Genre?,
    val recommendItems: LazyPagingItems<SearchKeyword>,
    val eventSink: (SearchEvent) -> Unit
) : CircuitUiState

sealed interface SearchEvent {
    data class GoToMovie(val id: Int) : SearchEvent
    data class GoToPeople(val id: Int) : SearchEvent
    data class GoToTv(val id: Int) : SearchEvent
    data class GoToSeries(val id: Int) : SearchEvent
    data class UpdateQuery(val query: TextFieldValue) : SearchEvent
    data class UpdateSearchType(val searchType: SearchType) : SearchEvent
    data class UpdateGenre(val genre: Genre?) : SearchEvent
    object SearchMovies : SearchEvent
    object ClearQuery : SearchEvent
    data class ClickRecommendKeyword(val keyword: String) : SearchEvent
}

sealed interface SearchState {
    data object SearchHint : SearchState
    data class Success(val pagingData: Flow<PagingData<Media>>) : SearchState
    data class Error(val throwable: Throwable) : SearchState
}