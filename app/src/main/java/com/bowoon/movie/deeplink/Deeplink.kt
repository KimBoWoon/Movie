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
 * 경로를 나타내는 부분 고민이 필요함
 *
 * 예를 들어 찜 내비게이션에서 인물 탭으로 이동 후 검색으로 이동했을 때
 * 현재 이런 경로가 옴 -> https://www.bowoon.movie.com/favoritePeople/search?query=미션&searchType=movie
 *
 * 인물탭을 나타내는 경로는 favoritePeople이고 이게 맞을지는 다시 생각해봐야함
 *
 * @param uri Deeplink로 전달받은 URI
 * @return 딥링크 경로를 담은 리스트
 */
fun parseDeeplink(uri: Uri?): List<NavKey> = uri?.let {
    // 경로와 도착지를 담아 리스트로 반환
    uri.parseDeeplinkPath().plus(element = uri.getDeeplinkDestination(destination = uri.lastPathSegment))
} ?: emptyList()

/**
 * 딥링크 경로 파싱
 *
 * @return 경로를 담은 리스트 반환
 */
fun Uri?.parseDeeplinkPath(): List<NavKey> {
    if (this == null) {
        return emptyList()
    }

    val paths = pathSegments.dropLast(n = 1)

    if (paths.isEmpty()) {
        return emptyList()
    }

    return buildList {
        paths.forEach { path ->
            when {
                path == "home" -> add(HomeNavKey)
                path == "favoriteMovie" -> add(FavoriteNavKey(tab = 0))
                path == "favoritePeople" -> add(FavoriteNavKey(tab = 1))
                path == "my" -> add(MyNavKey)
                path.split("_").first() == "search" -> {
                    val query = path.split("_")[1]
                    val searchType = path.split("_")[2]

                    add(SearchNavKey(query = query, searchType = searchType))
                }
                path.split("_").first() == "movie" -> {
                    val id = path.split("_")[1].toIntOrNull()
                    add(MovieNavKey(id = id ?: -1))
                }
                path.split("_").first() == "people" -> {
                    val id = path.split("_")[1].toIntOrNull()
                    add(PeopleNavKey(id = id ?: -1))
                }
                path.split("_").first() == "series" -> {
                    val id = path.split("_")[1].toIntOrNull()
                    add(SeriesNavKey(id = id ?: -1))
                }
            }
        }
    }
}

/**
 * 딥링크 도착지를 반환하는 함수
 *
 * @param destination 도작지
 * @return 도착지 Navkey를 반환
 */
fun Uri?.getDeeplinkDestination(destination: String?): NavKey {
    if (this == null) {
        return HomeNavKey
    }

    return when (destination) {
        "home" -> HomeNavKey
        "favorite" -> FavoriteNavKey(tab = getQueryParameter("tab")?.toIntOrNull() ?: 0)
        "my" -> MyNavKey
        "search" -> SearchNavKey(
            query = getQueryParameter("query") ?: "",
            searchType = getQueryParameter("searchType") ?: ""
        )
        "movie" -> MovieNavKey(id = getQueryParameter("id")?.toIntOrNull() ?: -1)
        "people" -> PeopleNavKey(id = getQueryParameter("id")?.toIntOrNull() ?: -1)
        "series" -> SeriesNavKey(id = getQueryParameter("id")?.toIntOrNull() ?: -1)
        else -> HomeNavKey
    }
}