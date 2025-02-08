package com.bowoon.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.bowoon.home.HomeScreen
import com.bowoon.notifications.DEEP_LINK_URI_PATTERN
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object HomeBaseRoute

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HomeRoute, navOptions)

fun NavGraphBuilder.homeSection(
    onMovieClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    firebaseLog: (String, String) -> Unit,
    detailDestination: NavGraphBuilder.() -> Unit
) {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DEEP_LINK_URI_PATTERN
                }
            )
        ) {
            HomeScreen(
                onMovieClick = onMovieClick,
                onShowSnackbar = onShowSnackbar,
                firebaseLog = firebaseLog
            )
        }
        detailDestination()
    }
}