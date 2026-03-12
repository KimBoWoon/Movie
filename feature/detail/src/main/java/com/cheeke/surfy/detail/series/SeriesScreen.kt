package com.cheeke.surfy.detail.series

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cheeke.surfy.analytics.TrackScreenViewEvent
import com.cheeke.surfy.core.network.R
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.SeriesPart
import com.cheeke.surfy.ui.components.CircularProgressComponent
import com.cheeke.surfy.ui.dialog.ConfirmDialog
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.bounceClick
import com.cheeke.surfy.ui.utils.dp1
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp12
import com.cheeke.surfy.ui.utils.dp14
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp18
import com.cheeke.surfy.ui.utils.dp185
import com.cheeke.surfy.ui.utils.dp20
import com.cheeke.surfy.ui.utils.dp22
import com.cheeke.surfy.ui.utils.dp24
import com.cheeke.surfy.ui.utils.dp28
import com.cheeke.surfy.ui.utils.dp340
import com.cheeke.surfy.ui.utils.dp4
import com.cheeke.surfy.ui.utils.dp42
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.dp6
import com.cheeke.surfy.ui.utils.dp7
import com.cheeke.surfy.ui.utils.dp8
import com.cheeke.surfy.ui.utils.dp88
import com.cheeke.surfy.ui.utils.dp9
import com.cheeke.surfy.ui.utils.dp999
import java.time.LocalDate

@Composable
fun SeriesScreen(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    viewModel: SeriesVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("SeriesScreen", "series screen init")
    TrackScreenViewEvent(screenName = "SeriesScreen")

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
                LocalFirebaseLogHelper.current.sendLog("SeriesScreen", "Series state Loading...")

                CircularProgressComponent(
                    modifier = Modifier.semantics { contentDescription = "seriesLoading" }
                        .align(Alignment.Center)
                )
            }
            is SeriesState.Success -> {
                LocalFirebaseLogHelper.current.sendLog("SeriesScreen", "Series state Success")

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = dp10)
                ) {
                    item {
                        SeriesHeroSection(
                            series = seriesState.series,
                            imageList = seriesState.imageList,
                            onBackClick = goToBack
                        )
                    }
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = dp20)
                        ) {
                            seriesState.series.overview?.takeIf { it.isNotEmpty() }?.let { overview ->
                                ExpandableOverviewCard(
                                    overview = overview
                                )
                                Spacer(modifier = Modifier.height(height = dp28))
                            }

                            if (!seriesState.series.parts.isNullOrEmpty()) {
                                Text(
                                    text = "시리즈 타임라인",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(height = dp6))

                                Text(
                                    text = "개봉 순서대로 흐름을 한 번에 볼 수 있어요",
                                    style = MaterialTheme.typography.bodyMedium,
                                )

                                Spacer(modifier = Modifier.height(height = dp20))
                            }
                        }
                    }
                    seriesState.series.parts?.groupBy { LocalDate.parse(it.releaseDate).year.toString() }?.forEach { (year, movies) ->
                        stickyHeader(key = "header_$year") {
                            Column(
                                modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(horizontal = dp20)
                            ) {
                                YearSectionHeader(
                                    year = year,
                                    count = movies.size
                                )
                                Spacer(modifier = Modifier.height(height = dp8))
                            }
                        }

                        items(
                            items = movies,
                            key = { movie -> movie.id ?: -1 }
                        ) { movie ->
                            Column(
                                modifier = Modifier.padding(horizontal = dp10)
                            ) {
                                MovieCard(
                                    movie = movie,
                                    goToMovie = { goToMovie(movie.id ?: -1) }
                                )
                                Spacer(modifier = Modifier.height(height = dp10))
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(height = dp12))
                    }
                }
            }
            is SeriesState.Error -> {
                LocalFirebaseLogHelper.current.sendLog("SeriesScreen", "Series state Error")

                ConfirmDialog(
                    title = stringResource(id = R.string.network_failed),
                    message = seriesState.throwable.message ?: stringResource(id = R.string.something_wrong),
                    confirmPair = stringResource(id = com.cheeke.surfy.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(id = com.cheeke.surfy.core.ui.R.string.back_message) to goToBack
                )
            }
        }
    }
}

@Composable
private fun SeriesHeroSection(
    series: Series,
    imageList: ImageList,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = dp340)
            .clip(shape = RoundedCornerShape(bottomStart = dp28, bottomEnd = dp28))
    ) {
        HeroBackground(
            backdropUrl = series.backdropPath,
            fallbackPosterUrls = if (!imageList.backdrops.isNullOrEmpty()) {
                imageList.backdrops?.mapNotNull { it.filePath } ?: emptyList()
            } else {
                imageList.posters?.mapNotNull { it.filePath } ?: emptyList()
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.6f to Color.Black.copy(alpha = 0.05f),
                            0.8f to Color.Black.copy(alpha = 0.10f),
                            1.0f to MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CircleActionButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                onClick = onBackClick
            )

            Spacer(modifier = Modifier.weight(weight = 1f))

            Column(
                modifier = Modifier.padding(horizontal = dp20, vertical = dp20)
            ) {
                Text(
                    text = series.title ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(height = dp10))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingChip(rating = series.voteAverage?.toDouble() ?: 0.toDouble())
                    Spacer(modifier = Modifier.width(width = dp10))
                    if (!series.parts.isNullOrEmpty()) {
                        Text(
                            text = "${series.parts?.size}편",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(height = dp16))
            }
        }
    }
}

@Composable
private fun HeroBackground(
    backdropUrl: String?,
    fallbackPosterUrls: List<String>,
    modifier: Modifier = Modifier
) {
    when {
        !backdropUrl.isNullOrBlank() -> {
            DynamicAsyncImageLoader(
                source = backdropUrl,
                contentDescription = null,
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }
        fallbackPosterUrls.size >= 3 -> {
            PosterCollageHero(
                posterUrls = fallbackPosterUrls,
                modifier = modifier
            )
        }
        fallbackPosterUrls.isNotEmpty() -> {
            PosterBlurHero(
                posterUrl = fallbackPosterUrls.first(),
                modifier = modifier
            )
        }
        else -> GradientHero(modifier = modifier)
    }
}

@Composable
private fun PosterBlurHero(
    posterUrl: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        DynamicAsyncImageLoader(
            source = posterUrl,
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .blur(radius = dp16),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.6f to Color.Black.copy(alpha = 0.05f),
                            0.8f to Color.Black.copy(alpha = 0.10f),
                            1.0f to MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        DynamicAsyncImageLoader(
            source = posterUrl,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = dp24, top = dp28)
                .height(height = dp185)
                .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                .clip(RoundedCornerShape(size = dp22))
                .border(
                    width = dp1,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(size = dp22)
                ),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun PosterCollageHero(
    posterUrls: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.background(
            Brush.verticalGradient(
                colorStops = arrayOf(
                    0.6f to Color.Black.copy(alpha = 0.05f),
                    0.8f to Color.Black.copy(alpha = 0.10f),
                    1.0f to MaterialTheme.colorScheme.background
                )
            )
        )
    ) {
        posterUrls.forEachIndexed { index, url ->
            DynamicAsyncImageLoader(
                source = url,
                contentDescription = null,
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxHeight()
                    .alpha(alpha = if (index % 2 == 0) 0.55f else 0.38f),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun GradientHero(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colorStops = arrayOf(
                    0.6f to Color.Black.copy(alpha = 0.05f),
                    0.8f to Color.Black.copy(alpha = 0.10f),
                    1.0f to MaterialTheme.colorScheme.background
                )
            )
        )
    )
}

@Composable
private fun CircleActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    highlighted: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.size(size = dp42),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun RatingChip(
    rating: Double
) {
    Surface(
        shape = RoundedCornerShape(size = dp999),
        color = Color(color = 0x33F4C15D),
        contentColor = Color(color = 0xFFF4C15D)
    ) {
        Text(
            text = "★ ${"%.1f".format(rating)}",
            modifier = Modifier.padding(horizontal = dp12, vertical = dp7),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ExpandableOverviewCard(
    overview: String
) {
    var expanded by rememberSaveable { mutableStateOf(value = false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = dp20),
    ) {
        Column(
            modifier = Modifier.padding(all = dp18)
        ) {
            Text(
                text = "시리즈 소개",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(height = dp10))

            Text(
                text = overview,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(height = dp12))

            Surface(
                onClick = { expanded = !expanded },
                shape = RoundedCornerShape(size = dp999),
                color = Color(color = 0x33F4C15D),
                contentColor = Color(color = 0xFFF4C15D)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = dp14, vertical = dp9),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) "접기" else "더보기",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(width = dp4))
                    Icon(
                        imageVector = Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(size = dp18)
                    )
                }
            }
        }
    }
}

@Composable
private fun YearSectionHeader(
    year: String,
    count: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dp10),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = year,
            style = MaterialTheme.typography.headlineSmall,
            color = Color(color = 0xFFF4C15D),
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.width(width = dp8))

        Text(
            text = "• ${count}편",
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.width(width = dp12))

        HorizontalDivider(
            modifier = Modifier.weight(weight = 1f),
            thickness = dp1,
            color = Color(color = 0xFFF4C15D).copy(alpha = 0.28f)
        )
    }
}

@Composable
private fun MovieCard(
    movie: SeriesPart,
    goToMovie: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().bounceClick { goToMovie() },
        shape = RoundedCornerShape(size = dp22),
        border = BorderStroke(width = dp1, color = Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(all = dp10),
            horizontalArrangement = Arrangement.Center
        ) {
            MoviePoster(
                posterUrl = movie.posterPath,
                modifier = Modifier
                    .width(width = dp88)
                    .aspectRatio(ratio = POSTER_IMAGE_RATIO)
            )

            Spacer(modifier = Modifier.width(width = dp5))

            Column(
                modifier = Modifier.weight(weight = 1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = movie.title ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★ ${"%.1f".format(movie.voteAverage ?: 0f)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(color = 0xFFF4C15D),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(width = dp8))
                    Text(
                        text = movie.releaseDate ?: "",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                Text(
                    text = movie.overview ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MoviePoster(
    posterUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = dp16))
            .background(color = Color(color = 0xFF1B1F27)),
        contentAlignment = Alignment.Center
    ) {
        if (posterUrl.isNullOrBlank()) {
            Text(
                text = "No Image",
                style = MaterialTheme.typography.labelMedium,
            )
        } else {
            DynamicAsyncImageLoader(
                source = posterUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}