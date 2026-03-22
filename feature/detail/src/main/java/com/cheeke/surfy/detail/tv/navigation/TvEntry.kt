package com.cheeke.surfy.detail.tv.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.detail.tv.TvScreen
import com.cheeke.surfy.detail.tv.TvVM
import com.cheeke.surfy.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class TvNavKey(
    val id: Int
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
                factory.create(id = detailRoute.id)
            }
        )
    }
}

fun Navigator.navigateToTv(
    id: Int
) {
    navigate(route = TvNavKey(id = id))
}