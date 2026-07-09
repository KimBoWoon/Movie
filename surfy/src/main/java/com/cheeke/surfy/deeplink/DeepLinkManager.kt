package com.cheeke.surfy.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.detail.movie.navigation.MovieNavKey
import com.cheeke.surfy.detail.people.navigation.PeopleNavKey
import com.cheeke.surfy.detail.series.navigation.SeriesNavKey
import com.cheeke.surfy.detail.tv.navigation.TvNavKey
import com.cheeke.surfy.favorite.navigation.FavoriteNavKey
import com.cheeke.surfy.home.navigation.HomeNavKey
import com.cheeke.surfy.search.navigation.SearchNavKey
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import jakarta.inject.Inject

interface DeepLinkManager {
    val rootDeeplink: Observable<List<NavKey>>
    val bottomDeeplink: Observable<List<NavKey>>

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
    private val rootDeepLinkSubject = BehaviorSubject.createDefault<List<NavKey>>(emptyList())
    override val rootDeeplink: Observable<List<NavKey>> = rootDeepLinkSubject.hide()
    private val bottomDeepLinkSubject = BehaviorSubject.createDefault<List<NavKey>>(emptyList())
    override val bottomDeeplink: Observable<List<NavKey>> = bottomDeepLinkSubject.hide()

    override fun handleDeepLink(uri: Uri?) {
        val stack = parseDeeplink(uri)

        if (stack.isEmpty()) {
            return
        }

        val rootDeepLink = mutableListOf<NavKey>()
        val bottomDeepLink = mutableListOf<NavKey>()

        stack.forEach { route ->
            when {
                route::class in bottomNavKeys -> bottomDeepLink += route
                route::class in rootNavKeys -> rootDeepLink += route
                else -> {
                    Log.d("잘못된 deeplink 입니다. : $route")
                    Firebase.crashlytics.recordException(
                        RuntimeException("잘못된 deeplink 입니다. : $route")
                    )
                }
            }
        }

        rootDeepLinkSubject.onNext(rootDeepLink)
        bottomDeepLinkSubject.onNext(bottomDeepLink)
    }

    override fun consumeRootDeepLink() {
        rootDeepLinkSubject.onNext(emptyList())
    }

    override fun consumeBottomDeepLink() {
        bottomDeepLinkSubject.onNext(emptyList())
    }
}