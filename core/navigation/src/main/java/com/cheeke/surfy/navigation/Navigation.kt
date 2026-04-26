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

interface AppNavigator {
    fun goToMovie(id: Int)
    fun goToPeople(id: Int)
    fun goToTv(id: Int)
    fun goToSeries(id: Int)
    fun goToSearch()
    fun back()
    fun goToSetting()
}

class AppNavigatorImpl(
    private val navigator: Navigator
) : AppNavigator {
    override fun goToMovie(id: Int) {
        navigator.goTo(screen = MovieScreen(id))
    }

    override fun goToPeople(id: Int) {
        navigator.goTo(screen = PeopleScreen(id))
    }

    override fun goToTv(id: Int) {
        navigator.goTo(screen = TvScreen(id))
    }

    override fun goToSeries(id: Int) {
        navigator.goTo(screen = SeriesScreen(id))
    }

    override fun goToSearch() {
        navigator.goTo(screen = SearchScreen())
    }

    override fun goToSetting() {
        navigator.goTo(screen = SettingScreen)
    }

    override fun back() {
        navigator.pop()
    }
}

val LocalAppNavigator = staticCompositionLocalOf<AppNavigator> {
    error("No AppNavigator")
}

val LocalCircuitNavigator = staticCompositionLocalOf<Navigator> {
    error("No AppNavigator")
}

val LocalCircuitBackStack = staticCompositionLocalOf<SaveableBackStack> {
    error("No AppNavigator")
}