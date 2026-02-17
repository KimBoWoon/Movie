package com.bowoon.detail.movie

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.domain.MovieWithFavorite
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.AlternativeTitles
import com.bowoon.model.Credits
import com.bowoon.model.Image
import com.bowoon.model.Movie
import com.bowoon.model.ProductionCompany
import com.bowoon.model.Series
import com.bowoon.movie.feature.detail.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.components.VideosComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp12
import com.bowoon.ui.utils.dp120
import com.bowoon.ui.utils.dp14
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp180
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp260
import com.bowoon.ui.utils.dp4
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp62
import com.bowoon.ui.utils.dp64
import com.bowoon.ui.utils.dp72
import com.bowoon.ui.utils.dp8
import com.bowoon.ui.utils.dp92
import com.bowoon.ui.utils.roundedCornerClickable
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp12
import com.bowoon.ui.utils.sp20
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
                val onSelect: (Image, Int) -> Unit = { image, index ->
                    selectedImage = image
                    selectedIndex = index
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
                        onSelect = onSelect,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )

                    with(receiver = this) {
                        FullscreenImageOverlay(
                            selectedImage = selectedImage,
                            selectedIndex = selectedIndex,
                            onDismiss = { selectedImage = null }
                        )
                    }
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
    onSelect: (Image, Int) -> Unit,
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
            modifier = Modifier.verticalScroll(state = scrollState)
        ) {
            VideosComponent(
                scope = scope,
                vodList = movieState.movie.videos?.results?.mapNotNull { it.key } ?: emptyList(),
                autoPlayTrailer = movieState.autoPlayTrailer
            )

            TitleSection(movie = movieState.movie)
            // 12) (옵션) Watch Providers / Where to watch
//            movieState.watchProviders?.let { watchProvider ->
//                item {
//                    WatchProvidersSection(providers = watchProvider)
//                }
//            }
            movieState.movie.alternativeTitles?.let { alternativeTitles ->
                if (!alternativeTitles.titles.isNullOrEmpty()) {
                    AlternativeTitleSection(alternativeTitles = alternativeTitles)
                }
            }
            movieState.movie.overview.takeIf { !it?.trim().isNullOrEmpty() }?.let { overview ->
                OverviewSection(overview = overview)
            }
            movieState.movie.credits?.let { credits ->
                CreditsSection(
                    credits = credits,
                    goToPeople = goToPeople
                )
            }
            movieState.movie.productionCompanies?.let { productionCompanies ->
                ProductionSection(companies = productionCompanies)
            }
            movieState.movie.series?.let { series ->
                SeriesSection(
                    collection = series,
                    goToMovie = goToMovie
                )
            }
            movieState.movie.images?.let { images ->
                val posters = images.posters ?: emptyList()
                val backdrops = images.backdrops ?: emptyList()

                ImagesSection(
                    backdrops = backdrops,
                    posters = posters,
                    sharedTransitionScope = sharedTransitionScope,
                    selectedImage = selectedImage,
                    onSelect = onSelect
                )
            }
            if (similarMovies.itemCount > 0) {
                SimilarSection(
                    similar = similarMovies,
                    goToMovie = goToMovie
                )
            }
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
fun TitleSection(
    movie: Movie
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dp16, vertical = dp10)
    ) {
        movie.tagline.takeIf { !it?.trim().isNullOrEmpty() }?.let { tagLine ->
            Text(
                modifier = Modifier
                    .semantics {
                        contentDescription = "movieTagline"
                    }
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = tagLine,
                fontSize = sp12,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
        }
        movie.title.takeIf { !it?.trim().isNullOrEmpty() }?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        movie.originalTitle.takeIf { !it?.trim().isNullOrEmpty() }?.let { originalTitle ->
            Text(
                text = originalTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(height = dp5))

        val meta = buildString {
            movie.releaseDate?.let { append(it) }
            movie.certification?.let { if (isNotEmpty()) append(" · "); append(it) }
            movie.runtime?.let { if (isNotEmpty()) append(" · "); append("${it}분") }
            if (!movie.genres.isNullOrEmpty()) {
                if (isNotEmpty()) {
                    append(" · ")
                }
                movie.genres?.forEachIndexed { index, genre ->
                    if (index == movie.genres?.lastIndex) {
                        append("${genre.name}")
                    } else {
                        append("${genre.name}, ")
                    }
                }
            }
            movie.voteAverage?.let { if (isNotEmpty()) append(" · "); append("★ ${"%.1f".format(it)}") }
        }

        Text(
            text = meta,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AlternativeTitleSection(alternativeTitles: AlternativeTitles?) {
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
fun OverviewSection(overview: String) {
    var expanded by remember { mutableStateOf(value = false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionHeader(title = "Overview")

        Spacer(modifier = Modifier.height(height = dp10))

        Text(
            modifier = Modifier.padding(horizontal = dp16),
            text = overview,
            maxLines = if (expanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis,
            lineHeight = sp20,
            style = MaterialTheme.typography.bodyMedium
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
fun ImagesSection(
    backdrops: List<Image>,
    posters: List<Image>,
    sharedTransitionScope: SharedTransitionScope,
    selectedImage: Image?,
    onSelect: (Image, Int) -> Unit
) {
    if (backdrops.isEmpty() && posters.isEmpty()) return

    Column(
        modifier = Modifier.padding(vertical = dp14)
    ) {
        SectionHeader(title = "Images"/*, actionText = "See all", onActionClick = onSeeAll*/)

        if (backdrops.isNotEmpty()) {
            Spacer(modifier = Modifier.height(height = dp12))
            SubSectionTitle(text = "Backdrops")
            Spacer(modifier = Modifier.height(height = dp10))
            ImageRow(images = backdrops, width = dp260, sharedTransitionScope = sharedTransitionScope, selectedImage = selectedImage, onSelect = onSelect)
        }

        if (posters.isNotEmpty()) {
            Spacer(modifier = Modifier.height(height = dp16))
            SubSectionTitle(text = "Posters")
            Spacer(modifier = Modifier.height(height = dp10))
            ImageRow(images = posters, width = dp120, sharedTransitionScope = sharedTransitionScope, selectedImage = selectedImage, onSelect = onSelect)
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dp16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (!actionText.isNullOrBlank() && onActionClick != null) {
            Text(
                text = actionText,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

@Composable
private fun SubSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = dp16),
        style = MaterialTheme.typography.titleSmall,
        color = Color.Gray
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageRow(
    images: List<Image>,
    width: Dp,
    sharedTransitionScope: SharedTransitionScope,
    selectedImage: Image?,
    onSelect: (Image, Int) -> Unit
) {
    val rowState = rememberLazyListState()
    var snapIndex by remember { mutableIntStateOf(value = 0) }
    var snapOffset by remember { mutableIntStateOf(value = 0) }

    // 오버레이 열림/닫힘 때 스크롤 복구
    LaunchedEffect(key1 = selectedImage) {
        // selected가 바뀌면(열기/닫기) 스냅샷 위치로 강제 복구
        rowState.scrollToItem(index = snapIndex, scrollOffset = snapOffset)
    }

    LazyRow(
        state = rowState,
        contentPadding = PaddingValues(horizontal = dp10),
        horizontalArrangement = Arrangement.spacedBy(space = dp10)
    ) {
        itemsIndexed(
            items = images,
            key = { index, image -> image.filePath ?: "image-$index" }
        ) { index, image ->
            val isSelected = selectedImage?.filePath == image.filePath
            val key = remember(key1 = image.filePath) { image.filePath ?: "image-$index" }

            with(receiver = sharedTransitionScope) {
                AnimatedVisibility(
                    visible = selectedImage == null || !isSelected,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None
                ) {
                    val base = Modifier
                        .width(width = width)
                        .aspectRatio(ratio = image.aspectRatio?.toFloat() ?: 1f)
                    val modifier = (if (selectedImage == null || isSelected) {
                        base.sharedElement(
                            sharedContentState = rememberSharedContentState(key = key),
                            animatedVisibilityScope = this@AnimatedVisibility
                        )
                    } else {
                        base
                    }).roundedCornerClickable(
                        onClick = {
                            snapIndex = rowState.firstVisibleItemIndex
                            snapOffset = rowState.firstVisibleItemScrollOffset
                            onSelect(image, index)
                        },
                        cornerRadius = dp10
                    )
                    DynamicAsyncImageLoader(
                        source = image.filePath.orEmpty(),
                        contentDescription = null,
                        modifier = modifier,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FullscreenImageOverlay(
    selectedImage: Image?,
    selectedIndex: Int?,
    onDismiss: () -> Unit,
) {
    // 백버튼으로 닫기
    BackHandler(enabled = selectedImage != null) { onDismiss() }

    if (selectedImage != null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable {}
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(alignment = Alignment.TopEnd)
                    .padding(all = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }

    AnimatedVisibility(
        visible = selectedImage != null,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        val img = selectedImage ?: return@AnimatedVisibility
        val key = remember(key1 = img.filePath) { img.filePath ?: "image-$selectedIndex" }

        DynamicAsyncImageLoader(
            source = img.filePath.orEmpty(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = key),
                    animatedVisibilityScope = this@AnimatedVisibility
                ),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun ProductionSection(companies: List<ProductionCompany>) {
    if (companies.isNotEmpty()) {
        Column {
            SectionHeader(title = "Production")
            Spacer(modifier = Modifier.height(height = dp12))

            LazyRow(contentPadding = PaddingValues(horizontal = dp16)) {
                items(items = companies) { company ->
                    Column(
                        modifier = Modifier
                            .width(width = dp120)
                            .padding(end = dp12)
                    ) {
                        DynamicAsyncImageLoader(
                            source = company.logoPath ?: "",
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = dp64)
                                .clip(shape = RoundedCornerShape(size = dp14))
                                .background(color = Color.DarkGray.copy(alpha = 0.35f))
                                .padding(all = dp10),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(height = dp8))
                        Text(
                            text = company.name ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreditsSection(
    credits: Credits,
    goToPeople: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dp10)
    ) {
        if (!credits.cast.isNullOrEmpty()) {
            Text(
                text = "출연진",
                modifier = Modifier.padding(horizontal = dp16, vertical = dp5),
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = dp16),
                horizontalArrangement = Arrangement.spacedBy(space = dp12)
            ) {
                items(
                    items = credits.cast ?: emptyList(),
                    key = { "${it.id}_${it.creditId}_${it.castId}" }
                ) { actor ->
                    Column(
                        modifier = Modifier
                            .width(width = dp72)
                            .bounceClick(onClick = { goToPeople(actor.id ?: -1) }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DynamicAsyncImageLoader(
                            source = actor.profilePath ?: "",
                            contentDescription = null,
                            modifier = Modifier
                                .size(size = dp72)
                                .clip(shape = CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = actor.character ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = actor.name ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        if (!credits.crew.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(height = dp5))

            Text(
                text = "제작진",
                modifier = Modifier.padding(horizontal = dp16, vertical = dp5),
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = dp16),
                horizontalArrangement = Arrangement.spacedBy(space = dp12)
            ) {
                items(
                    items = credits.crew ?: emptyList(),
                    key = { "${it.id}_${it.job}" }
                ) { crew ->
                    Column(
                        modifier = Modifier
                            .width(width = dp72)
                            .bounceClick(onClick = { goToPeople(crew.id ?: -1) }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DynamicAsyncImageLoader(
                            source = crew.profilePath ?: "",
                            contentDescription = null,
                            modifier = Modifier
                                .size(size = dp72)
                                .clip(shape = CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = crew.department ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = crew.name ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = crew.job ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimilarSection(
    similar: LazyPagingItems<Movie>,
    goToMovie: (Int) -> Unit
) {
    Column(
        modifier = Modifier.padding(bottom = dp20),
        verticalArrangement = Arrangement.spacedBy(space = dp12)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = dp16),
            text = "비슷한 영화",
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = dp16),
            horizontalArrangement = Arrangement.spacedBy(space = dp10)
        ) {
            items(
                count = similar.itemCount,
                key = { index -> similar.peek(index)?.id ?: -1 }
            ) { index ->
                Box(
                    modifier = Modifier.width(width = dp120)
                ) {
                    DynamicAsyncImageLoader(
                        source = similar[index]?.posterPath ?: "",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                            .roundedCornerClickable(
                                onClick = { goToMovie(similar[index]?.id ?: -1) },
                                cornerRadius = dp12
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
fun SeriesSection(
    collection: Series?,
    goToMovie: (Int) -> Unit,
//    onSeeAll: () -> Unit
) {
    Column(
        modifier = Modifier.padding(top = dp10)
    ) {
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
            Spacer(Modifier.height(height = dp14))
            SubSectionTitle(text = "Parts")
            Spacer(Modifier.height(height = dp10))

            LazyRow(contentPadding = PaddingValues(horizontal = dp16)) {
                items(items = collection.parts ?: emptyList()) { m ->
                    Column(
                        modifier = Modifier
                            .width(width = dp120)
                            .padding(end = dp12)
                            .clickable { goToMovie(m.id ?: -1) }
                    ) {
                        DynamicAsyncImageLoader(
                            source = m.posterPath ?: "",
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = dp180)
                                .clip(shape = RoundedCornerShape(size = dp14))
                                .background(Color.DarkGray),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(height = dp8))
                        Text(
                            text = m.title ?: "",
                            style = MaterialTheme.typography.bodySmall,
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