package com.bowoon.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.bowoon.detail.DetailScreen
import com.bowoon.notifications.DEEP_LINK_URI_PATTERN
import kotlinx.serialization.Serializable

@Serializable
data object DetailBaseRoute

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
    navController: NavController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    firebaseLog: (String, String) -> Unit,
    peopleDestination: NavGraphBuilder.() -> Unit
) {
    navigation<DetailBaseRoute>(startDestination = DetailRoute(id = 0)) {
        composable<DetailRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DEEP_LINK_URI_PATTERN
                }
            )
        ) {
            DetailScreen(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
                firebaseLog = firebaseLog
            )
        }
        peopleDestination()
    }
}