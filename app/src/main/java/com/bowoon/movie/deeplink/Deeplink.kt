package com.bowoon.movie.deeplink

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import com.bowoon.detail.movie.navigation.MovieNavKey
import com.bowoon.detail.people.navigation.PeopleNavKey
import com.bowoon.detail.series.navigation.SeriesNavKey
import com.bowoon.favorite.navigation.FavoriteNavKey
import com.bowoon.home.navigation.HomeNavKey
import com.bowoon.my.navigation.MyNavKey
import com.bowoon.search.navigation.SearchNavKey

/**
 * copyright https://github.com/android/nav3-recipes/tree/main
 */

/**
 * Movie App Deeplink
 */
internal val deepLinkPatterns: List<DeepLinkPattern<out NavKey>> = listOf(
    DeepLinkPattern(
        serializer = FavoriteNavKey.serializer(),
        uriPattern = "https://www.bowoon.movie.com/favorite/{tab}".toUri()
    ),
    DeepLinkPattern(
        serializer = HomeNavKey.serializer(),
        uriPattern = "https://www.bowoon.movie.com/home".toUri()
    ),
    DeepLinkPattern(
        serializer = FavoriteNavKey.serializer(),
        uriPattern = "https://www.bowoon.movie.com/favorite?tab={tab}".toUri()
    ),
    DeepLinkPattern(
        serializer = MyNavKey.serializer(),
        uriPattern = "https://www.bowoon.movie.com/my".toUri()
    ),
    DeepLinkPattern(
        serializer = MovieNavKey.serializer(),
        uriPattern = "https://www.bowoon.movie.com/movie?id={id}".toUri()
    ),
    DeepLinkPattern(
        serializer = SearchNavKey.serializer(),
        uriPattern = "https://www.bowoon.movie.com/search?query={query}".toUri()
    )
)

/**
 * Deeplink parse
 *
 * @param uri Deeplink로 전달받은 URI
 */
fun parseDeepLink(uri: Uri?): NavKey = uri?.let {
    /** Parse requested deeplink */
    val request = DeepLinkRequest(uri)

    // 요청된 딥링크와 패턴을 비교하여 일치하는 패턴을 찾음
    val match = deepLinkPatterns.firstNotNullOfOrNull { pattern ->
        DeepLinkMatcher(request = request, deepLinkPattern = pattern).match()
    }
    // 일치하는 항목을 찾으면 NavKey로 변환
    match?.let {
        // kotlinx.serialization's Decoder를 사용하여 디코딩
        // 결과를 백스택 키로 일치 시키기
        KeyDecoder(arguments = match.args).decodeSerializableValue(deserializer = match.serializer)
    }
} ?: HomeNavKey // fallback if intent. uri is null or match is not found

/**
 * Deeplink parse
 *
 * path는 딥링크 경로, query는 매개변수
 *
 * @param uri Deeplink로 전달받은 URI
 */
fun parseDeeplink(uri: Uri?): List<NavKey> = uri?.let {
    buildList {
        var index = 0
        val pathSegments = uri.pathSegments ?: emptyList()

        while (index < pathSegments.size) {
            when (pathSegments[index]) {
                "home" -> {
                    add(HomeNavKey)
                    index++
                }
                "favorite" -> {
                    val query = uri.getQueryParameter("tab")?.toIntOrNull()
                    val path = if (index + 1 < pathSegments.size) pathSegments[index + 1].toIntOrNull() else 0
                    val tabIndex = query ?: path

                    if (tabIndex == null) {
                        add(FavoriteNavKey(tab = 0))
                        index++
                    } else {
                        add(FavoriteNavKey(tab = tabIndex))
                        index += 2
                    }
                }
                "my" -> {
                    add(MyNavKey)
                    index++
                }
                "search" -> {
                    add(
                        SearchNavKey(
                            query = uri.getQueryParameter("query") ?: "",
                            searchType = uri.getQueryParameter("searchType") ?: ""
                        )
                    )
                    index++
                }
                "movie" -> {
                    add(MovieNavKey(id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1))
                    index++
                }
                "people" -> {
                    add(PeopleNavKey(id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1))
                    index++
                }
                "series" -> {
                    add(SeriesNavKey(id = uri.getQueryParameter("id")?.toIntOrNull() ?: -1))
                    index++
                }
            }
        }
    }
} ?: emptyList()