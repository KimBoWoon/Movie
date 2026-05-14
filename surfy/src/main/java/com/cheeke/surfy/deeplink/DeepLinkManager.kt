package com.cheeke.surfy.deeplink

import android.net.Uri
import com.cheeke.surfy.navigation.FavoriteScreen
import com.cheeke.surfy.navigation.HomeScreen
import com.cheeke.surfy.navigation.MovieScreen
import com.cheeke.surfy.navigation.PeopleScreen
import com.cheeke.surfy.navigation.SearchScreen
import com.cheeke.surfy.navigation.SeriesScreen
import com.cheeke.surfy.navigation.TvScreen
import com.slack.circuit.runtime.screen.Screen
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class DeepLinkManager @Inject constructor() {
    private val rootNavKeys = setOf(
        MovieScreen::class.java.simpleName,
        TvScreen::class.java.simpleName,
        PeopleScreen::class.java.simpleName,
        SeriesScreen::class.java.simpleName,
        SearchScreen::class.java.simpleName
    )
    private val bottomNavKeys = setOf(
        HomeScreen::class.java.simpleName,
        FavoriteScreen::class.java.simpleName
    )

    private val _rootDeeplink = MutableStateFlow<List<Screen>>(value = emptyList())
    val rootDeeplink = _rootDeeplink.asStateFlow()
    private val _bottomDeeplink = MutableStateFlow<List<Screen>>(value = emptyList())
    val bottomDeeplink = _bottomDeeplink.asStateFlow()

    fun handleDeepLink(uri: Uri?) {
        val stack = parseDeeplink(uri = uri)
        if (stack.isEmpty()) {
            return
        }

        val rootDeepLink = mutableListOf<Screen>()
        val bottomDeepLink = mutableListOf<Screen>()

        stack.forEach { route ->
            when {
                bottomNavKeys.any { it == route::class.java.simpleName } -> bottomDeepLink.add(element = route)
                rootNavKeys.any { it == route::class.java.simpleName } -> rootDeepLink.add(element = route)
                else -> throw RuntimeException("잘못된 deeplink 입니다. : $route")
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