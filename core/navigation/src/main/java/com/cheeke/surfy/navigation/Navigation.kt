package com.cheeke.surfy.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import com.cheeke.surfy.model.SearchType
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object RootScreen : Screen

@Parcelize
data class MovieScreen(val id: Int) : Screen

@Parcelize
data class PeopleScreen(val id: Int) : Screen

@Parcelize
data class TvScreen(val id: Int) : Screen

@Parcelize
data class SeriesScreen(val id: Int) : Screen

@Parcelize
data class SearchScreen(val query: String = "", val searchType: SearchType = SearchType.MULTI) : Screen

@Parcelize
object HomeScreen : Screen

@Parcelize
data class FavoriteScreen(val index: Int = 0) : Screen

@Parcelize
object SettingScreen : Screen

fun Navigator.goToMovie(id: Int) { goTo(screen = MovieScreen(id = id)) }
fun Navigator.goToPeople(id: Int) { goTo(screen = PeopleScreen(id = id)) }
fun Navigator.goToTv(id: Int) { goTo(screen = TvScreen(id = id)) }
fun Navigator.goToSeries(id: Int) { goTo(screen = SeriesScreen(id = id)) }
fun Navigator.goToSearch(query: String, searchType: SearchType) { goTo(screen = SearchScreen(query = query, searchType = searchType)) }

val LocalRootNavigator = staticCompositionLocalOf<Navigator> {
    error("No Root Navigator")
}

val LocalRootBackstack = staticCompositionLocalOf<SaveableBackStack> {
    error("No Root Backstack")
}