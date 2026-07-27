package com.cheeke.surfy.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.detail.api.movie.MovieNavKey
import com.cheeke.surfy.detail.api.people.PeopleNavKey
import com.cheeke.surfy.detail.api.series.SeriesNavKey
import com.cheeke.surfy.detail.api.tv.TvNavKey
import com.cheeke.surfy.favorite.api.FavoriteNavKey
import com.cheeke.surfy.home.api.HomeNavKey
import com.cheeke.surfy.search.api.SearchNavKey
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface DeepLinkManager {
    val rootDeeplink: Flow<List<NavKey>>
    val bottomDeeplink: Flow<List<NavKey>>

    fun handleDeepLink(uri: Uri?)
    fun consumeRootDeepLink()
    fun consumeBottomDeepLink()
}

class DeepLinkManagerImpl @Inject constructor() : DeepLinkManager {
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
    override val rootDeeplink = _rootDeeplink.asStateFlow()
    private val _bottomDeeplink = MutableStateFlow<List<NavKey>>(value = emptyList())
    override val bottomDeeplink = _bottomDeeplink.asStateFlow()

    override fun handleDeepLink(uri: Uri?) {
        val stack = parseDeeplink(uri = uri)
        if (stack.isEmpty()) {
            return
        }

        val rootDeepLink = mutableListOf<NavKey>()
        val bottomDeepLink = mutableListOf<NavKey>()

        stack.forEach { route ->
            when {
                bottomNavKeys.any { it.java.simpleName == route::class.java.simpleName } -> bottomDeepLink.add(element = route)
                rootNavKeys.any { it.java.simpleName == route::class.java.simpleName } -> rootDeepLink.add(element = route)
                else -> {
                    Log.d("잘못된 deeplink 입니다. : $route")
                    Firebase.crashlytics.recordException(RuntimeException("잘못된 deeplink 입니다. : $route"))
                }
            }
        }

        _rootDeeplink.value = rootDeepLink
        _bottomDeeplink.value = bottomDeepLink
    }

    override fun consumeRootDeepLink() {
        _rootDeeplink.value = emptyList()
    }

    override fun consumeBottomDeepLink() {
        _bottomDeeplink.value = emptyList()
    }
}