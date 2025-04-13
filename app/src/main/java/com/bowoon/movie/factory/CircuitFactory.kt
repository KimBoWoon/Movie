package com.bowoon.movie.factory

import com.bowoon.detail.DetailPresenter
import com.bowoon.detail.DetailScreen
import com.bowoon.detail.DetailState
import com.bowoon.detail.navigation.Detail
import com.bowoon.detail.navigation.goToMovie
import com.bowoon.favorite.FavoritePresenter
import com.bowoon.favorite.FavoriteScreen
import com.bowoon.favorite.FavoriteUiState
import com.bowoon.favorite.navigation.Favorite
import com.bowoon.home.HomePresenter
import com.bowoon.home.HomeScreen
import com.bowoon.home.HomeUiState
import com.bowoon.home.navigation.Home
import com.bowoon.my.MyPresenter
import com.bowoon.my.MyScreen
import com.bowoon.my.MyUiState
import com.bowoon.my.navigation.My
import com.bowoon.people.PeoplePresenter
import com.bowoon.people.PeopleScreen
import com.bowoon.people.PeopleUiState
import com.bowoon.people.navigation.People
import com.bowoon.people.navigation.goToPeople
import com.bowoon.search.SearchPresenter
import com.bowoon.search.SearchScreen
import com.bowoon.search.SearchUiState
import com.bowoon.search.navigation.Search
import com.bowoon.series.SeriesPresenter
import com.bowoon.series.SeriesScreen
import com.bowoon.series.SeriesUiState
import com.bowoon.series.navigation.Series
import com.bowoon.series.navigation.goToSeries
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import javax.inject.Inject

class ScreenFactory @Inject constructor(

) : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
        is Home -> ui<HomeUiState> { state, _ -> HomeScreen(state = state) }
        is Search -> ui<SearchUiState> { state, _ -> SearchScreen(state = state) }
        is Favorite -> ui<FavoriteUiState> { state, _ -> FavoriteScreen(state = state) }
        is My -> ui<MyUiState> { state, _ -> MyScreen(state = state) }
        is Detail -> ui<DetailState> { state, _ -> DetailScreen(state = state) }
        is People -> ui<PeopleUiState> { state, _ -> PeopleScreen(state = state) }
        is Series -> ui<SeriesUiState> { state, _ -> SeriesScreen(state = state) }
        else -> null
    }
}

class PresenterFactory @Inject constructor(
    private val homePresenterFactory: HomePresenter.Factory,
    private val searchPresenterFactory: SearchPresenter.Factory,
    private val favoritePresenterFactory: FavoritePresenter.Factory,
    private val myPresenter: MyPresenter,
    private val detailPresenterFactory: DetailPresenter.Factory,
    private val peoplePresenterFactory: PeoplePresenter.Factory,
    private val seriesPresenterFactory: SeriesPresenter.Factory
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext
    ): Presenter<*>? = when (screen) {
        is Home -> homePresenterFactory.create(navigator = navigator, goToMovie = navigator::goToMovie)
        is Search -> searchPresenterFactory.create(navigator = navigator, goToMovie = navigator::goToMovie, goToPeople = navigator::goToPeople, goToSeries = navigator::goToSeries)
        is Favorite -> favoritePresenterFactory.create(navigator = navigator, goToMovie = navigator::goToMovie, goToPeople = navigator::goToPeople)
        is My -> myPresenter
        is Detail -> detailPresenterFactory.create(navigator = navigator, screen = screen, goToMovie = navigator::goToMovie, goToPeople = navigator::goToPeople)
        is People -> peoplePresenterFactory.create(navigator = navigator, screen = screen, goToMovie = navigator::goToMovie)
        is Series -> seriesPresenterFactory.create(navigator = navigator, screen = screen, goToMovie = navigator::goToMovie)
        else -> null
    }
}