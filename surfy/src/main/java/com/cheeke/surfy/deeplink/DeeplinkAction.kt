package com.cheeke.surfy.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.detail.api.movie.MovieNavKey
import com.cheeke.surfy.detail.api.people.PeopleNavKey
import com.cheeke.surfy.detail.api.series.SeriesNavKey
import com.cheeke.surfy.detail.api.tv.TvNavKey
import com.cheeke.surfy.favorite.api.FavoriteContentType
import com.cheeke.surfy.favorite.api.FavoriteNavKey
import com.cheeke.surfy.home.api.HomeNavKey
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.search.api.SearchNavKey

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
        val key = (uri.getQueryParameter("key") ?: "movie").let {
            FavoriteContentType.valueOf(it.uppercase())
        }
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
        return listOf(FavoriteNavKey(tab = FavoriteContentType.PEOPLE), PeopleNavKey(id = id))
    }
}

object OpenFavoriteMovieParser : DeeplinkParser {
    override val path = "open_favorite_movie"
    override fun parse(uri: Uri): List<NavKey> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(FavoriteNavKey(tab = FavoriteContentType.MOVIE), MovieNavKey(id = id))
    }
}

object OpenFavoriteTvParser : DeeplinkParser {
    override val path = "open_favorite_tv"
    override fun parse(uri: Uri): List<NavKey> {
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(FavoriteNavKey(tab = FavoriteContentType.TV), TvNavKey(id = id))
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
        val query = uri.getQueryParameter("query").orEmpty()
        val searchType = uri.getQueryParameter("searchType") ?: "multi"
        return listOf(SearchNavKey(query = query, searchType = SearchType.entries.find { it.name == searchType.uppercase() } ?: SearchType.MULTI))
    }
}

object SearchToMovieParser : DeeplinkParser {
    override val path = "search_to_movie"
    override fun parse(uri: Uri): List<NavKey> {
        val query = uri.getQueryParameter("query").orEmpty()
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchNavKey(query = query, searchType = SearchType.MOVIE), MovieNavKey(id = id))
    }
}

object SearchToPeopleParser : DeeplinkParser {
    override val path = "search_to_people"
    override fun parse(uri: Uri): List<NavKey> {
        val query = uri.getQueryParameter("query").orEmpty()
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchNavKey(query = query, searchType = SearchType.PEOPLE), PeopleNavKey(id = id))
    }
}

object SearchToSeriesParser : DeeplinkParser {
    override val path = "search_to_series"
    override fun parse(uri: Uri): List<NavKey> {
        val query = uri.getQueryParameter("query").orEmpty()
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchNavKey(query = query, searchType = SearchType.SERIES), TvNavKey(id = id))
    }
}

object SearchToTvParser : DeeplinkParser {
    override val path = "search_to_tv"
    override fun parse(uri: Uri): List<NavKey> {
        val query = uri.getQueryParameter("query").orEmpty()
        val id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1
        return listOf(SearchNavKey(query = query, searchType = SearchType.TV), SeriesNavKey(id = id))
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