package com.bowoon.series.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.bowoon.series.SeriesScreen
import kotlinx.serialization.Serializable

@Serializable
data class SeriesRoute(
    val id: Int
)

fun NavController.navigateToSeries(
    id: Int,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) = navigate(route = SeriesRoute(id)) {
    navOptions()
}

fun NavGraphBuilder.seriesScreen(
    onBack: () -> Unit,
    goToMovie: (Int) -> Unit
) {
    composable<SeriesRoute>() {
        SeriesScreen(
            onBack = onBack,
            goToMovie = goToMovie
        )
    }
}