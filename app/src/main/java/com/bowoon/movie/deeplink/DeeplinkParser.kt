package com.bowoon.movie.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.bowoon.detail.movie.navigation.MovieNavKey
import com.bowoon.detail.people.navigation.PeopleNavKey
import com.bowoon.detail.series.navigation.SeriesNavKey
import com.bowoon.favorite.navigation.FavoriteNavKey
import com.bowoon.home.navigation.HomeNavKey
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToFavorite
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToHome
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToMovie
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToPeople
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToSearch
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToSeries
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToSetting
import com.bowoon.movie.deeplink.DeeplinkCommand.OpenFavoriteMovie
import com.bowoon.movie.deeplink.DeeplinkCommand.OpenFavoritePeople
import com.bowoon.movie.deeplink.DeeplinkCommand.SearchToMovie
import com.bowoon.movie.deeplink.DeeplinkCommand.SearchToPeople
import com.bowoon.my.navigation.SettingNavKey
import com.bowoon.search.navigation.SearchNavKey
import javax.inject.Inject

class DeeplinkParser @Inject constructor() {
    fun parseDeeplink(uri: Uri?): List<NavKey> = uri?.let { uri ->
        val path = uri.pathSegments.firstOrNull()
        val query = uri.queryParameterNames.associateWith { name -> uri.getQueryParameter(name) }

        DeeplinkAction.entries.find { it.value == path }?.let { deeplinkAction ->
            parse(action = deeplinkAction, query = query)
        }
    } ?: emptyList()

    private fun parse(action: DeeplinkAction, query: Map<String, String?>): List<NavKey> =
        when (action) {
            DeeplinkAction.GO_TO_HOME -> createDeeplinkStack(command = GoToHome)
            DeeplinkAction.GO_TO_FAVORITE -> createDeeplinkStack(command = GoToFavorite(tabIndex = query["tabIndex"]?.toIntOrNull() ?: 0))
            DeeplinkAction.GO_TO_SETTING -> createDeeplinkStack(command = GoToSetting)
            DeeplinkAction.OPEN_FAVORITE_PERSON -> createDeeplinkStack(command = OpenFavoritePeople(id = query["peopleId"]?.toIntOrNull() ?: -1))
            DeeplinkAction.OPEN_FAVORITE_MOVIE -> createDeeplinkStack(command = OpenFavoriteMovie(id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.GO_TO_MOVIE -> createDeeplinkStack(command = GoToMovie(id = query["id"]?.toIntOrNull() ?: -1, tabIndex = query["tabIndex"]?.toIntOrNull() ?: 0))
            DeeplinkAction.GO_TO_PEOPLE -> createDeeplinkStack(command = GoToPeople(id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.GO_TO_SERIES -> createDeeplinkStack(command = GoToSeries(id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.GO_TO_SEARCH -> createDeeplinkStack(command = GoToSearch(query = query["query"] ?: "", searchType = query["searchType"] ?: "movie"))
            DeeplinkAction.SEARCH_TO_MOVIE -> createDeeplinkStack(command = SearchToMovie(query = query["query"] ?: "", searchType = query["searchType"] ?: "movie", id = query["id"]?.toIntOrNull() ?: -1, tabIndex = query["tabIndex"]?.toIntOrNull() ?: 0))
            DeeplinkAction.SEARCH_TO_PEOPLE -> createDeeplinkStack(command = SearchToPeople(query = query["query"] ?: "", searchType = query["searchType"] ?: "people", id = query["id"]?.toIntOrNull() ?: -1))
        }

    private fun createDeeplinkStack(command: DeeplinkCommand): List<NavKey> = when (command) {
        is GoToHome -> listOf(HomeNavKey)
        is GoToFavorite -> listOf(FavoriteNavKey(tab = command.tabIndex))
        is GoToSetting -> listOf(SettingNavKey)
        is OpenFavoritePeople -> listOf(FavoriteNavKey(tab = 0), PeopleNavKey(id = command.id))
        is OpenFavoriteMovie -> listOf(FavoriteNavKey(tab = 1), MovieNavKey(id = command.id))
        is GoToMovie -> listOf(MovieNavKey(id = command.id, tab = command.tabIndex))
        is GoToPeople -> listOf(PeopleNavKey(id = command.id))
        is GoToSeries -> listOf(SeriesNavKey(id = command.id))
        is GoToSearch -> listOf(SearchNavKey(query = command.query, searchType = command.searchType))
        is SearchToMovie -> listOf(SearchNavKey(query = command.query, searchType = command.searchType), MovieNavKey(id = command.id, tab = command.tabIndex))
        is SearchToPeople -> listOf(SearchNavKey(query = command.query, searchType = command.searchType), PeopleNavKey(id = command.id))
    }
}