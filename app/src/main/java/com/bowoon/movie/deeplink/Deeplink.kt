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

internal val deepLinkPatterns: List<DeepLinkPattern<out NavKey>> = listOf(
    DeepLinkPattern(serializer = HomeNavKey.serializer(), uriPattern = ("movieinfo://movie/home").toUri()),
//    DeepLinkPattern(serializer = FavoriteNavKey.serializer(), uriPattern = ("movieinfo://movie/favorite").toUri()),
//    DeepLinkPattern(serializer = FavoriteNavKey.serializer(), uriPattern = ("movieinfo://movie/favorite/movie").toUri()),
//    DeepLinkPattern(serializer = FavoriteNavKey.serializer(), uriPattern = ("movieinfo://movie/favorite/tab/1").toUri()),
    DeepLinkPattern(serializer = FavoriteNavKey.serializer(), uriPattern = ("movieinfo://movie/favorite?tab={tab}").toUri()),
    DeepLinkPattern(serializer = MyNavKey.serializer(), uriPattern = ("movieinfo://movie/my").toUri()),
//    DeepLinkPattern(serializer = MovieNavKey.serializer(), uriPattern = ("movieinfo://movie/detail/movie/{id}").toUri())
    DeepLinkPattern(serializer = MovieNavKey.serializer(), uriPattern = ("movieinfo://movie/movie?id={id}").toUri())
)

fun parseDeepLink(uri: Uri?): NavKey? = uri?.let {
    /** STEP 2. Parse requested deeplink */
    val request = DeepLinkRequest(uri)
    /** STEP 3. Compared requested with supported deeplink to find match*/
    val match = deepLinkPatterns.firstNotNullOfOrNull { pattern ->
        DeepLinkMatcher(request = request, deepLinkPattern = pattern).match()
    }
    /** STEP 4. If match is found, associate match to the correct key*/
    match?.let {
        //leverage kotlinx.serialization's Decoder to decode
        // match result into a backstack key
        KeyDecoder(arguments = match.args).decodeSerializableValue(deserializer = match.serializer)
    }
} /*?: HomeNavKey*/ // fallback if intent. uri is null or match is not found