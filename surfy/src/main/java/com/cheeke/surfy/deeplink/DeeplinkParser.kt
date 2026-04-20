//package com.cheeke.surfy.deeplink
//
//import android.net.Uri
//import androidx.navigation3.runtime.NavKey
//import com.cheeke.surfy.detail.movie.navigation.MovieNavKey
//import com.cheeke.surfy.detail.people.navigation.PeopleNavKey
//import com.cheeke.surfy.detail.series.navigation.SeriesNavKey
//import com.cheeke.surfy.detail.tv.navigation.TvNavKey
//import com.cheeke.surfy.favorite.FavoriteTab
//import com.cheeke.surfy.favorite.navigation.FavoriteNavKey
//import com.cheeke.surfy.home.navigation.HomeNavKey
//import com.cheeke.surfy.search.navigation.SearchNavKey
//
//fun parseDeeplink(uri: Uri?): List<NavKey> = uri?.let { uri ->
//    val path = uri.pathSegments.firstOrNull()
//    val query = uri.queryParameterNames.associateWith { name -> uri.getQueryParameter(name) }
//
//    DeeplinkAction.entries.find { it.value == path }?.let { deeplinkAction ->
//        when (deeplinkAction) {
//            DeeplinkAction.GO_TO_HOME -> createDeeplinkStack(command = DeeplinkCommand.GoToHome)
//            DeeplinkAction.GO_TO_FAVORITE -> createDeeplinkStack(
//                command = DeeplinkCommand.GoToFavorite(tabIndex = query["tabIndex"]?.toIntOrNull() ?: 0)
//            )
//            DeeplinkAction.GO_TO_SETTING -> createDeeplinkStack(command = DeeplinkCommand.GoToSetting)
//            DeeplinkAction.OPEN_FAVORITE_PERSON -> createDeeplinkStack(
//                command = DeeplinkCommand.OpenFavoritePeople(id = query["peopleId"]?.toIntOrNull() ?: -1)
//            )
//            DeeplinkAction.OPEN_FAVORITE_MOVIE -> createDeeplinkStack(
//                command = DeeplinkCommand.OpenFavoriteMovie(id = query["id"]?.toIntOrNull() ?: -1)
//            )
//            DeeplinkAction.OPEN_FAVORITE_TV -> createDeeplinkStack(
//                command = DeeplinkCommand.OpenFavoriteTv(id = query["id"]?.toIntOrNull() ?: -1)
//            )
//            DeeplinkAction.GO_TO_MOVIE -> createDeeplinkStack(
//                command = DeeplinkCommand.GoToMovie(id = query["id"]?.toIntOrNull() ?: -1)
//            )
//            DeeplinkAction.GO_TO_TV -> createDeeplinkStack(
//                command = DeeplinkCommand.GoToTv(id = query["id"]?.toIntOrNull() ?: -1)
//            )
//            DeeplinkAction.GO_TO_PEOPLE -> createDeeplinkStack(
//                command = DeeplinkCommand.GoToPeople(id = query["id"]?.toIntOrNull() ?: -1)
//            )
//            DeeplinkAction.GO_TO_SERIES -> createDeeplinkStack(
//                command = DeeplinkCommand.GoToSeries(id = query["id"]?.toIntOrNull() ?: -1)
//            )
//            DeeplinkAction.GO_TO_SEARCH -> createDeeplinkStack(
//                command = DeeplinkCommand.GoToSearch(
//                    query = query["query"] ?: "",
//                    searchType = query["searchType"] ?: "surfy"
//                )
//            )
//            DeeplinkAction.SEARCH_TO_MOVIE -> createDeeplinkStack(
//                command = DeeplinkCommand.SearchToMovie(
//                    query = query["query"] ?: "",
//                    searchType = query["searchType"] ?: "surfy",
//                    id = query["id"]?.toIntOrNull() ?: -1
//                )
//            )
//            DeeplinkAction.SEARCH_TO_PEOPLE -> createDeeplinkStack(
//                command = DeeplinkCommand.SearchToPeople(
//                    query = query["query"] ?: "",
//                    searchType = query["searchType"] ?: "people",
//                    id = query["id"]?.toIntOrNull() ?: -1
//                )
//            )
//            DeeplinkAction.SEARCH_TO_SERIES -> createDeeplinkStack(
//                command = DeeplinkCommand.SearchToSeries(
//                    query = query["query"] ?: "",
//                    searchType = query["searchType"] ?: "series",
//                    id = query["id"]?.toIntOrNull() ?: -1
//                )
//            )
//            DeeplinkAction.SEARCH_TO_TV -> createDeeplinkStack(
//                command = DeeplinkCommand.SearchToTv(
//                    query = query["query"] ?: "",
//                    searchType = query["searchType"] ?: "tv",
//                    id = query["id"]?.toIntOrNull() ?: -1
//                )
//            )
//        }
//    }
//} ?: emptyList()
//
//private fun createDeeplinkStack(command: DeeplinkCommand): List<NavKey> = when (command) {
//    is DeeplinkCommand.GoToHome -> listOf(HomeNavKey)
//    is DeeplinkCommand.GoToFavorite -> listOf(FavoriteNavKey(tab = command.tabIndex))
//    is DeeplinkCommand.GoToSetting -> emptyList()
//    is DeeplinkCommand.OpenFavoritePeople -> listOf(FavoriteNavKey(tab = FavoriteTab.PEOPLE.ordinal), PeopleNavKey(id = command.id))
//    is DeeplinkCommand.OpenFavoriteMovie -> listOf(FavoriteNavKey(tab = FavoriteTab.MOVIE.ordinal), MovieNavKey(id = command.id))
//    is DeeplinkCommand.OpenFavoriteTv -> listOf(FavoriteNavKey(tab = FavoriteTab.TV.ordinal), TvNavKey(id = command.id))
//    is DeeplinkCommand.GoToMovie -> listOf(MovieNavKey(id = command.id))
//    is DeeplinkCommand.GoToTv -> listOf(TvNavKey(id = command.id))
//    is DeeplinkCommand.GoToPeople -> listOf(PeopleNavKey(id = command.id))
//    is DeeplinkCommand.GoToSeries -> listOf(SeriesNavKey(id = command.id))
//    is DeeplinkCommand.GoToSearch -> listOf(SearchNavKey(query = command.query, searchType = command.searchType))
//    is DeeplinkCommand.SearchToMovie -> listOf(SearchNavKey(query = command.query, searchType = command.searchType), MovieNavKey(id = command.id))
//    is DeeplinkCommand.SearchToPeople -> listOf(SearchNavKey(query = command.query, searchType = command.searchType), PeopleNavKey(id = command.id))
//    is DeeplinkCommand.SearchToSeries -> listOf(SearchNavKey(query = command.query, searchType = command.searchType), SeriesNavKey(id = command.id))
//    is DeeplinkCommand.SearchToTv -> listOf(SearchNavKey(query = command.query, searchType = command.searchType), TvNavKey(id = command.id))
//}