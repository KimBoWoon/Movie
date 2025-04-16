package com.bowoon.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import com.bowoon.common.Log
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Genre
import com.bowoon.model.InternalData
import com.bowoon.model.Movie
import com.bowoon.model.SearchGroup
import com.bowoon.model.SearchType
import com.bowoon.search.navigation.Search
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class SearchPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted("goToMovie") private val goToMovie: ((Int) -> Unit),
    @Assisted("goToPeople") private val goToPeople: ((Int) -> Unit),
    @Assisted("goToSeries") private val goToSeries: ((Int) -> Unit),
    private val pagingRepository: PagingRepository,
    private val userDataRepository: UserDataRepository
) : Presenter<SearchUiState> {
    companion object {
        private const val TAG = "SearchVM"
    }

    var searchResult = MutableStateFlow<SearchState>(SearchState.Loading)

    @Composable
    override fun present(): SearchUiState {
        var searchType by remember { mutableStateOf<SearchType>(SearchType.MOVIE) }
        var selectedGenre by remember { mutableStateOf<Genre?>(null) }
        val internalData by produceRetainedState<InternalData?>(initialValue = null) {
            userDataRepository.internalData.collect { value = it }
        }

        return SearchUiState(
            searchType = searchType,
            selectedGenre = selectedGenre,
            searchState = searchResult
        ) { event ->
            when (event) {
                is SearchEvent.SelectedSearchType -> searchType = event.searchType
                is SearchEvent.SelectedGenre -> selectedGenre = if (selectedGenre == event.genre) null else event.genre
                is SearchEvent.Search -> {
                    Log.d(event.keyword)
                    event.keyword.trim().takeIf { it.isNotEmpty() }?.let { query ->
                        combine(
                            Pager(
                                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                                initialKey = 1,
                                pagingSourceFactory = {
                                    pagingRepository.searchMovieSource(
                                        type = searchType,
                                        query = query,
                                        language = "${internalData?.language}-${internalData?.region}",
                                        region = internalData?.region ?: "",
                                        isAdult = internalData?.isAdult ?: true
                                    )
                                }
                            ).flow,
                            flowOf(selectedGenre)
                        ) { pagingData, selectedGenre ->
                            selectedGenre?.let { genre ->
                                pagingData.filter { it is Movie && genre.id in (it.genreIds ?: emptyList()) }
                            } ?: pagingData
                        }.let {
                            searchResult.tryEmit(SearchState.Search(it))
                        }
                    } ?: searchResult.tryEmit(SearchState.InputKeyword)
                }
                is SearchEvent.GoToMovie -> goToMovie(event.id)
                is SearchEvent.GoToPeople -> goToPeople(event.id)
                is SearchEvent.GoToSeries -> goToSeries(event.id)
                else -> Log.d("$event")
            }
        }
    }

    @CircuitInject(Search::class, ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            navigator: Navigator,
            @Assisted("goToMovie") goToMovie: (Int) -> Unit = {},
            @Assisted("goToPeople") goToPeople: (Int) -> Unit = {},
            @Assisted("goToSeries") goToSeries: (Int) -> Unit = {}
        ): SearchPresenter
    }
}

data class SearchUiState(
    val searchType: SearchType,
    val selectedGenre: Genre?,
    val searchState: Flow<SearchState>,
    val eventSink: (SearchEvent) -> Unit
) : CircuitUiState

sealed interface SearchEvent : CircuitUiEvent {
    data class SelectedSearchType(val searchType: SearchType) : SearchEvent
    data class SelectedGenre(val genre: Genre?) : SearchEvent
    data class Search(val keyword: String) : SearchEvent
    data class GoToMovie(val id: Int) : SearchEvent
    data class GoToPeople(val id: Int) : SearchEvent
    data class GoToSeries(val id: Int) : SearchEvent
}

sealed interface SearchState {
    data object Loading : SearchState
    data class Search(val pagingData: Flow<PagingData<SearchGroup>>) : SearchState
    data class Error(val message: String) : SearchState
    data object InputKeyword : SearchState
}