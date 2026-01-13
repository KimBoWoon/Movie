package com.bowoon.favorite.navigation

import androidx.annotation.Keep
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.bowoon.favorite.FavoriteScreen
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class FavoriteNavKey(
    val tab: Int = 0
) : NavKey {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is FavoriteNavKey
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

fun EntryProviderScope<NavKey>.favoriteEntry(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<FavoriteNavKey> {
        FavoriteScreen(
            goToMovie = goToMovie,
            goToPeople = goToPeople,
            onShowSnackbar = onShowSnackbar,
            initialTab = it.tab
        )
    }
}