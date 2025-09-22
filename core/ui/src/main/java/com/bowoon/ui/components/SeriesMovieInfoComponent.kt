package com.bowoon.ui.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.constraintlayout.compose.layoutId
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.model.SeriesPart
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.sp12
import com.bowoon.ui.utils.sp13
import com.bowoon.ui.utils.sp20

fun LazyListScope.movieSeriesListComponent(
    series: List<SeriesPart>,
    goToMovie: (Int) -> Unit
) {
    items(
        items = series,
        key = { movieSeries -> movieSeries.id ?: -1 }
    ) { movieSeries ->
        SeriesMovieInfoComponent(
            seriesPart = movieSeries,
            goToMovie = goToMovie
        )
    }
}

@Composable
fun SeriesMovieInfoComponent(
    seriesPart: SeriesPart,
    goToMovie: (Int) -> Unit
) {
    Layout(
        modifier = Modifier.bounceClick(onClick = { goToMovie(seriesPart.id ?: -1) }),
        content = {
            DynamicAsyncImageLoader(
                modifier = Modifier
                    .layoutId(layoutId = "SeriesPartImage")
                    .width(width = dp150)
                    .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                    .clip(shape = RoundedCornerShape(size = dp10)),
                source = seriesPart.posterPath ?: "",
                contentDescription = seriesPart.posterPath
            )
            Text(
                modifier = Modifier
                    .layoutId(layoutId = "SeriesPartTitle"),
                text = seriesPart.title ?: "",
                fontSize = sp20,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                maxLines = 1
            )
            Text(
                modifier = Modifier
                    .layoutId(layoutId = "SeriesPartReleaseDate"),
                text = seriesPart.releaseDate ?: "",
                fontSize = sp12,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                maxLines = 1
            )
            Text(
                modifier = Modifier
                    .layoutId(layoutId = "SeriesPartOverview")
                    .semantics { contentDescription = "seriesPartOverview" },
                text = seriesPart.overview ?: "",
                overflow = TextOverflow.Ellipsis,
                fontSize = sp13,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
        }
    ) { measurables, constraints ->
        val imageEndPadding = 15
        val image = measurables.first { it.layoutId == "SeriesPartImage" }.measure(constraints)
        val title = measurables.first { it.layoutId == "SeriesPartTitle" }.measure(Constraints(maxWidth = constraints.maxWidth - image.width - imageEndPadding))
        val releaseDate = measurables.first { it.layoutId == "SeriesPartReleaseDate" }.measure(Constraints(maxWidth = constraints.maxWidth - image.width - imageEndPadding))
        val overview = measurables.first { it.layoutId == "SeriesPartOverview" }.measure(Constraints(minWidth = 0, maxWidth = constraints.maxWidth - image.width - imageEndPadding, maxHeight = image.height - title.height - releaseDate.height))

        layout(width = constraints.maxWidth, height = image.height) {
            image.placeRelative(x = 0, y = 0)
            title.placeRelative(x = image.width + imageEndPadding, y = 0)
            releaseDate.placeRelative(x = image.width + imageEndPadding, y = title.height)
            overview.placeRelative(x = image.width + imageEndPadding, y = title.height + releaseDate.height)
        }
    }
}