package com.bowoon.movie.deeplink

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import com.bowoon.detail.movie.navigation.MovieNavKey
import com.bowoon.favorite.navigation.FavoriteNavKey
import com.bowoon.home.navigation.HomeNavKey
import com.bowoon.my.navigation.MyNavKey

/**
 * copyright https://github.com/android/nav3-recipes/tree/main
 */

/**
 * Movie App Deeplink
 */
internal val deepLinkPatterns: List<DeepLinkPattern<out NavKey>> = listOf(
//    DeepLinkPattern(
//        serializer = HomeNavKey.serializer(),
//        uriPattern = ("movieinfo://movie/favorite/{tab}/search").toUri()
//    ),
    DeepLinkPattern(
        serializer = HomeNavKey.serializer(),
        uriPattern = ("movieinfo://movie/home").toUri()
    ),
    DeepLinkPattern(
        serializer = FavoriteNavKey.serializer(),
        uriPattern = ("movieinfo://movie/favorite?tab={tab}").toUri()
    ),
    DeepLinkPattern(
        serializer = MyNavKey.serializer(),
        uriPattern = ("movieinfo://movie/my").toUri()
    ),
    DeepLinkPattern(
        serializer = MovieNavKey.serializer(),
        uriPattern = ("movieinfo://movie/movie?id={id}").toUri()
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