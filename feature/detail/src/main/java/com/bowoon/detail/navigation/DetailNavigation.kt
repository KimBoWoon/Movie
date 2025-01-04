package com.bowoon.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.bowoon.detail.DetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class DetailRoute(
    val id: Int
)

fun NavController.navigateToDetail(
    id: Int,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = DetailRoute(id)) {
        navOptions()
    }
}

fun NavGraphBuilder.detailScreen(
    navController: NavController,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable<DetailRoute>() {
        DetailScreen(
            navController = navController,
            onShowSnackbar = onShowSnackbar
        )
    }
}