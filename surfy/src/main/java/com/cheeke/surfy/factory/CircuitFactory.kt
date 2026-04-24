package com.cheeke.surfy.factory

import com.cheeke.surfy.detail.movie.MoviePresenter
import com.cheeke.surfy.detail.movie.MovieScreen
import com.cheeke.surfy.detail.movie.MovieUiState
import com.cheeke.surfy.detail.movie.navigation.MovieScreen
import com.cheeke.surfy.detail.movie.navigation.goToMovie
import com.cheeke.surfy.detail.people.PeoplePresenter
import com.cheeke.surfy.detail.people.PeopleScreen
import com.cheeke.surfy.detail.people.PeopleUiState
import com.cheeke.surfy.detail.people.navigation.PeopleScreen
import com.cheeke.surfy.detail.people.navigation.goToPeople
import com.cheeke.surfy.detail.series.SeriesPresenter
import com.cheeke.surfy.detail.series.SeriesScreen
import com.cheeke.surfy.detail.series.SeriesUiState
import com.cheeke.surfy.detail.series.navigation.SeriesScreen
import com.cheeke.surfy.detail.series.navigation.goToSeries
import com.cheeke.surfy.detail.tv.TvPresenter
import com.cheeke.surfy.detail.tv.TvScreen
import com.cheeke.surfy.detail.tv.TvUiState
import com.cheeke.surfy.detail.tv.navigation.TvScreen
import com.cheeke.surfy.detail.tv.navigation.goToTv
import com.cheeke.surfy.favorite.FavoritePresenter
import com.cheeke.surfy.favorite.FavoriteScreen
import com.cheeke.surfy.favorite.FavoriteUiState
import com.cheeke.surfy.favorite.navigation.FavoriteScreen
import com.cheeke.surfy.home.HomePresenter
import com.cheeke.surfy.home.HomeScreen
import com.cheeke.surfy.home.HomeState
import com.cheeke.surfy.home.navigation.HomeScreen
import com.cheeke.surfy.search.SearchPresenter
import com.cheeke.surfy.search.SearchScreen
import com.cheeke.surfy.search.SearchUiState
import com.cheeke.surfy.search.navigation.SearchScreen
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
        is HomeScreen -> ui<HomeState> { state, modifier -> HomeScreen(modifier = modifier, homeState = state) }
        is SearchScreen -> ui<SearchUiState> { state, modifier -> SearchScreen(modifier = modifier, searchUiState = state) }
        is FavoriteScreen -> ui<FavoriteUiState> { state, modifier -> FavoriteScreen(modifier = modifier, favoriteUiState = state) }
        is MovieScreen -> ui<MovieUiState> { state, modifier -> MovieScreen(modifier = modifier, movieUiState = state) }
        is PeopleScreen -> ui<PeopleUiState> { state, modifier -> PeopleScreen(modifier = modifier, peopleUiState = state) }
        is SeriesScreen -> ui<SeriesUiState> { state, modifier -> SeriesScreen(modifier = modifier, seriesUiState = state) }
        is TvScreen -> ui<TvUiState> { state, modifier -> TvScreen(modifier = modifier, tvUiState = state) }
        else -> null
    }
}

class SurfyPresenterFactory @Inject constructor(
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
        is HomeScreen -> homePresenterFactory.create(goToMovie = navigator::goToMovie, goToPeople = navigator::goToPeople, goToTv = navigator::goToTv)
        is SearchScreen -> searchPresenterFactory.create(screen = screen, goToMovie = navigator::goToMovie, goToPeople = navigator::goToPeople, goToSeries = navigator::goToSeries, goToTv = navigator::goToTv)
        is FavoriteScreen -> favoritePresenterFactory.create(screen = screen, goToMovie = navigator::goToMovie, goToPeople = navigator::goToPeople, goToTv = navigator::goToTv)
        is MovieScreen -> moviePresenterFactory.create(navigator = navigator, screen = screen)
        is PeopleScreen -> peoplePresenterFactory.create(navigator = navigator, screen = screen)
        is SeriesScreen -> seriesPresenterFactory.create(navigator = navigator, screen = screen)
        is TvScreen -> tvPresenterFactory.create(navigator = navigator, screen = screen)
        else -> null
    }
}