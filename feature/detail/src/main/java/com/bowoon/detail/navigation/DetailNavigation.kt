package com.bowoon.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.bowoon.detail.DetailScreen
import com.bowoon.notifications.DEEP_LINK_URI_PATTERN
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

fun NavGraphBuilder.detailSection(
    onBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable<DetailRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEP_LINK_URI_PATTERN
            }
        )
    ) {
        DetailScreen(
            onBack = onBack,
            goToMovie = goToMovie,
            goToPeople = goToPeople,
            goToSeries = goToSeries,
            onShowSnackbar = onShowSnackbar,
        )
    }
}