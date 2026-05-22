package com.cheeke.surfy.detail.movie

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
import com.cheeke.surfy.analytics.LocalAnalyticsHelper
import com.cheeke.surfy.analytics.TrackScreenViewEvent
import com.cheeke.surfy.analytics.logFavorite
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
import com.cheeke.surfy.feature.detail.R
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.model.AlternativeTitle
import com.cheeke.surfy.model.Image
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.ui.components.CircularProgressComponent
import com.cheeke.surfy.ui.components.CreditsComponent
import com.cheeke.surfy.ui.components.ImageOverlay
import com.cheeke.surfy.ui.components.ImageType
import com.cheeke.surfy.ui.components.ImagesComponent
import com.cheeke.surfy.ui.components.MediaTitleComponent
import com.cheeke.surfy.ui.components.OverviewComponent
import com.cheeke.surfy.ui.components.ProductionComponent
import com.cheeke.surfy.ui.components.SectionHeader
import com.cheeke.surfy.ui.components.SimilarComponent
import com.cheeke.surfy.ui.components.SubSectionTitleComponent
import com.cheeke.surfy.ui.components.TitleComponent
import com.cheeke.surfy.ui.components.VideosComponent
import com.cheeke.surfy.ui.dialog.ConfirmDialog
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.bounceClick
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp12
import com.cheeke.surfy.ui.utils.dp120
import com.cheeke.surfy.ui.utils.dp14
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp20
import com.cheeke.surfy.ui.utils.dp4
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.dp62
import com.cheeke.surfy.ui.utils.dp8
import com.cheeke.surfy.ui.utils.dp92
import com.cheeke.surfy.ui.utils.sp10
import kotlinx.coroutines.launch

@Composable
fun MovieScreen(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: MovieVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("DetailScreen", "detail screen start!")
    TrackScreenViewEvent(screenName = "DetailScreen")

    val movieState by viewModel.movie.collectAsStateWithLifecycle()
    val similarMovies = viewModel.similarMovies.collectAsLazyPagingItems()
    val isCheatActive by viewModel.isCheatActive.collectAsStateWithLifecycle()

    MovieScreen(
        movieState = movieState,
        similarMovies = similarMovies,
        goToMovie = goToMovie,
        goToPeople = goToPeople,
        goToSeries = goToSeries,
        goToBack = goToBack,
        isCheatActive = isCheatActive,
        onShowSnackbar = onShowSnackbar,
        insertFavoriteMovie = viewModel::insertMovie,
        deleteFavoriteMovie = viewModel::deleteMovie,
        restart = viewModel::restart
    )
}

@Composable
fun MovieScreen(
    movieState: MovieState,
    similarMovies: LazyPagingItems<SimilarMedia>,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    goToBack: () -> Unit,
    isCheatActive: Boolean,
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
                        movie = movieState.movie,
                        isAutoPlayTrailer = movieState.isAutoPlayTrailer,
                        similarMovies = similarMovies,
                        goToMovie = goToMovie,
                        goToPeople = goToPeople,
                        goToSeries = goToSeries,
                        goToBack = goToBack,
                        isCheatActive = isCheatActive,
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

                val message = movieState.throwable.stringRes?.let { stringResource(id = it) } ?: stringResource(id = com.cheeke.surfy.core.network.R.string.something_wrong)

                ConfirmDialog(
                    title = stringResource(id = com.cheeke.surfy.core.network.R.string.network_failed),
                    message = message,
                    confirmPair = stringResource(id = com.cheeke.surfy.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(id = com.cheeke.surfy.core.ui.R.string.back_message) to goToBack
                )
            }
        }
    }
}

@Composable
fun MovieDetailComponent(
    movie: Movie,
    isAutoPlayTrailer: Boolean,
    similarMovies: LazyPagingItems<SimilarMedia>,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    goToBack: () -> Unit,
    isCheatActive: Boolean,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    insertFavoriteMovie: (Movie) -> Unit,
    deleteFavoriteMovie: (Movie) -> Unit,
    selectedImage: Image?,
    selectedIndex: Int?,
    overlayVisible: Boolean,
    onSelect: (ImageType, Image, Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope
) {
    val favoriteMessage = if (movie.isFavorite) stringResource(id = R.string.add_favorite_movie) else stringResource(id = R.string.remove_favorite_movie)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val analyticsHelper = LocalAnalyticsHelper.current

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(state = scrollState)
    ) {
        TitleComponent(
            isFavorite = movie.isFavorite,
            goToBack = goToBack,
            onFavorite = {
                if (movie.isFavorite) {
                    deleteFavoriteMovie(movie)
                    analyticsHelper.logFavorite(isFavorite = false, contentType = "movie", media = movie)
                } else {
                    insertFavoriteMovie(movie)
                    analyticsHelper.logFavorite(isFavorite = true, contentType = "movie", media = movie)
                }
                scope.launch {
                    onShowSnackbar(favoriteMessage, null)
                }
            }
        )
        movie.videos?.results?.filter { it.site == "YouTube" }?.takeIf { it.isNotEmpty() }?.let { vodList ->
            VideosComponent(
                scope = scope,
                vodList = vodList,
                autoPlayTrailer = isAutoPlayTrailer
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
        }

        MediaTitleComponent(media = movie)
        // 12) (옵션) Watch Providers / Where to watch
//            movieState.watchProviders?.let { watchProvider ->
//                item {
//                    WatchProvidersSection(providers = watchProvider)
//                }
//            }
        if (isCheatActive) {
            movie.alternativeTitles?.titles?.takeIf { it.isNotEmpty() }?.let { alternativeTitles ->
                Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
                AlternativeTitleComponent(alternativeTitles = alternativeTitles)
            }
        }
        movie.overview?.takeIf { it.trim().isNotEmpty() }?.let { overview ->
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
            OverviewComponent(overview = overview)
        }
        movie.credits?.let { credits ->
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
            CreditsComponent(
                credits = credits,
                goToPeople = goToPeople
            )
        }
        movie.productionCompanies?.let { productionCompanies ->
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
            ProductionComponent(companies = productionCompanies)
        }
        movie.series?.let { series ->
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
            SeriesComponent(
                collection = series,
                goToMovie = goToMovie,
                goToSeries = goToSeries
            )
        }
        movie.images?.let { images ->
            val posters = images.posters ?: emptyList()
            val backdrops = images.backdrops ?: emptyList()

            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
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
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
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

@Composable
fun AlternativeTitleComponent(alternativeTitles: List<AlternativeTitle>) {
    var expanded by remember { mutableStateOf(value = false) }
    val titles = alternativeTitles.fold(initial = "") { acc, title -> if (acc.isEmpty()) "${title.title}" else "$acc\n${title.title}" }

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
    goToSeries: (Int) -> Unit
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
                .padding(all = dp14)
                .bounceClick { goToSeries(collection?.id ?: -1) },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                DynamicAsyncImageLoader(
                    source = collection?.posterPath ?: "",
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = dp62, height = dp92)
                        .clip(shape = RoundedCornerShape(size = dp12))
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