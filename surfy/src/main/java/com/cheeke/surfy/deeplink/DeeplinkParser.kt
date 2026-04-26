package com.cheeke.surfy.deeplink

import android.net.Uri
import com.cheeke.surfy.favorite.FavoriteTab
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.navigation.FavoriteScreen
import com.cheeke.surfy.navigation.HomeScreen
import com.cheeke.surfy.navigation.MovieScreen
import com.cheeke.surfy.navigation.PeopleScreen
import com.cheeke.surfy.navigation.SearchScreen
import com.cheeke.surfy.navigation.SeriesScreen
import com.cheeke.surfy.navigation.TvScreen
import com.slack.circuit.runtime.screen.Screen

fun parseDeeplink(uri: Uri?): List<Screen> = uri?.let { uri ->
    val path = uri.pathSegments.firstOrNull()
    val query = uri.queryParameterNames.associateWith { name -> uri.getQueryParameter(name) }

    DeeplinkAction.entries.find { it.value == path }?.let { deeplinkAction ->
        when (deeplinkAction) {
            DeeplinkAction.GO_TO_HOME -> createDeeplinkStack(command = DeeplinkCommand.GoToHome)
            DeeplinkAction.GO_TO_FAVORITE -> createDeeplinkStack(
                command = DeeplinkCommand.GoToFavorite(tabIndex = query["tabIndex"]?.toIntOrNull() ?: 0)
            )
            DeeplinkAction.GO_TO_SETTING -> createDeeplinkStack(command = DeeplinkCommand.GoToSetting)
            DeeplinkAction.OPEN_FAVORITE_PERSON -> createDeeplinkStack(
                command = DeeplinkCommand.OpenFavoritePeople(id = query["peopleId"]?.toIntOrNull() ?: -1)
            )
            DeeplinkAction.OPEN_FAVORITE_MOVIE -> createDeeplinkStack(
                command = DeeplinkCommand.OpenFavoriteMovie(id = query["id"]?.toIntOrNull() ?: -1)
            )
            DeeplinkAction.OPEN_FAVORITE_TV -> createDeeplinkStack(
                command = DeeplinkCommand.OpenFavoriteTv(id = query["id"]?.toIntOrNull() ?: -1)
            )
            DeeplinkAction.GO_TO_MOVIE -> createDeeplinkStack(
                command = DeeplinkCommand.GoToMovie(id = query["id"]?.toIntOrNull() ?: -1)
            )
            DeeplinkAction.GO_TO_TV -> createDeeplinkStack(
                command = DeeplinkCommand.GoToTv(id = query["id"]?.toIntOrNull() ?: -1)
            )
            DeeplinkAction.GO_TO_PEOPLE -> createDeeplinkStack(
                command = DeeplinkCommand.GoToPeople(id = query["id"]?.toIntOrNull() ?: -1)
            )
            DeeplinkAction.GO_TO_SERIES -> createDeeplinkStack(
                command = DeeplinkCommand.GoToSeries(id = query["id"]?.toIntOrNull() ?: -1)
            )
            DeeplinkAction.GO_TO_SEARCH -> createDeeplinkStack(
                command = DeeplinkCommand.GoToSearch(
                    query = query["query"] ?: "",
                    searchType = query["searchType"] ?: "surfy"
                )
            )
            DeeplinkAction.SEARCH_TO_MOVIE -> createDeeplinkStack(
                command = DeeplinkCommand.SearchToMovie(
                    query = query["query"] ?: "",
                    searchType = query["searchType"] ?: "surfy",
                    id = query["id"]?.toIntOrNull() ?: -1
                )
            )
            DeeplinkAction.SEARCH_TO_PEOPLE -> createDeeplinkStack(
                command = DeeplinkCommand.SearchToPeople(
                    query = query["query"] ?: "",
                    searchType = query["searchType"] ?: "people",
                    id = query["id"]?.toIntOrNull() ?: -1
                )
            )
            DeeplinkAction.SEARCH_TO_SERIES -> createDeeplinkStack(
                command = DeeplinkCommand.SearchToSeries(
                    query = query["query"] ?: "",
                    searchType = query["searchType"] ?: "series",
                    id = query["id"]?.toIntOrNull() ?: -1
                )
            )
            DeeplinkAction.SEARCH_TO_TV -> createDeeplinkStack(
                command = DeeplinkCommand.SearchToTv(
                    query = query["query"] ?: "",
                    searchType = query["searchType"] ?: "tv",
                    id = query["id"]?.toIntOrNull() ?: -1
                )
            )
        }
    }
} ?: emptyList()

private fun createDeeplinkStack(command: DeeplinkCommand): List<Screen> = when (command) {
    is DeeplinkCommand.GoToHome -> listOf(HomeScreen)
    is DeeplinkCommand.GoToFavorite -> listOf(FavoriteScreen(index = command.tabIndex))
    is DeeplinkCommand.GoToSetting -> emptyList()
    is DeeplinkCommand.OpenFavoritePeople -> listOf(FavoriteScreen(index = FavoriteTab.PEOPLE.ordinal), PeopleScreen(id = command.id))
    is DeeplinkCommand.OpenFavoriteMovie -> listOf(FavoriteScreen(index = FavoriteTab.MOVIE.ordinal), MovieScreen(id = command.id))
    is DeeplinkCommand.OpenFavoriteTv -> listOf(FavoriteScreen(index = FavoriteTab.TV.ordinal), TvScreen(id = command.id))
    is DeeplinkCommand.GoToMovie -> listOf(MovieScreen(id = command.id))
    is DeeplinkCommand.GoToTv -> listOf(TvScreen(id = command.id))
    is DeeplinkCommand.GoToPeople -> listOf(PeopleScreen(id = command.id))
    is DeeplinkCommand.GoToSeries -> listOf(SeriesScreen(id = command.id))
    is DeeplinkCommand.GoToSearch -> listOf(SearchScreen(query = command.query, searchType = SearchType.valueOf(command.searchType.uppercase())))
    is DeeplinkCommand.SearchToMovie -> listOf(SearchScreen(query = command.query, searchType = SearchType.valueOf(command.searchType.uppercase())), MovieScreen(id = command.id))
    is DeeplinkCommand.SearchToPeople -> listOf(SearchScreen(query = command.query, searchType = SearchType.valueOf(command.searchType.uppercase())), PeopleScreen(id = command.id))
    is DeeplinkCommand.SearchToSeries -> listOf(SearchScreen(query = command.query, searchType = SearchType.valueOf(command.searchType.uppercase())), SeriesScreen(id = command.id))
    is DeeplinkCommand.SearchToTv -> listOf(SearchScreen(query = command.query, searchType = SearchType.valueOf(command.searchType.uppercase())), TvScreen(id = command.id))
}