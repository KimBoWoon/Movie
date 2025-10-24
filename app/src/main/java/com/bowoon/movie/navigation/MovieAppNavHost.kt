package com.bowoon.movie.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.bowoon.detail.DetailScreen
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.favorite.FavoriteScreen
import com.bowoon.favorite.navigation.FavoriteRoute
import com.bowoon.home.HomeScreen
import com.bowoon.home.navigation.HomeRoute
import com.bowoon.movie.MovieAppState
import com.bowoon.my.MyScreen
import com.bowoon.my.navigation.MyRoute
import com.bowoon.people.PeopleScreen
import com.bowoon.people.navigation.PeopleRoute
import com.bowoon.search.SearchScreen
import com.bowoon.search.navigation.SearchRoute
import com.bowoon.series.SeriesScreen
import com.bowoon.series.navigation.SeriesRoute

@Composable
fun MovieAppNavHost(
    modifier: Modifier,
    appState: MovieAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    NavDisplay(
        modifier = modifier,
        backStack = appState.backstack,
        onBack = { appState.backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            /**
             * main navigation
             */
            entry(key = HomeRoute) {
                HomeScreen(
                    goToMovie = { id -> appState.backstack.add(DetailRoute(id = id)) },
                    onShowSnackbar = onShowSnackbar
                )
            }
            entry(key = FavoriteRoute) {
                FavoriteScreen(
                    goToMovie = { id -> appState.backstack.add(DetailRoute(id = id)) },
                    goToPeople = { id -> appState.backstack.add(PeopleRoute(id = id)) },
                    onShowSnackbar = onShowSnackbar
                )
            }
            entry(key = MyRoute) {
                MyScreen()
            }

            /**
             * other screen
             */
            entry(key = SearchRoute) {
                SearchScreen(
                    goToMovie = { id -> appState.backstack.add(DetailRoute(id = id)) },
                    goToPeople = { id -> appState.backstack.add(PeopleRoute(id = id)) },
                    goToSeries = { id -> appState.backstack.add(SeriesRoute(id = id)) },
                    onShowSnackbar = onShowSnackbar
                )
            }
            entry<DetailRoute> {
                DetailScreen(
                    goToBack = { appState.backstack.removeLastOrNull() },
                    goToMovie = { id -> appState.backstack.add(DetailRoute(id = id)) },
                    goToPeople = { id -> appState.backstack.add(PeopleRoute(id = id)) },
                    onShowSnackbar = onShowSnackbar,
                )
            }
            entry<PeopleRoute> {
                PeopleScreen(
                    goToBack = { appState.backstack.removeLastOrNull() },
                    goToMovie = { id -> appState.backstack.add(DetailRoute(id = id)) },
                    onShowSnackbar = onShowSnackbar
                )
            }
            entry<SeriesRoute> {
                SeriesScreen(
                    goToBack = { appState.backstack.removeLastOrNull() },
                    goToMovie = { id -> appState.backstack.add(DetailRoute(id = id)) }
                )
            }
        }
    )
}