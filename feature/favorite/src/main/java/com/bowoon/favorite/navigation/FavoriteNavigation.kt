package com.bowoon.favorite.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.bowoon.favorite.FavoriteScreen
import kotlinx.serialization.Serializable

@Serializable
data object FavoriteRoute

fun NavController.navigateToFavorite(navOptions: NavOptions) = navigate(route = FavoriteRoute, navOptions)

fun NavGraphBuilder.favoriteScreen(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable<FavoriteRoute>() {
        FavoriteScreen(
            goToMovie = goToMovie,
            goToPeople = goToPeople,
            onShowSnackbar = onShowSnackbar
        )
    }
}