package com.bowoon.series

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.MovieSeries
import com.bowoon.movie.feature.series.R
import com.bowoon.ui.components.SeriesMovieInfoComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp5
import org.threeten.bp.LocalDate

@Composable
fun SeriesScreen(
    onBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    viewModel: SeriesVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("SeriesScreen", "series screen init")

    val seriesState by viewModel.series.collectAsStateWithLifecycle()

    SeriesScreen(
        seriesState = seriesState,
        onBack = onBack,
        goToMovie = goToMovie
    )
}

@Composable
fun SeriesScreen(
    seriesState: SeriesState,
    onBack: () -> Unit,
    goToMovie: (Int) -> Unit
) {
    var series by remember { mutableStateOf<MovieSeries>(MovieSeries()) }

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
            is SeriesState.Success -> series = seriesState.series
            is SeriesState.Error -> {
                LocalFirebaseLogHelper.current.sendLog("SeriesScreen", "Series state Error")

                ConfirmDialog(
                    title = stringResource(com.bowoon.movie.core.network.R.string.network_failed),
                    message = seriesState.throwable.message ?: stringResource(com.bowoon.movie.core.network.R.string.something_wrong),
                    confirmPair = stringResource(com.bowoon.movie.core.ui.R.string.retry_message) to {},
                    dismissPair = stringResource(com.bowoon.movie.core.ui.R.string.confirm_message) to {}
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TitleComponent(
                title = series.name ?: stringResource(R.string.title_series),
                onBackClick = { onBack() }
            )
            SeriesComponent(
                series = series,
                goToMovie = goToMovie
            )
        }
    }
}

@Composable
fun SeriesComponent(
    series: MovieSeries,
    goToMovie: (Int) -> Unit
) {
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()

    LazyColumn(
        modifier = Modifier.semantics { contentDescription = "seriesList" }.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = dp16, vertical = dp10),
        verticalArrangement = Arrangement.spacedBy(dp10)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dp5)
            ) {
                DynamicAsyncImageLoader(
                    modifier = Modifier.width(dp150).aspectRatio(POSTER_IMAGE_RATIO),
                    source = "$posterUrl${series.posterPath}",
                    contentDescription = "$posterUrl${series.posterPath}"
                )
                series.overview?.takeIf { it.isNotEmpty() }?.let {
                    Text(
                        modifier = Modifier.semantics { contentDescription = "seriesOverview" },
                        text = it
                    )
                }
            }
        }
        items(
            items = series.parts?.sortedBy { it?.releaseDate?.trim()?.takeIf { date -> date.isNotEmpty() }?.let { date -> LocalDate.parse(date) } } ?: emptyList(),
            key = { seriesMovie -> seriesMovie?.id ?: -1 }
        ) { seriesMovie ->
            seriesMovie?.let {
                SeriesMovieInfoComponent(
                    movieSeriesPart = it,
                    posterUrl = posterUrl,
                    goToMovie = goToMovie
                )
            }
        }
    }
}