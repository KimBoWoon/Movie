package com.bowoon.surfy.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.bowoon.detail.movie.navigation.MovieNavKey
import com.bowoon.detail.people.navigation.PeopleNavKey
import com.bowoon.detail.series.navigation.SeriesNavKey
import com.bowoon.favorite.navigation.FavoriteNavKey
import com.bowoon.home.navigation.HomeNavKey
import com.bowoon.surfy.deeplink.DeeplinkCommand.GoToFavorite
import com.bowoon.surfy.deeplink.DeeplinkCommand.GoToHome
import com.bowoon.surfy.deeplink.DeeplinkCommand.GoToMovie
import com.bowoon.surfy.deeplink.DeeplinkCommand.GoToPeople
import com.bowoon.surfy.deeplink.DeeplinkCommand.GoToSearch
import com.bowoon.surfy.deeplink.DeeplinkCommand.GoToSeries
import com.bowoon.surfy.deeplink.DeeplinkCommand.GoToSetting
import com.bowoon.surfy.deeplink.DeeplinkCommand.OpenFavoriteMovie
import com.bowoon.surfy.deeplink.DeeplinkCommand.OpenFavoritePeople
import com.bowoon.surfy.deeplink.DeeplinkCommand.SearchToMovie
import com.bowoon.surfy.deeplink.DeeplinkCommand.SearchToPeople
import com.bowoon.surfy.deeplink.DeeplinkCommand.SearchToSeries
import com.bowoon.search.navigation.SearchNavKey

fun parseDeeplink(uri: Uri?): List<NavKey> = uri?.let { uri ->
    val path = uri.pathSegments.firstOrNull()
    val query = uri.queryParameterNames.associateWith { name -> uri.getQueryParameter(name) }

    DeeplinkAction.entries.find { it.value == path }?.let { deeplinkAction ->
        when (deeplinkAction) {
            DeeplinkAction.GO_TO_HOME -> createDeeplinkStack(command = GoToHome)
            DeeplinkAction.GO_TO_FAVORITE -> createDeeplinkStack(command = GoToFavorite(tabIndex = query["tabIndex"]?.toIntOrNull() ?: 0))
            DeeplinkAction.GO_TO_SETTING -> createDeeplinkStack(command = GoToSetting)
            DeeplinkAction.OPEN_FAVORITE_PERSON -> createDeeplinkStack(command = OpenFavoritePeople(id = query["peopleId"]?.toIntOrNull() ?: -1))
            DeeplinkAction.OPEN_FAVORITE_MOVIE -> createDeeplinkStack(command = OpenFavoriteMovie(id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.GO_TO_MOVIE -> createDeeplinkStack(command = GoToMovie(id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.GO_TO_PEOPLE -> createDeeplinkStack(command = GoToPeople(id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.GO_TO_SERIES -> createDeeplinkStack(command = GoToSeries(id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.GO_TO_SEARCH -> createDeeplinkStack(command = GoToSearch(query = query["query"] ?: "", searchType = query["searchType"] ?: "surfy"))
            DeeplinkAction.SEARCH_TO_MOVIE -> createDeeplinkStack(command = SearchToMovie(query = query["query"] ?: "", searchType = query["searchType"] ?: "surfy", id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.SEARCH_TO_PEOPLE -> createDeeplinkStack(command = SearchToPeople(query = query["query"] ?: "", searchType = query["searchType"] ?: "people", id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.SEARCH_TO_SERIES -> createDeeplinkStack(command = SearchToSeries(query = query["query"] ?: "", searchType = query["searchType"] ?: "series", id = query["id"]?.toIntOrNull() ?: -1))
        }
    }
} ?: emptyList()

private fun createDeeplinkStack(command: DeeplinkCommand): List<NavKey> = when (command) {
    is GoToHome -> listOf(HomeNavKey)
    is GoToFavorite -> listOf(FavoriteNavKey(tab = command.tabIndex))
    is GoToSetting -> emptyList()
    is OpenFavoritePeople -> listOf(FavoriteNavKey(tab = 0), PeopleNavKey(id = command.id))
    is OpenFavoriteMovie -> listOf(FavoriteNavKey(tab = 1), MovieNavKey(id = command.id))
    is GoToMovie -> listOf(MovieNavKey(id = command.id))
    is GoToPeople -> listOf(PeopleNavKey(id = command.id))
    is GoToSeries -> listOf(SeriesNavKey(id = command.id))
    is GoToSearch -> listOf(SearchNavKey(query = command.query, searchType = command.searchType))
    is SearchToMovie -> listOf(SearchNavKey(query = command.query, searchType = command.searchType), MovieNavKey(id = command.id))
    is SearchToPeople -> listOf(SearchNavKey(query = command.query, searchType = command.searchType), PeopleNavKey(id = command.id))
    is SearchToSeries -> listOf(SearchNavKey(query = command.query, searchType = command.searchType), SeriesNavKey(id = command.id))
}