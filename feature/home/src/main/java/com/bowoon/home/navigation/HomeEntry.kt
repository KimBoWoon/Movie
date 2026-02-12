package com.bowoon.home.navigation

import androidx.annotation.Keep
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.bowoon.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
@Keep
data object HomeNavKey : NavKey

fun EntryProviderScope<NavKey>.homeEntry(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToTv: (Int) -> Unit
) {
    entry<HomeNavKey> {
        HomeScreen(
            goToMovie = goToMovie,
            goToPeople = goToPeople,
            goToTv = goToTv
        )
    }
}