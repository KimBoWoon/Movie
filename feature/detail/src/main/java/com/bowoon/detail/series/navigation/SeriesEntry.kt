package com.bowoon.detail.series.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.bowoon.detail.series.SeriesScreen
import com.bowoon.detail.series.SeriesVM
import com.bowoon.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class SeriesNavKey(
    val id: Int
) : NavKey

fun EntryProviderScope<NavKey>.seriesEntry(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit
) {
    entry<SeriesNavKey> { seriesRoute ->
        SeriesScreen(
            goToBack = goToBack,
            goToMovie = goToMovie,
            viewModel = hiltViewModel<SeriesVM, SeriesVM.Factory>(
                key = seriesRoute.id.toString(),
            ) { factory ->
                factory.create(id = seriesRoute.id)
            }
        )
    }
}

fun Navigator.navigateToSeries(
    id: Int
) {
    navigate(route = SeriesNavKey(id = id))
}