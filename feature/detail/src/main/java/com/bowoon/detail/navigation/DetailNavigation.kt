package com.bowoon.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.bowoon.detail.DetailScreen
import kotlinx.serialization.Serializable

@Serializable
data object DetailBaseRoute

@Serializable
data class DetailRoute(
    val id: Int
)

//@Serializable
//data class PeopleRoute(
//    val id: Int
//)

fun NavController.navigateToDetail(
    id: Int,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = DetailRoute(id)) {
        navOptions()
    }
}

//fun NavController.navigateToPeople(
//    id: Int,
//    navOptions: NavOptionsBuilder.() -> Unit = {}
//) {
//    navigate(route = PeopleRoute(id)) {
//        navOptions()
//    }
//}

fun NavGraphBuilder.detailSection(
    navController: NavController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    peopleDestination: NavGraphBuilder.() -> Unit
) {
    navigation<DetailBaseRoute>(startDestination = DetailRoute(id = 0)) {
        composable<DetailRoute>() {
            DetailScreen(
                navController = navController,
                onShowSnackbar = onShowSnackbar
            )
        }
        peopleDestination()
    }
//    composable<DetailRoute>() {
//        DetailScreen(
//            navController = navController,
//            onShowSnackbar = onShowSnackbar
//        )
//    }
}