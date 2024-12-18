package com.bowoon.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.bowoon.detail.DetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class DetailRoute(
    val openDt: String,
    val movieCd: String,
    val title: String
)

fun NavController.navigateToDetail(
    openDt: String,
    movieCd: String,
    title: String,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = DetailRoute(openDt, movieCd, title)) {
        navOptions()
    }
}

fun NavGraphBuilder.detailScreen() {
    composable<DetailRoute>() {
        DetailScreen()
    }
}