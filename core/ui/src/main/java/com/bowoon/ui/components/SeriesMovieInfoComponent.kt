package com.bowoon.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.model.MovieSeriesPart
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp13
import com.bowoon.ui.utils.sp20
import org.threeten.bp.LocalDate

fun LazyListScope.movieSeriesListComponent(
    series: List<MovieSeriesPart>,
    posterUrl: String,
    goToMovie: (Int) -> Unit
) {
    items(
        items = series.sortedBy { it.releaseDate?.trim()?.takeIf { date -> date.isNotEmpty() }?.let { date -> LocalDate.parse(date) } },
        key = { movieSeries -> movieSeries.id ?: -1 }
    ) { movieSeries ->
        SeriesMovieInfoComponent(
            seriesPart = movieSeries,
            posterUrl = posterUrl,
            goToMovie = goToMovie
        )
    }
}

@Composable
fun SeriesMovieInfoComponent(
    seriesPart: MovieSeriesPart,
    posterUrl: String,
    goToMovie: (Int) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { goToMovie(seriesPart.id ?: -1) }
    ) {
        val poster = createRef()
        val title = createRef()
        val date = createRef()
        val overview = createRef()

        createVerticalChain(title, date, overview, chainStyle = ChainStyle.Packed(0f))

        Box(
            modifier = Modifier
                .width(dp150)
                .aspectRatio(POSTER_IMAGE_RATIO)
                .constrainAs(poster) {
                    width = Dimension.value(dp150)
                    height = Dimension.ratio("2:3")
                }
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier.fillMaxSize(),
                source = "$posterUrl${seriesPart.posterPath}",
                contentDescription = "$posterUrl${seriesPart.posterPath}"
            )
        }
        Text(
            modifier = Modifier.constrainAs(title) {
                start.linkTo(anchor = poster.end, margin = dp5)
                end.linkTo(anchor = parent.end)
                width = Dimension.fillToConstraints
            },
            text = seriesPart.title ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = sp20,
            fontWeight = FontWeight.Bold
        )
        seriesPart.releaseDate?.takeIf { it.isNotEmpty() }?.let {
            Text(
                modifier = Modifier.constrainAs(date) {
                    start.linkTo(anchor = poster.end, margin = dp5)
                    end.linkTo(anchor = parent.end)
                    top.linkTo(anchor = title.bottom, margin = dp5)
                    bottom.linkTo(anchor = overview.top, margin = dp5)
                    width = Dimension.fillToConstraints
                },
                text = it,
                fontSize = sp10
            )
        }
        Text(
            modifier = Modifier.constrainAs(overview) {
                start.linkTo(anchor = poster.end, margin = dp5)
                end.linkTo(anchor = parent.end)
                top.linkTo(anchor = date.bottom)
                bottom.linkTo(anchor = parent.bottom)
                width = Dimension.preferredWrapContent
                height = Dimension.fillToConstraints
            },
            text = seriesPart.overview ?: "",
            overflow = TextOverflow.Ellipsis,
            fontSize = sp13,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            textAlign = TextAlign.Justify
        )
    }
}