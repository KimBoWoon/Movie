package com.cheeke.surfy.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.detail.movie.navigation.MovieNavKey
import com.cheeke.surfy.detail.people.navigation.PeopleNavKey
import com.cheeke.surfy.detail.series.navigation.SeriesNavKey
import com.cheeke.surfy.detail.tv.navigation.TvNavKey
import com.cheeke.surfy.favorite.navigation.FavoriteNavKey
import com.cheeke.surfy.home.navigation.HomeNavKey
import com.cheeke.surfy.search.navigation.SearchNavKey
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class DeepLinkManager @Inject constructor() {
    private val rootNavKeys = setOf(
        MovieNavKey::class,
        TvNavKey::class,
        PeopleNavKey::class,
        SeriesNavKey::class,
        SearchNavKey::class
    )
    private val bottomNavKeys = setOf(
        HomeNavKey::class,
        FavoriteNavKey::class
    )

    private val _rootDeeplink = MutableStateFlow<List<NavKey>>(value = emptyList())
    val rootDeeplink = _rootDeeplink.asStateFlow()
    private val _bottomDeeplink = MutableStateFlow<List<NavKey>>(value = emptyList())
    val bottomDeeplink = _bottomDeeplink.asStateFlow()

    fun handleDeepLink(uri: Uri?) {
        val stack = parseDeeplink(uri = uri)
        if (stack.isEmpty()) {
            return
        }

        val rootDeepLink = mutableListOf<NavKey>()
        val bottomDeepLink = mutableListOf<NavKey>()

        stack.forEach { route ->
            if (bottomNavKeys.any { it.java.simpleName == route::class.java.simpleName }) {
                bottomDeepLink.add(element = route)
            } else {
                rootDeepLink.add(element = route)
            }
        }

        _rootDeeplink.value = rootDeepLink
        _bottomDeeplink.value = bottomDeepLink
    }

    fun consumeRootDeepLink() {
        _rootDeeplink.value = emptyList()
    }

    fun consumeBottomDeepLink() {
        _bottomDeeplink.value = emptyList()
    }
}