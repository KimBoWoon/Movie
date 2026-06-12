package com.cheeke.surfy.favorite.navigation

import androidx.annotation.Keep
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.favorite.FavoriteScreen
import com.cheeke.surfy.favorite.FavoriteVM
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class FavoriteNavKey(
    val tab: String = "movie"
) : NavKey

fun EntryProviderScope<NavKey>.favoriteEntry(
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<FavoriteNavKey> { navKey ->
        val viewModel = hiltViewModel<FavoriteVM, FavoriteVM.Factory>(
            key = FavoriteVM.TAG
        ) { factory ->
            factory.create(tab = navKey.tab)
        }

        FavoriteScreen(
            goToMovie = goToMovie,
            goToTv = goToTv,
            goToPeople = goToPeople,
            onShowSnackbar = onShowSnackbar,
            viewModel = viewModel
        )
    }
}