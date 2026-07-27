package com.cheeke.surfy.home.impl

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.home.api.HomeNavKey

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