package com.bowoon.movie.navigation

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.bowoon.detail.DetailScreen
import com.bowoon.detail.DetailVM
import com.bowoon.favorite.FavoriteScreen
import com.bowoon.favorite.FavoriteVM
import com.bowoon.home.HomeScreen
import com.bowoon.home.HomeVM
import com.bowoon.movie.MovieAppState
import com.bowoon.my.MyScreen
import com.bowoon.my.MyVM
import com.bowoon.people.PeopleScreen
import com.bowoon.people.PeopleVM
import com.bowoon.search.SearchScreen
import com.bowoon.search.SearchVM
import com.bowoon.series.SeriesScreen
import com.bowoon.series.SeriesVM
import kotlinx.serialization.Serializable

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
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(), // Add this line
//            rememberSavedStateNavEntryDecorator() // Optional, but recommended
        ),
        entryProvider = entryProvider {
            /**
             * main navigation
             */
            entry(key = Screen.Home) {
                val viewModel: HomeVM = hiltViewModel()

                HomeScreen(
                    goToMovie = { id -> appState.backstack.add(Screen.MovieDetail(id = id)) },
                    onShowSnackbar = onShowSnackbar,
                    viewModel = viewModel
                )
            }
            entry(key = Screen.Favorite) {
                val viewModel: FavoriteVM = hiltViewModel()

                FavoriteScreen(
                    goToMovie = { id -> appState.backstack.add(Screen.MovieDetail(id = id)) },
                    goToPeople = { id -> appState.backstack.add(Screen.PeopleDetail(id = id)) },
                    onShowSnackbar = onShowSnackbar,
                    viewModel = viewModel
                )
            }
            entry(key = Screen.My) {
                val viewModel: MyVM = hiltViewModel()

                MyScreen(viewModel = viewModel)
            }

            /**
             * other screen
             */
            entry(key = Screen.Search) {
                val viewModel: SearchVM = hiltViewModel()

                SearchScreen(
                    goToMovie = { id -> appState.backstack.add(Screen.MovieDetail(id = id)) },
                    goToPeople = { id -> appState.backstack.add(Screen.PeopleDetail(id = id)) },
                    goToSeries = { id -> appState.backstack.add(Screen.SeriesDetail(id = id)) },
                    onShowSnackbar = onShowSnackbar,
                    viewModel = viewModel
                )
            }
            entry<Screen.MovieDetail> {
                val viewModel: DetailVM = hiltViewModel()
                viewModel.id = it.id

                DetailScreen(
                    goToBack = { appState.backstack.removeLastOrNull() },
                    goToMovie = { id -> appState.backstack.add(Screen.MovieDetail(id = id)) },
                    goToPeople = { id -> appState.backstack.add(Screen.PeopleDetail(id = id)) },
                    onShowSnackbar = onShowSnackbar,
                    viewModel = viewModel
                )
            }
            entry<Screen.PeopleDetail> {
                val viewModel: PeopleVM = hiltViewModel()
                viewModel.id = it.id

                PeopleScreen(
                    goToBack = { appState.backstack.removeLastOrNull() },
                    goToMovie = { id -> appState.backstack.add(Screen.MovieDetail(id = id)) },
                    onShowSnackbar = onShowSnackbar,
                    viewModel = viewModel
                )
            }
            entry<Screen.SeriesDetail> {
                val viewModel: SeriesVM = hiltViewModel()
                viewModel.id = it.id

                SeriesScreen(
                    goToBack = { appState.backstack.removeLastOrNull() },
                    goToMovie = { id -> appState.backstack.add(Screen.MovieDetail(id = id)) },
                    viewModel = viewModel
                )
            }
        }
    )
}

sealed interface Screen {
    @Serializable
    @Keep
    object Home : NavKey, Screen
    @Serializable
    @Keep
    object Favorite : NavKey, Screen
    @Serializable
    @Keep
    object My : NavKey, Screen
    @Serializable
    @Keep
    object Search : NavKey, Screen
    @Serializable
    @Keep
    data class MovieDetail(val id: Int) : NavKey, Screen
    @Serializable
    @Keep
    data class PeopleDetail(val id: Int) : NavKey, Screen
    @Serializable
    @Keep
    data class SeriesDetail(val id: Int) : NavKey, Screen
}