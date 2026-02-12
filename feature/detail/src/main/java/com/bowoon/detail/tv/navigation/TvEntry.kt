package com.bowoon.detail.tv.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.bowoon.detail.tv.TvScreen
import com.bowoon.detail.tv.TvVM
import com.bowoon.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class TvNavKey(
    val id: Int,
    val tab: Int = 0
) : NavKey

fun EntryProviderScope<NavKey>.tvEntry(
    goToBack: () -> Unit,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<TvNavKey> { detailRoute ->
        TvScreen(
            goToBack = goToBack,
            goToTv = goToTv,
            goToPeople = goToPeople,
            onShowSnackbar = onShowSnackbar,
            viewModel = hiltViewModel<TvVM, TvVM.Factory>(
                key = detailRoute.id.toString(),
            ) { factory ->
                factory.create(id = detailRoute.id, initialTabIndex = detailRoute.tab)
            }
        )
    }
}

fun Navigator.navigateToTv(
    id: Int
) {
    navigate(route = TvNavKey(id = id))
}