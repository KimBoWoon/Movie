package com.cheeke.surfy.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.cheeke.surfy.search.navigation.SearchScreen
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class SearchRepository @Inject constructor(
    @param:ActivityRetainedScopeCoroutine private val scope: CoroutineScope,
    private val dataManager: DataManager,
    private val pagingRepository: PagingRepository
) {
    companion object {
        private const val TAG = "SearchRepository"
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
        private const val GENRE = "genre"
        private const val SEARCH_TYPE = "searchType"
    }

//    private val _query = MutableStateFlow(value = TextFieldValue(text = initialQuery))
//    val query = _query.asStateFlow()
//    val selectedGenre = savedStateHandle.getStateFlow<Genre?>(key = GENRE, initialValue = null)
//    val searchType = savedStateHandle.getStateFlow<SearchType>(key = SEARCH_TYPE, initialValue = initialSearchType)
//    val showSnackbar = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
//    private val recommendKeywordFlow = MutableStateFlow<String>(value = "")
//    val surfyAppData = dataManager.surfyAppData
//        .map { it.getMovieAppData() }
//        .stateIn(
//            scope = scope,
//            started = SharingStarted.WhileSubscribed(),
//            initialValue = SurfyAppData()
//        )
//    val recommendKeywordPaging = recommendKeywordFlow
//        .debounce(timeoutMillis = 300)
//        .distinctUntilChanged()
//        .flatMapLatest { query ->
//            if (query.trim().isEmpty()) {
//                flowOf(value = PagingData.empty())
//            } else {
//                Pager(
//                    config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
//                    initialKey = 1,
//                    pagingSourceFactory = { pagingRepository.getRecommendKeywordPagingSource(query = query) }
//                ).flow
//            }
//        }.cachedIn(scope = scope)
//    private val searchTrigger: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 0, extraBufferCapacity = 1)
//    val searchResult: StateFlow<SearchState> = merge(
//        searchType.map { SearchState.SearchHint },
//        searchTrigger
//            .onStart {
//                if (initialQuery.trim().isNotEmpty()) emit(value = Unit)
//            }
//            .map { _: Unit ->
//                val currentQuery: String = query.value.text.trim()
//
//                if (currentQuery.isEmpty()) {
//                    showSnackbar.emit(value = Unit)
//                }
//
//                currentQuery
//            }.filter { currentQuery: String ->
//                currentQuery.isNotEmpty()
//            }
//            .flatMapLatest { currentQuery: String ->
//                flow<SearchState> {
//                    emit(
//                        value = SearchState.Success(
//                            pagingData = combine(
//                                Pager(
//                                    config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
//                                    initialKey = 1,
//                                    pagingSourceFactory = {
//                                        pagingRepository.getSearchPagingSource(
//                                            type = searchType.value,
//                                            query = currentQuery,
//                                            language = surfyAppData.value.language.find { it.isSelected }?.code.orEmpty(),
//                                            region = surfyAppData.value.region.find { it.isSelected }?.code.orEmpty(),
//                                            isAdult = surfyAppData.value.isAdult
//                                        )
//                                    }
//                                ).flow.cachedIn(scope = scope),
//                                selectedGenre
//                            ) { pagingData: PagingData<Media>, genre: Genre? ->
//                                if (genre != null) {
//                                    pagingData.filter { media: Media ->
//                                        genre.id in (media.genres?.map { it.id } ?: emptyList())
//                                    }
//                                } else {
//                                    pagingData
//                                }
//                            }
//                        )
//                    )
//                }.catch { throwable: Throwable ->
//                    emit(value = SearchState.Error(throwable = throwable))
//                }
//            }
//    ).stateIn(
//        scope = scope,
//        started = SharingStarted.Lazily,
//        initialValue = SearchState.SearchHint
//    )
//
//    fun updateGenre(genre: Genre?) {
//        savedStateHandle[GENRE] = if (genre == selectedGenre.value) null else genre
//    }
//
//    fun updateQuery(value: TextFieldValue) {
//        _query.value = value
//        scope.launch { recommendKeywordFlow.emit(value = value.text) }
//    }
//
//    fun updateSearchType(searchType: SearchType) {
//        savedStateHandle[SEARCH_TYPE] = searchType
//    }
//
//    fun searchMovies() {
//        analyticsHelper.logSearch(searchType = searchType.value.label, query = query.value.text)
//        scope.launch { searchTrigger.emit(value = Unit) }
//    }

    val surfyAppData = dataManager.surfyAppData
        .map { it.getMovieAppData() }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SurfyAppData()
        )

    fun searchMovies(
        query: String,
        searchType: SearchType,
        language: String,
        region: String,
        isAdult: Boolean
    ): Flow<PagingData<Media>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                pagingRepository.getSearchPagingSource(
                    type = searchType,
                    query = query,
                    language = language,
                    region = region,
                    isAdult = isAdult
                )
            }
        ).flow.cachedIn(scope)
    }

    // 🔥 추천 검색어
    fun recommendKeywords(query: String): Flow<PagingData<SearchKeyword>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                pagingRepository.getRecommendKeywordPagingSource(query)
            }
        ).flow.cachedIn(scope)
    }
}

class SearchPresenter @AssistedInject constructor(
    @Assisted private val screen: SearchScreen,
    @Assisted(value = "goToMovie") private val goToMovie: (Int) -> Unit,
    @Assisted(value = "goToPeople") private val goToPeople: (Int) -> Unit,
    @Assisted(value = "goToTv") private val goToTv: (Int) -> Unit,
    @Assisted(value = "goToSeries") private val goToSeries: ((Int) -> Unit) = {},
    private val searchRepository: SearchRepository,
    private val analyticsHelper: AnalyticsHelper
) : Presenter<SearchUiState> {
    @Composable
    override fun present(): SearchUiState {
        var query by rememberSaveable(
            stateSaver = TextFieldValue.Saver
        ) {
            mutableStateOf(TextFieldValue(screen.query))
        }
        var searchType by rememberSaveable {
            mutableStateOf(value = screen.searchType)
        }
        var genre by rememberSaveable {
            mutableStateOf<Genre?>(value = null)
        }
        val surfyAppData by searchRepository.surfyAppData.collectAsStateWithLifecycle()
        val scope = rememberCoroutineScope()

        // 🔥 SearchState
        val searchStateFlow = remember {
            MutableStateFlow<SearchState>(SearchState.SearchHint)
        }

        // 🔥 추천 검색어 Flow
        val recommendFlow = remember {
            snapshotFlow { query.text }
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { q ->
                    if (q.isBlank()) {
                        flowOf(value = PagingData.empty())
                    } else {
                        searchRepository.recommendKeywords(q)
                    }
                }
        }

        val recommendItems = recommendFlow.collectAsLazyPagingItems()

        // 🔥 검색 실행
        fun search() {
            val q = query.text.trim()

            if (q.isEmpty()) {
                searchStateFlow.value = SearchState.SearchHint
                return
            }

            scope.launch {
                try {
                    val pagingFlow = searchRepository.searchMovies(
                        query = q,
                        searchType = searchType,
                        language = surfyAppData.language.find { it.isSelected }?.code.orEmpty(),
                        region = surfyAppData.region.find { it.isSelected }?.code.orEmpty(),
                        isAdult = surfyAppData.isAdult
                    ).map { pagingData ->
                        if (genre != null) {
                            pagingData.filter { media ->
                                genre!!.id in (media.genres?.map { it.id } ?: emptyList())
                            }
                        } else pagingData
                    }

                    searchStateFlow.value = SearchState.Success(pagingData = pagingFlow)

                } catch (e: Throwable) {
                    searchStateFlow.value = SearchState.Error(throwable = e)
                }
            }
        }

        val searchState by searchStateFlow.collectAsState()

        return SearchUiState(
            surfyAppData = surfyAppData,
            searchState = searchState,
            query = query,
            searchType = searchType,
            genre = genre,
            recommendItems = recommendItems
        ) { event ->
            when (event) {
                is SearchEvent.UpdateQuery -> query = event.query
                is SearchEvent.UpdateSearchType -> searchType = event.searchType
                is SearchEvent.UpdateGenre -> genre = event.genre
                is SearchEvent.ClearQuery -> {
                    query = TextFieldValue("")
                    searchStateFlow.value = SearchState.SearchHint
                }
                is SearchEvent.ClickRecommendKeyword -> {
                    query = TextFieldValue(event.keyword)
                    analyticsHelper.logSearch(searchType = searchType.label, query = event.keyword)
                    search()
                }
                SearchEvent.SearchMovies -> {
                    analyticsHelper.logSearch(searchType = searchType.label, query = query.text)
                    search()
                }
                is SearchEvent.GoToMovie -> goToMovie(event.id)
                is SearchEvent.GoToPeople -> goToPeople(event.id)
                is SearchEvent.GoToTv -> goToTv(event.id)
                is SearchEvent.GoToSeries -> goToSeries(event.id)
            }
        }
    }

    @CircuitInject(SearchScreen::class, ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted screen: SearchScreen,
            @Assisted(value = "goToMovie") goToMovie: ((Int) -> Unit) = {},
            @Assisted(value = "goToPeople") goToPeople: ((Int) -> Unit) = {},
            @Assisted(value = "goToTv") goToTv: ((Int) -> Unit) = {},
            @Assisted(value = "goToSeries") goToSeries: ((Int) -> Unit) = {}
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

//@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
//@HiltViewModel(assistedFactory = SearchVM.Factory::class)
//class SearchVM @AssistedInject constructor(
//    @Assisted initialQuery: String,
//    @Assisted initialSearchType: SearchType,
//    dataManager: DataManager,
//    private val savedStateHandle: SavedStateHandle,
//    private val pagingRepository: PagingRepository,
//    private val analyticsHelper: AnalyticsHelper
//) : ViewModel() {
//    companion object {
//        internal const val TAG = "SearchVM"
//        private const val GENRE = "genre"
//        private const val SEARCH_TYPE = "searchType"
//    }
//
//    @AssistedFactory
//    interface Factory {
//        fun create(
//            initialQuery: String,
//            initialSearchType: SearchType
//        ): SearchVM
//    }
//
//    private val _query = MutableStateFlow(value = TextFieldValue(text = initialQuery))
//    val query = _query.asStateFlow()
//    val selectedGenre = savedStateHandle.getStateFlow<Genre?>(key = GENRE, initialValue = null)
//    val searchType = savedStateHandle.getStateFlow<SearchType>(key = SEARCH_TYPE, initialValue = initialSearchType)
//    val showSnackbar = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
//    private val recommendKeywordFlow = MutableStateFlow<String>(value = "")
//    val surfyAppData = dataManager.surfyAppData
//        .map { it.getMovieAppData() }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(),
//            initialValue = SurfyAppData()
//        )
//    val recommendKeywordPaging = recommendKeywordFlow
//        .debounce(timeoutMillis = 300)
//        .distinctUntilChanged()
//        .flatMapLatest { query ->
//            if (query.trim().isEmpty()) {
//                flowOf(value = PagingData.empty())
//            } else {
//                Pager(
//                    config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
//                    initialKey = 1,
//                    pagingSourceFactory = { pagingRepository.getRecommendKeywordPagingSource(query = query) }
//                ).flow
//            }
//        }.cachedIn(scope = viewModelScope)
//    private val searchTrigger: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 0, extraBufferCapacity = 1)
//    val searchResult: StateFlow<SearchUiState> = merge(
//        searchType.map { SearchUiState.SearchHint },
//        searchTrigger
//            .onStart {
//                if (initialQuery.trim().isNotEmpty()) emit(Unit)
//            }
//            .map { _: Unit ->
//                val currentQuery: String = query.value.text.trim()
//
//                if (currentQuery.isEmpty()) {
//                    showSnackbar.emit(value = Unit)
//                }
//
//                currentQuery
//            }.filter { currentQuery: String ->
//                currentQuery.isNotEmpty()
//            }
//            .flatMapLatest { currentQuery: String ->
//                flow<SearchUiState> {
//                    emit(
//                        value = SearchUiState.Success(
//                            pagingData = combine(
//                                Pager(
//                                    config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
//                                    initialKey = 1,
//                                    pagingSourceFactory = {
//                                        pagingRepository.getSearchPagingSource(
//                                            type = searchType.value,
//                                            query = currentQuery,
//                                            language = surfyAppData.value.language.find { it.isSelected }?.code.orEmpty(),
//                                            region = surfyAppData.value.region.find { it.isSelected }?.code.orEmpty(),
//                                            isAdult = surfyAppData.value.isAdult
//                                        )
//                                    }
//                                ).flow.cachedIn(scope = viewModelScope),
//                                selectedGenre
//                            ) { pagingData: PagingData<Media>, genre: Genre? ->
//                                if (genre != null) {
//                                    pagingData.filter { media: Media ->
//                                        genre.id in (media.genres?.map { it.id } ?: emptyList())
//                                    }
//                                } else {
//                                    pagingData
//                                }
//                            }
//                        )
//                    )
//                }.catch { throwable: Throwable ->
//                    emit(value = SearchUiState.Error(throwable = throwable))
//                }
//            }
//        ).stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.Lazily,
//            initialValue = SearchUiState.SearchHint
//        )
//
//    fun updateGenre(genre: Genre?) {
//        savedStateHandle[GENRE] = if (genre == selectedGenre.value) null else genre
//    }
//
//    fun updateQuery(value: TextFieldValue) {
//        _query.value = value
//        viewModelScope.launch { recommendKeywordFlow.emit(value = value.text) }
//    }
//
//    fun updateSearchType(searchType: SearchType) {
//        savedStateHandle[SEARCH_TYPE] = searchType
//    }
//
//    fun searchMovies() {
//        analyticsHelper.logSearch(searchType = searchType.value.label, query = query.value.text)
//        viewModelScope.launch { searchTrigger.emit(value = Unit) }
//    }
//}
//
//sealed interface SearchUiState {
//    data object SearchHint : SearchUiState
//    data class Success(val pagingData: Flow<PagingData<Media>>) : SearchUiState
//    data class Error(val throwable: Throwable) : SearchUiState
//}