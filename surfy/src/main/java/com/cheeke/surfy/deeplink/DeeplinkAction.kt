package com.cheeke.surfy.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.detail.movie.navigation.MovieNavKey
import com.cheeke.surfy.detail.people.navigation.PeopleNavKey
import com.cheeke.surfy.detail.series.navigation.SeriesNavKey
import com.cheeke.surfy.detail.tv.navigation.TvNavKey
import com.cheeke.surfy.favorite.FavoriteKeys
import com.cheeke.surfy.favorite.navigation.FavoriteNavKey
import com.cheeke.surfy.home.navigation.HomeNavKey
import com.cheeke.surfy.search.navigation.SearchNavKey

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
    fun parse(uri: Uri): List<NavKey>
}

object GoToHomeParser : DeeplinkParser {
    override val path = "go_to_home"
    override fun parse(uri: Uri): List<NavKey> = listOf(HomeNavKey)
}

object GoToFavoriteParser : DeeplinkParser {
    override val path = "go_to_favorite"
    override fun parse(uri: Uri): List<NavKey> {
        val key = uri.getQueryParameter("key") ?: "movie"
        return listOf(FavoriteNavKey(tab = key))
    }
}

object GoToSettingParser : DeeplinkParser {
    override val path = "go_to_setting"
    override fun parse(uri: Uri): List<NavKey> = emptyList()
}

object OpenFavoritePeopleParser : DeeplinkParser {
    override val path = "open_favorite_people"
    override fun parse(uri: Uri): List<NavKey> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(FavoriteNavKey(tab = FavoriteKeys.PEOPLE), PeopleNavKey(id = id))
    }
}

object OpenFavoriteMovieParser : DeeplinkParser {
    override val path = "open_favorite_movie"
    override fun parse(uri: Uri): List<NavKey> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(FavoriteNavKey(tab = FavoriteKeys.MOVIE), MovieNavKey(id = id))
    }
}

object OpenFavoriteTvParser : DeeplinkParser {
    override val path = "open_favorite_tv"
    override fun parse(uri: Uri): List<NavKey> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(FavoriteNavKey(tab = FavoriteKeys.TV), TvNavKey(id = id))
    }
}

object GoToMovieParser : DeeplinkParser {
    override val path = "go_to_movie"
    override fun parse(uri: Uri): List<NavKey> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(MovieNavKey(id = id))
    }
}

object GoToPeopleParser : DeeplinkParser {
    override val path = "go_to_people"
    override fun parse(uri: Uri): List<NavKey> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(PeopleNavKey(id = id))
    }
}

object GoToTvParser : DeeplinkParser {
    override val path = "go_to_tv"
    override fun parse(uri: Uri): List<NavKey> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(TvNavKey(id = id))
    }
}

object GoToSeriesParser : DeeplinkParser {
    override val path = "go_to_series"
    override fun parse(uri: Uri): List<NavKey> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SeriesNavKey(id = id))
    }
}

object GoToSearchParser : DeeplinkParser {
    override val path = "go_to_search"
    override fun parse(uri: Uri): List<NavKey> {
        val query = uri.getQueryParameter("query") ?: ""
        val searchType = uri.getQueryParameter("searchType") ?: "surfy"
        return listOf(SearchNavKey(query = query, searchType = searchType.uppercase()))
    }
}

object SearchToMovieParser : DeeplinkParser {
    override val path = "search_to_movie"
    override fun parse(uri: Uri): List<NavKey> {
        val query = uri.getQueryParameter("query") ?: ""
        val searchType = uri.getQueryParameter("searchType") ?: "surfy"
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchNavKey(query = query, searchType = searchType.uppercase()), MovieNavKey(id = id))
    }
}

object SearchToPeopleParser : DeeplinkParser {
    override val path = "search_to_people"
    override fun parse(uri: Uri): List<NavKey> {
        val query = uri.getQueryParameter("query") ?: ""
        val searchType = uri.getQueryParameter("searchType") ?: "surfy"
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchNavKey(query = query, searchType = searchType.uppercase()), PeopleNavKey(id = id))
    }
}

object SearchToSeriesParser : DeeplinkParser {
    override val path = "search_to_series"
    override fun parse(uri: Uri): List<NavKey> {
        val query = uri.getQueryParameter("query") ?: ""
        val searchType = uri.getQueryParameter("searchType") ?: "surfy"
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchNavKey(query = query, searchType = searchType.uppercase()), TvNavKey(id = id))
    }
}

object SearchToTvParser : DeeplinkParser {
    override val path = "search_to_tv"
    override fun parse(uri: Uri): List<NavKey> {
        val query = uri.getQueryParameter("query") ?: ""
        val searchType = uri.getQueryParameter("searchType") ?: "surfy"
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchNavKey(query = query, searchType = searchType.uppercase()), SeriesNavKey(id = id))
    }
}

fun parseDeeplink(uri: Uri?): List<NavKey> {
    if (uri == null) {
        return emptyList()
    }

    val path = uri.pathSegments.firstOrNull() ?: return emptyList()
    val parser = parsers.firstOrNull { it.path == path } ?: return emptyList()
    return parser.parse(uri)
}