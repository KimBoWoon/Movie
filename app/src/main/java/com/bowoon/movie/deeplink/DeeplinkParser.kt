package com.bowoon.movie.deeplink

import android.net.Uri
import com.bowoon.detail.movie.navigation.MovieNavKey
import com.bowoon.detail.people.navigation.PeopleNavKey
import com.bowoon.favorite.navigation.FavoriteNavKey
import com.bowoon.home.navigation.HomeNavKey
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToFavorite
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToHome
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToMovie
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToPeople
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToSearch
import com.bowoon.movie.deeplink.DeeplinkCommand.GoToSetting
import com.bowoon.movie.deeplink.DeeplinkCommand.OpenFavoriteMovie
import com.bowoon.movie.deeplink.DeeplinkCommand.OpenFavoritePeople
import com.bowoon.movie.deeplink.DeeplinkCommand.SearchToMovie
import com.bowoon.movie.deeplink.DeeplinkCommand.SearchToPeople
import com.bowoon.my.navigation.SettingNavKey
import com.bowoon.navigation.NavigationState
import com.bowoon.search.navigation.SearchNavKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DeeplinkParser @AssistedInject constructor(
    @Assisted(value = "navigationState") private val navigationState: NavigationState
) {
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(value = "navigationState") navigationState: NavigationState
        ): DeeplinkParser
    }

    fun parseDeeplink(
        uri: Uri?,
        navigationState: NavigationState
    ) {
        uri?.let { uri ->
            val path = uri.pathSegments.first()
            val query = uri.queryParameterNames.associateWith { name -> uri.getQueryParameter(name) }

            // 딥링크로 진입시 백스택 초기화
            navigationState.backStacks.entries.forEach { (_, value) ->
                while (value.size > 1) {
                    value.removeAt(index = value.lastIndex)
                }
            }

            when (path) {
                DeeplinkAction.GO_TO_HOME.value -> parse(action = DeeplinkAction.GO_TO_HOME, query = emptyMap())
                DeeplinkAction.GO_TO_FAVORITE.value -> parse(action = DeeplinkAction.GO_TO_FAVORITE, query = query)
                DeeplinkAction.GO_TO_SETTING.value -> parse(action = DeeplinkAction.GO_TO_SETTING, query = emptyMap())
                DeeplinkAction.OPEN_FAVORITE_PERSON.value -> parse(action = DeeplinkAction.OPEN_FAVORITE_PERSON, query = query)
                DeeplinkAction.OPEN_FAVORITE_MOVIE.value -> parse(action = DeeplinkAction.OPEN_FAVORITE_MOVIE, query = query)
                DeeplinkAction.GO_TO_MOVIE.value -> parse(action = DeeplinkAction.GO_TO_MOVIE, query = query)
                DeeplinkAction.GO_TO_PEOPLE.value -> parse(action = DeeplinkAction.GO_TO_PEOPLE, query = query)
                DeeplinkAction.GO_TO_SEARCH.value -> parse(action = DeeplinkAction.GO_TO_SEARCH, query = query)
                DeeplinkAction.SEARCH_TO_MOVIE.value -> parse(action = DeeplinkAction.SEARCH_TO_MOVIE, query = query)
                DeeplinkAction.SEARCH_TO_PEOPLE.value -> parse(action = DeeplinkAction.SEARCH_TO_PEOPLE, query = query)
            }
        }
    }

    private fun parse(action: DeeplinkAction, query: Map<String, String?>) {
        when (action) {
            DeeplinkAction.GO_TO_HOME -> createDeeplinkStack(command = GoToHome)
            DeeplinkAction.GO_TO_FAVORITE -> createDeeplinkStack(command = GoToFavorite(tabIndex = query["tabIndex"]?.toIntOrNull() ?: 0))
            DeeplinkAction.GO_TO_SETTING -> createDeeplinkStack(command = GoToSetting)
            DeeplinkAction.OPEN_FAVORITE_PERSON -> createDeeplinkStack(command = OpenFavoritePeople(id = query["peopleId"]?.toIntOrNull() ?: -1))
            DeeplinkAction.OPEN_FAVORITE_MOVIE -> createDeeplinkStack(command = OpenFavoriteMovie(id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.GO_TO_MOVIE -> createDeeplinkStack(command = GoToMovie(id = query["id"]?.toIntOrNull() ?: -1, tabIndex = query["tabIndex"]?.toIntOrNull() ?: 0))
            DeeplinkAction.GO_TO_PEOPLE -> createDeeplinkStack(command = GoToPeople(id = query["id"]?.toIntOrNull() ?: -1))
            DeeplinkAction.GO_TO_SEARCH -> createDeeplinkStack(command = GoToSearch(query = query["query"] ?: "", searchType = query["searchType"] ?: "movie"))
            DeeplinkAction.SEARCH_TO_MOVIE -> createDeeplinkStack(command = SearchToMovie(query = query["query"] ?: "", searchType = query["searchType"] ?: "movie", id = query["id"]?.toIntOrNull() ?: -1, tabIndex = query["tabIndex"]?.toIntOrNull() ?: 0))
            DeeplinkAction.SEARCH_TO_PEOPLE -> createDeeplinkStack(command = SearchToPeople(query = query["query"] ?: "", searchType = query["searchType"] ?: "people", id = query["id"]?.toIntOrNull() ?: -1))
        }
    }

    private fun createDeeplinkStack(command: DeeplinkCommand) {
        when (command) {
            is GoToHome -> navigationState.topLevelRoute = HomeNavKey
            is GoToFavorite -> {
                navigationState.topLevelRoute = FavoriteNavKey(tab = 0)
                navigationState.backStacks[navigationState.topLevelRoute]?.add(element = FavoriteNavKey(tab = command.tabIndex))
            }
            is GoToSetting -> navigationState.topLevelRoute = SettingNavKey
            is OpenFavoritePeople -> {
                navigationState.topLevelRoute = FavoriteNavKey(tab = 0)
                navigationState.backStacks[navigationState.topLevelRoute]?.add(element = FavoriteNavKey(tab = 0))
                navigationState.backStacks[navigationState.topLevelRoute]?.add(element = PeopleNavKey(id = command.id))
            }
            is OpenFavoriteMovie -> {
                navigationState.topLevelRoute = FavoriteNavKey(tab = 0)
                navigationState.backStacks[navigationState.topLevelRoute]?.add(element = FavoriteNavKey(tab = 1))
                navigationState.backStacks[navigationState.topLevelRoute]?.add(element = MovieNavKey(id = command.id))
            }
            is GoToMovie -> navigationState.backStacks[navigationState.topLevelRoute]?.add(element = MovieNavKey(id = command.id, tab = command.tabIndex))
            is GoToPeople -> navigationState.backStacks[navigationState.topLevelRoute]?.add(element = PeopleNavKey(id = command.id))
            is GoToSearch -> navigationState.backStacks[navigationState.topLevelRoute]?.add(element = SearchNavKey(query = command.query, searchType = command.searchType))
            is SearchToMovie -> {
                navigationState.backStacks[navigationState.topLevelRoute]?.add(element = SearchNavKey(query = command.query, searchType = command.searchType))
                navigationState.backStacks[navigationState.topLevelRoute]?.add(element = MovieNavKey(id = command.id, tab = command.tabIndex))
            }
            is SearchToPeople -> {
                navigationState.backStacks[navigationState.topLevelRoute]?.add(element = SearchNavKey(query = command.query, searchType = command.searchType))
                navigationState.backStacks[navigationState.topLevelRoute]?.add(element = PeopleNavKey(id = command.id))
            }
        }
    }
}