package com.bowoon.people.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.bowoon.people.PeopleScreen
import kotlinx.serialization.Serializable

@Serializable
data class PeopleRoute(
    val id: Int
)

fun NavController.navigateToPeople(
    id: Int,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = PeopleRoute(id)) {
        navOptions()
    }
}

fun NavGraphBuilder.peopleScreen(
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    firebaseLog: (String, String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable<PeopleRoute>() {
        PeopleScreen(
            navController = navController,
            onMovieClick = onMovieClick,
            onShowSnackbar = onShowSnackbar,
            firebaseLog = firebaseLog
        )
    }
}