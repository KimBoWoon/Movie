package com.bowoon.detail.movie

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.domain.MovieWithFavorite
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.AlternativeTitles
import com.bowoon.model.Image
import com.bowoon.model.Movie
import com.bowoon.model.Series
import com.bowoon.movie.feature.detail.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.components.CreditsComponent
import com.bowoon.ui.components.ImageOverlay
import com.bowoon.ui.components.ImageType
import com.bowoon.ui.components.ImagesComponent
import com.bowoon.ui.components.MediaTitleComponent
import com.bowoon.ui.components.OverviewComponent
import com.bowoon.ui.components.ProductionComponent
import com.bowoon.ui.components.SectionHeader
import com.bowoon.ui.components.SimilarComponent
import com.bowoon.ui.components.SubSectionTitleComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.components.VideosComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp12
import com.bowoon.ui.utils.dp120
import com.bowoon.ui.utils.dp14
import com.bowoon.ui.utils.dp15
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp4
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp62
import com.bowoon.ui.utils.dp8
import com.bowoon.ui.utils.dp92
import com.bowoon.ui.utils.sp10
import kotlinx.coroutines.launch

@Composable
fun MovieScreen(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: MovieVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("DetailScreen", "detail screen start!")

    val movieState by viewModel.movie.collectAsStateWithLifecycle()
    val similarMovies = viewModel.similarMovies.collectAsLazyPagingItems()

    MovieScreen(
        movieState = movieState,
        similarMovies = similarMovies,
        goToMovie = goToMovie,
        goToPeople = goToPeople,
        goToBack = goToBack,
        onShowSnackbar = onShowSnackbar,
        insertFavoriteMovie = viewModel::insertMovie,
        deleteFavoriteMovie = viewModel::deleteMovie,
        restart = viewModel::restart
    )
}

@Composable
fun MovieScreen(
    movieState: MovieState,
    similarMovies: LazyPagingItems<Movie>,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    insertFavoriteMovie: (Movie) -> Unit,
    deleteFavoriteMovie: (Movie) -> Unit,
    restart: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (movieState) {
            is MovieState.Loading -> {
                Log.d("loading...")
                LocalFirebaseLogHelper.current.sendLog(name = "DetailScreen", message = "loading...")

                CircularProgressComponent(
                    modifier = Modifier
                        .testTag(tag = "detailScreenLoading")
                        .align(Alignment.Center)
                )
            }
            is MovieState.Success -> {
                Log.d("$movieState")
                LocalFirebaseLogHelper.current.sendLog(name = "DetailScreen", message = "$movieState")

                var selectedImage by remember { mutableStateOf<Image?>(value = null) }
                var selectedIndex by remember { mutableStateOf<Int?>(value = null) }
                var selectedType by remember { mutableStateOf<ImageType?>(value = null) }
                var overlayVisible by remember { mutableStateOf(value = false) }
                var overlayImageVisible by remember { mutableStateOf(value = false) }
                val scope = rememberCoroutineScope()
                val onSelect: (ImageType, Image, Int) -> Unit = { type, image, index ->
                    selectedType = type
                    selectedImage = image
                    selectedIndex = index
                    overlayVisible = true
                    scope.launch {
                        withFrameNanos {  }
                        overlayImageVisible = true
                    }
                }

                SharedTransitionLayout {
                    MovieDetailComponent(
                        movieState = movieState.movie,
                        similarMovies = similarMovies,
                        goToMovie = goToMovie,
                        goToPeople = goToPeople,
                        goToBack = goToBack,
                        onShowSnackbar = onShowSnackbar,
                        insertFavoriteMovie = insertFavoriteMovie,
                        deleteFavoriteMovie = deleteFavoriteMovie,
                        selectedImage = selectedImage,
                        selectedIndex = selectedIndex,
                        onSelect = onSelect,
                        overlayVisible = overlayVisible,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )

                    ImageOverlay(
                        selectedType = selectedType,
                        selectedImage = selectedImage,
                        selectedIndex = selectedIndex,
                        overlayVisible = overlayVisible,
                        overlayImageVisible = overlayImageVisible,
                        onDismiss = {
                            selectedType = null
                            selectedIndex = null
                            selectedImage = null
                            overlayVisible = false
                            overlayImageVisible = false
                        }
                    )
                }
            }
            is MovieState.Error -> {
                Log.e("${movieState.throwable.message}")
                LocalFirebaseLogHelper.current.sendLog(name = "DetailScreen", message = "${movieState.throwable.message}")

                ConfirmDialog(
                    title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                    message = "${movieState.throwable.message}",
                    confirmPair = stringResource(id = com.bowoon.movie.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(id = com.bowoon.movie.core.ui.R.string.back_message) to goToBack
                )
            }
        }
    }
}

@Composable
fun MovieDetailComponent(
    movieState: MovieWithFavorite,
    similarMovies: LazyPagingItems<Movie>,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    insertFavoriteMovie: (Movie) -> Unit,
    deleteFavoriteMovie: (Movie) -> Unit,
    selectedImage: Image?,
    selectedIndex: Int?,
    overlayVisible: Boolean,
    onSelect: (ImageType, Image, Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope
) {
    val favoriteMessage = if (movieState.isFavorite) stringResource(id = R.string.add_favorite_movie) else stringResource(id = R.string.remove_favorite_movie)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleComponent(
            title = movieState.movie.title ?: "",
            isFavorite = movieState.isFavorite,
            goToBack = goToBack,
            onFavoriteClick = {
                if (movieState.isFavorite) {
                    deleteFavoriteMovie(movieState.movie)
                } else {
                    insertFavoriteMovie(movieState.movie)
                }
                scope.launch {
                    onShowSnackbar(favoriteMessage, null)
                }
            }
        )

        Column(
            modifier = Modifier.verticalScroll(state = scrollState),
            verticalArrangement = Arrangement.spacedBy(space = dp15)
        ) {
            VideosComponent(
                scope = scope,
                vodList = movieState.movie.videos?.results?.mapNotNull { it.key } ?: emptyList(),
                autoPlayTrailer = movieState.autoPlayTrailer
            )

            MediaTitleComponent(media = movieState.movie)
            // 12) (옵션) Watch Providers / Where to watch
//            movieState.watchProviders?.let { watchProvider ->
//                item {
//                    WatchProvidersSection(providers = watchProvider)
//                }
//            }
            movieState.movie.alternativeTitles?.let { alternativeTitles ->
                if (!alternativeTitles.titles.isNullOrEmpty()) {
                    AlternativeTitleComponent(alternativeTitles = alternativeTitles)
                }
            }
            movieState.movie.overview?.takeIf { it.trim().isNotEmpty() }?.let { overview ->
                OverviewComponent(overview = overview)
            }
            movieState.movie.credits?.let { credits ->
                CreditsComponent(
                    credits = credits,
                    goToPeople = goToPeople
                )
            }
            movieState.movie.productionCompanies?.let { productionCompanies ->
                ProductionComponent(companies = productionCompanies)
            }
            movieState.movie.series?.let { series ->
                SeriesComponent(
                    collection = series,
                    goToMovie = goToMovie
                )
            }
            movieState.movie.images?.let { images ->
                val posters = images.posters ?: emptyList()
                val backdrops = images.backdrops ?: emptyList()

                ImagesComponent(
                    backdrops = backdrops,
                    posters = posters,
                    sharedTransitionScope = sharedTransitionScope,
                    selectedImage = selectedImage,
                    selectedIndex = selectedIndex,
                    overlayVisible = overlayVisible,
                    onSelect = onSelect
                )
            }
            if (similarMovies.itemCount > 0) {
                SimilarComponent(
                    similar = similarMovies,
                    goToDestination = goToMovie
                )
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp20))
//            // 11) (옵션) Reviews Preview (2~3개) + See all
//            if (uiState.reviews.isNotEmpty()) {
//                item {
//                    ReviewsPreviewSection(
//                        reviews = uiState.reviews,
//                        onSeeAll = { /* open reviews screen */ }
//                    )
//                }
//            }
        }
    }
}

@Composable
fun AlternativeTitleComponent(alternativeTitles: AlternativeTitles?) {
    var expanded by remember { mutableStateOf(value = false) }
    val titles = alternativeTitles?.titles?.fold(initial = "") { acc, title -> if (acc.isEmpty()) "${title.title}" else "$acc\n${title.title}" } ?: ""

    Column {
        SectionHeader(title = "Alternative Titles")

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dp16),
            text = titles,
            fontSize = sp10,
            maxLines = if (expanded) Int.MAX_VALUE else 1,
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            modifier = Modifier
                .padding(start = dp16, end = dp16, top = dp5)
                .clickable { expanded = !expanded },
            text = if (expanded) "접기" else "더보기",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SeriesComponent(
    collection: Series?,
    goToMovie: (Int) -> Unit,
//    onSeeAll: () -> Unit
) {
    Column {
        SectionHeader(
            title = "Series",
//            actionText = "See all",
//            onActionClick = onSeeAll
        )

        Spacer(modifier = Modifier.height(height = dp12))

        Column(
            modifier = Modifier
                .padding(horizontal = dp16)
                .clip(shape = RoundedCornerShape(size = dp16))
                .background(Color.DarkGray.copy(alpha = 0.25f))
                .fillMaxWidth()
                .padding(all = dp14),
        ) {
            // Collection summary card
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                DynamicAsyncImageLoader(
                    source = collection?.posterPath ?: "",
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = dp62, height = dp92)
                        .clip(RoundedCornerShape(size = dp12))
                        .background(Color.DarkGray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(width = dp12))

                Column(modifier = Modifier.weight(weight = 1f)) {
                    Text(
                        text = collection?.title ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(height = dp4))
                    Text(
                        text = "${collection?.parts?.size} movies",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(height = dp10))
            collection?.overview?.takeIf { it.isNotEmpty() }?.let { overview ->
                Text(
                    text = overview,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        if (!collection?.parts.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(height = dp14))
            SubSectionTitleComponent(text = "Parts")
            Spacer(modifier = Modifier.height(height = dp10))

            LazyRow(
                contentPadding = PaddingValues(horizontal = dp16),
                horizontalArrangement = Arrangement.spacedBy(space = dp12)
            ) {
                items(items = collection.parts ?: emptyList()) { m ->
                    Column(
                        modifier = Modifier
                            .width(width = dp120)
                            .clickable { goToMovie(m.id ?: -1) }
                    ) {
                        DynamicAsyncImageLoader(
                            source = m.posterPath ?: "",
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                                .clip(shape = RoundedCornerShape(size = dp14))
                                .background(Color.DarkGray),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(height = dp8))
                        Text(
                            text = m.title ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            minLines = 2,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        m.voteAverage?.let {
                            Text(
                                text = "★ ${"%.1f".format(it)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun WatchProvidersSection(providers: MovieWatchProviderResult) {
//    Column(modifier = Modifier.padding(vertical = dp14)) {
//        SectionHeader(title = "Where to watch")
//        Spacer(Modifier.height(height = dp12))
//
//        if (!providers.flatrate.isNullOrEmpty()) {
//            SubSectionTitle(text = "스트리밍")
//            LazyRow(contentPadding = PaddingValues(horizontal = dp16)) {
//                items(items = providers.flatrate ?: emptyList()) { p ->
//                    Column(
//                        modifier = Modifier
//                            .width(width = dp92)
//                            .padding(end = dp12),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        DynamicAsyncImageLoader(
//                            source = p.logoPath ?: "",
//                            contentDescription = null,
//                            modifier = Modifier
//                                .size(size = dp64)
//                                .clip(shape = RoundedCornerShape(size = dp16))
//                                .background(Color.DarkGray.copy(alpha = 0.35f))
//                                .padding(all = dp10),
//                            contentScale = ContentScale.Fit
//                        )
//                        Spacer(Modifier.height(height = dp8))
//                        Text(text = p.providerName ?: "", style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
//                    }
//                }
//            }
//        }
//
//        if (!providers.rent.isNullOrEmpty()) {
//            SubSectionTitle(text = "대여")
//            LazyRow(contentPadding = PaddingValues(horizontal = dp16)) {
//                items(items = providers.rent ?: emptyList()) { p ->
//                    Column(
//                        modifier = Modifier
//                            .width(width = dp92)
//                            .padding(end = dp12),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        DynamicAsyncImageLoader(
//                            source = p.logoPath ?: "",
//                            contentDescription = null,
//                            modifier = Modifier
//                                .size(size = dp64)
//                                .clip(shape = RoundedCornerShape(size = dp16))
//                                .background(Color.DarkGray.copy(alpha = 0.35f))
//                                .padding(all = dp10),
//                            contentScale = ContentScale.Fit
//                        )
//                        Spacer(Modifier.height(height = dp8))
//                        Text(text = p.providerName ?: "", style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
//                    }
//                }
//            }
//        }
//
//        if (!providers.buy.isNullOrEmpty()) {
//            SubSectionTitle(text = "구매")
//            LazyRow(contentPadding = PaddingValues(horizontal = dp16)) {
//                items(items = providers.buy ?: emptyList()) { p ->
//                    Column(
//                        modifier = Modifier
//                            .width(width = dp92)
//                            .padding(end = dp12),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        DynamicAsyncImageLoader(
//                            source = p.logoPath ?: "",
//                            contentDescription = null,
//                            modifier = Modifier
//                                .size(size = dp64)
//                                .clip(shape = RoundedCornerShape(size = dp16))
//                                .background(Color.DarkGray.copy(alpha = 0.35f))
//                                .padding(all = dp10),
//                            contentScale = ContentScale.Fit
//                        )
//                        Spacer(Modifier.height(height = dp8))
//                        Text(text = p.providerName ?: "", style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
//                    }
//                }
//            }
//        }
//    }
//}