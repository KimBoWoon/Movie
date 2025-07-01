package com.bowoon.series

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.MovieSeries
import com.bowoon.movie.feature.series.R
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.components.movieSeriesListComponent
import com.bowoon.ui.components.seriesInfoComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp16

@Composable
fun SeriesScreen(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    viewModel: SeriesVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("SeriesScreen", "series screen init")

    val seriesState by viewModel.series.collectAsStateWithLifecycle()

    SeriesScreen(
        seriesState = seriesState,
        restart = viewModel::restart,
        goToBack = goToBack,
        goToMovie = goToMovie
    )
}

@Composable
fun SeriesScreen(
    seriesState: SeriesState,
    restart: () -> Unit,
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (seriesState) {
            is SeriesState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.semantics { contentDescription = "seriesLoading" }
                        .align(Alignment.Center)
                )
            }
            is SeriesState.Success -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TitleComponent(
                        title = seriesState.series.name ?: stringResource(R.string.title_series),
                        goToBack = { goToBack() }
                    )
                    SeriesComponent(
                        series = seriesState.series,
                        goToMovie = goToMovie
                    )
                }
            }
            is SeriesState.Error -> {
                LocalFirebaseLogHelper.current.sendLog("SeriesScreen", "Series state Error")

                ConfirmDialog(
                    title = stringResource(com.bowoon.movie.core.network.R.string.network_failed),
                    message = seriesState.throwable.message ?: stringResource(com.bowoon.movie.core.network.R.string.something_wrong),
                    confirmPair = stringResource(com.bowoon.movie.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(com.bowoon.movie.core.ui.R.string.confirm_message) to goToBack
                )
            }
        }
    }
}

@Composable
fun SeriesComponent(
    series: MovieSeries,
    goToMovie: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.semantics { contentDescription = "seriesList" }.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = dp16, vertical = dp10),
        verticalArrangement = Arrangement.spacedBy(dp10)
    ) {
        seriesInfoComponent(series = series)
        movieSeriesListComponent(
            series = series.parts ?: emptyList(),
            goToMovie = goToMovie
        )
    }
}