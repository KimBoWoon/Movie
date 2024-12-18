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
    onMovieClick: (String, String, String) -> Unit
) {
    composable<FavoriteRoute>() {
        FavoriteScreen(onMovieClick)
    }
}