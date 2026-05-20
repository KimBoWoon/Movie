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

private val parsers = listOf(
    GoToHomeParser,
    GoToFavoriteParser,
    GoToSettingParser,
    OpenFavoritePeopleParser,
    OpenFavoriteMovieParser,
    OpenFavoriteTvParser,
    GoToMovieParser,
    GoToPeopleParser,
    GoToTvParser,
    GoToSeriesParser,
    GoToSearchParser,
    SearchToMovieParser,
    SearchToPeopleParser,
    SearchToSeriesParser,
    SearchToTvParser
)

interface DeeplinkParser {
    val path: String
    fun parse(uri: Uri): List<Screen>
}

object GoToHomeParser : DeeplinkParser {
    override val path = "go_to_home"
    override fun parse(uri: Uri): List<Screen> = listOf(HomeScreen)
}

object GoToFavoriteParser : DeeplinkParser {
    override val path = "go_to_favorite"
    override fun parse(uri: Uri): List<Screen> {
        val index = uri.getQueryParameter("index")?.toIntOrNull() ?: 0
        return listOf(FavoriteScreen(index = index))
    }
}

object GoToSettingParser : DeeplinkParser {
    override val path = "go_to_setting"
    override fun parse(uri: Uri): List<Screen> = emptyList()
}

object OpenFavoritePeopleParser : DeeplinkParser {
    override val path = "open_favorite_people"
    override fun parse(uri: Uri): List<Screen> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(FavoriteScreen(index = FavoriteTab.PEOPLE.ordinal), PeopleScreen(id = id))
    }
}

object OpenFavoriteMovieParser : DeeplinkParser {
    override val path = "open_favorite_movie"
    override fun parse(uri: Uri): List<Screen> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(FavoriteScreen(index = FavoriteTab.MOVIE.ordinal), MovieScreen(id = id))
    }
}

object OpenFavoriteTvParser : DeeplinkParser {
    override val path = "open_favorite_tv"
    override fun parse(uri: Uri): List<Screen> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(FavoriteScreen(index = FavoriteTab.TV.ordinal), TvScreen(id = id))
    }
}

object GoToMovieParser : DeeplinkParser {
    override val path = "go_to_movie"
    override fun parse(uri: Uri): List<Screen> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(MovieScreen(id = id))
    }
}

object GoToPeopleParser : DeeplinkParser {
    override val path = "go_to_people"
    override fun parse(uri: Uri): List<Screen> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(PeopleScreen(id = id))
    }
}

object GoToTvParser : DeeplinkParser {
    override val path = "go_to_tv"
    override fun parse(uri: Uri): List<Screen> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(TvScreen(id = id))
    }
}

object GoToSeriesParser : DeeplinkParser {
    override val path = "go_to_series"
    override fun parse(uri: Uri): List<Screen> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SeriesScreen(id = id))
    }
}

object GoToSearchParser : DeeplinkParser {
    override val path = "go_to_search"
    override fun parse(uri: Uri): List<Screen> {
        val query = uri.getQueryParameter("query") ?: ""
        val searchType = uri.getQueryParameter("searchType") ?: "surfy"
        return listOf(SearchScreen(query = query, searchType = SearchType.valueOf(value = searchType.uppercase())))
    }
}

object SearchToMovieParser : DeeplinkParser {
    override val path = "search_to_movie"
    override fun parse(uri: Uri): List<Screen> {
        val query = uri.getQueryParameter("query") ?: ""
        val searchType = uri.getQueryParameter("searchType") ?: "surfy"
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchScreen(query = query, searchType = SearchType.valueOf(value = searchType.uppercase())), MovieScreen(id = id))
    }
}

object SearchToPeopleParser : DeeplinkParser {
    override val path = "search_to_people"
    override fun parse(uri: Uri): List<Screen> {
        val query = uri.getQueryParameter("query") ?: ""
        val searchType = uri.getQueryParameter("searchType") ?: "surfy"
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchScreen(query = query, searchType = SearchType.valueOf(value = searchType.uppercase())), PeopleScreen(id = id))
    }
}

object SearchToSeriesParser : DeeplinkParser {
    override val path = "search_to_series"
    override fun parse(uri: Uri): List<Screen> {
        val query = uri.getQueryParameter("query") ?: ""
        val searchType = uri.getQueryParameter("searchType") ?: "surfy"
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchScreen(query = query, searchType = SearchType.valueOf(value = searchType.uppercase())), TvScreen(id = id))
    }
}

object SearchToTvParser : DeeplinkParser {
    override val path = "search_to_tv"
    override fun parse(uri: Uri): List<Screen> {
        val query = uri.getQueryParameter("query") ?: ""
        val searchType = uri.getQueryParameter("searchType") ?: "surfy"
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchScreen(query = query, searchType = SearchType.valueOf(value = searchType.uppercase())), SeriesScreen(id = id))
    }
}

fun parseDeeplink(uri: Uri?): List<Screen> {
    if (uri == null) {
        return emptyList()
    }

    val path = uri.pathSegments.firstOrNull() ?: return emptyList()
    val parser = parsers.firstOrNull { it.path == path } ?: return emptyList()
    return parser.parse(uri)
}