package com.cheeke.surfy.factory

import com.cheeke.surfy.detail.movie.MoviePresenter
import com.cheeke.surfy.detail.movie.MovieScreen
import com.cheeke.surfy.detail.movie.MovieUiState
import com.cheeke.surfy.detail.people.PeoplePresenter
import com.cheeke.surfy.detail.people.PeopleScreen
import com.cheeke.surfy.detail.people.PeopleUiState
import com.cheeke.surfy.detail.series.SeriesPresenter
import com.cheeke.surfy.detail.series.SeriesScreen
import com.cheeke.surfy.detail.series.SeriesUiState
import com.cheeke.surfy.detail.tv.TvPresenter
import com.cheeke.surfy.detail.tv.TvScreen
import com.cheeke.surfy.detail.tv.TvUiState
import com.cheeke.surfy.favorite.FavoritePresenter
import com.cheeke.surfy.favorite.FavoriteScreen
import com.cheeke.surfy.favorite.FavoriteState
import com.cheeke.surfy.home.HomePresenter
import com.cheeke.surfy.home.HomeScreen
import com.cheeke.surfy.home.HomeState
import com.cheeke.surfy.navigation.FavoriteScreen
import com.cheeke.surfy.navigation.HomeScreen
import com.cheeke.surfy.navigation.MovieScreen
import com.cheeke.surfy.navigation.PeopleScreen
import com.cheeke.surfy.navigation.RootScreen
import com.cheeke.surfy.navigation.SearchScreen
import com.cheeke.surfy.navigation.SeriesScreen
import com.cheeke.surfy.navigation.TvScreen
import com.cheeke.surfy.search.SearchPresenter
import com.cheeke.surfy.search.SearchScreen
import com.cheeke.surfy.search.SearchUiState
import com.cheeke.surfy.ui.root.RootPresenter
import com.cheeke.surfy.ui.root.RootScreen
import com.cheeke.surfy.ui.root.RootState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import javax.inject.Inject

class SurfyScreenFactory @Inject constructor(

) : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is RootScreen -> ui<RootState> { state, modifier -> RootScreen(modifier = modifier, rootState = state) }
        is HomeScreen -> ui<HomeState> { state, modifier -> HomeScreen(modifier = modifier, homeState = state) }
        is SearchScreen -> ui<SearchUiState> { state, modifier -> SearchScreen(modifier = modifier, searchUiState = state) }
        is FavoriteScreen -> ui<FavoriteState> { state, modifier -> FavoriteScreen(modifier = modifier, favoriteState = state) }
        is MovieScreen -> ui<MovieUiState> { state, modifier -> MovieScreen(modifier = modifier, movieUiState = state) }
        is PeopleScreen -> ui<PeopleUiState> { state, modifier -> PeopleScreen(modifier = modifier, peopleUiState = state) }
        is SeriesScreen -> ui<SeriesUiState> { state, modifier -> SeriesScreen(modifier = modifier, seriesUiState = state) }
        is TvScreen -> ui<TvUiState> { state, modifier -> TvScreen(modifier = modifier, tvUiState = state) }
        else -> null
    }
}

class SurfyPresenterFactory @Inject constructor(
    private val rootPresenterFactory: RootPresenter.Factory,
    private val homePresenterFactory: HomePresenter.Factory,
    private val searchPresenterFactory: SearchPresenter.Factory,
    private val favoritePresenterFactory: FavoritePresenter.Factory,
    private val moviePresenterFactory: MoviePresenter.Factory,
    private val peoplePresenterFactory: PeoplePresenter.Factory,
    private val seriesPresenterFactory: SeriesPresenter.Factory,
    private val tvPresenterFactory: TvPresenter.Factory
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext
    ): Presenter<*>? = when (screen) {
        is RootScreen -> rootPresenterFactory.create()
        is HomeScreen -> homePresenterFactory.create()
        is SearchScreen -> searchPresenterFactory.create(screen = screen, navigator = navigator)
        is FavoriteScreen -> favoritePresenterFactory.create()
        is MovieScreen -> moviePresenterFactory.create(screen = screen, navigator = navigator)
        is PeopleScreen -> peoplePresenterFactory.create(screen = screen, navigator = navigator)
        is SeriesScreen -> seriesPresenterFactory.create(screen = screen, navigator = navigator)
        is TvScreen -> tvPresenterFactory.create(screen = screen, navigator = navigator)
        else -> null
    }
}