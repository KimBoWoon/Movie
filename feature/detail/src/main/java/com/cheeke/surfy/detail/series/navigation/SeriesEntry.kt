package com.cheeke.surfy.detail.series.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.detail.series.SeriesScreen
import com.cheeke.surfy.detail.series.SeriesVM
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

fun NavBackStack<NavKey>.goToSeries(id: Int) = add(element = SeriesNavKey(id = id))