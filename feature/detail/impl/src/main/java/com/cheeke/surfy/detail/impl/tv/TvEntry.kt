package com.cheeke.surfy.detail.impl.tv

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.detail.api.tv.TvNavKey

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

fun NavBackStack<NavKey>.goToTv(id: Int) = add(element = TvNavKey(id = id))