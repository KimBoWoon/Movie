package com.cheeke.surfy.ui.root.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.ui.root.RootScreen
import kotlinx.serialization.Serializable

@Serializable
object RootNavKey : NavKey

fun EntryProviderScope<NavKey>.rootEntry(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    goToSearch: (String, SearchType) -> Unit,
    showSettingDialog: () -> Unit
) {
    entry<RootNavKey> {
        RootScreen(
            goToMovie = goToMovie,
            goToPeople = goToPeople,
            goToTv = goToTv,
            goToSearch = goToSearch,
            showSettingDialog = showSettingDialog
        )
    }
}