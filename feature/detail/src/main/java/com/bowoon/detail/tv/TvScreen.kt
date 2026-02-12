package com.bowoon.detail.tv

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.domain.TvWithFavorite
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.ReviewDataModel
import com.bowoon.model.Tv
import com.bowoon.model.TvEpisode
import com.bowoon.model.TvSeasons
import com.bowoon.movie.feature.detail.R
import com.bowoon.ui.components.ActorAndCrewComponent
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.components.ImageComponent
import com.bowoon.ui.components.ReviewComponent
import com.bowoon.ui.components.SimilarMediaComponent
import com.bowoon.ui.components.TabComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.components.VideosComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.roundedCornerClickable
import com.bowoon.ui.utils.dp0
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp250
import com.bowoon.ui.utils.dp300
import com.bowoon.ui.utils.dp32
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp8
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp12
import com.bowoon.ui.utils.sp15
import com.bowoon.ui.utils.sp20
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TvScreen(
    goToBack: () -> Unit,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: TvVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("DetailScreen", "detail screen start!")

    val tvState by viewModel.tv.collectAsStateWithLifecycle()
    val tabIndex by viewModel.tabIndex.collectAsStateWithLifecycle()
    val similarTvs = viewModel.similarTvs.collectAsLazyPagingItems()
    val tvReviews = viewModel.tvReviews.collectAsLazyPagingItems()
    val selectedEpisode by viewModel.selectedEpisode.collectAsStateWithLifecycle()

    TvScreen(
        tvState = tvState,
        similarTvs = similarTvs,
        tvReviews = tvReviews,
        selectedEpisode = selectedEpisode,
        tabIndex = tabIndex,
        goToTv = goToTv,
        goToPeople = goToPeople,
        goToBack = goToBack,
        showEpisodeDetail = viewModel::showEpisodeDetail,
        hideEpisodeDetail = viewModel::hideEpisodeDetail,
        onShowSnackbar = onShowSnackbar,
        updateTabIndex = viewModel::updateTabIndex,
        insertFavoriteTv = viewModel::insertTv,
        deleteFavoriteTv = viewModel::deleteTv,
        restart = viewModel::restart
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvScreen(
    tvState: TvState,
    similarTvs: LazyPagingItems<Tv>,
    tvReviews: LazyPagingItems<ReviewDataModel>,
    selectedEpisode: TvEpisode?,
    tabIndex: Int,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    showEpisodeDetail: (TvEpisode) -> Unit,
    hideEpisodeDetail: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    updateTabIndex: (Int) -> Unit,
    insertFavoriteTv: (Tv) -> Unit,
    deleteFavoriteTv: (Tv) -> Unit,
    restart: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (tvState) {
            is TvState.Loading -> {
                Log.d("loading...")
                LocalFirebaseLogHelper.current.sendLog(name = "TvScreen", message = "loading...")

                CircularProgressComponent(
                    modifier = Modifier
                        .semantics { contentDescription = "tvDetailLoading" }
                        .align(Alignment.Center)
                )
            }
            is TvState.Success -> {
                Log.d("${tvState.tv}")
                LocalFirebaseLogHelper.current.sendLog(name = "TvScreen", message = "$tvState")

                TvDetailComponent(
                    tv = tvState.tv,
                    similarTvs = similarTvs,
                    tvReviews = tvReviews,
                    tabIndex = tabIndex,
                    goToTv = goToTv,
                    goToPeople = goToPeople,
                    goToBack = goToBack,
                    showEpisodeDetail = showEpisodeDetail,
                    onShowSnackbar = onShowSnackbar,
                    updateTabIndex = updateTabIndex,
                    insertFavoriteTv = insertFavoriteTv,
                    deleteFavoriteTv = deleteFavoriteTv
                )

                if (selectedEpisode != null) {
                    val columnScrollState = rememberLazyListState()
                    val crewScrollState = rememberLazyListState()
                    val guestStarScrollState = rememberLazyListState()

                    EpisodeDetailBottomSheetDialog(
                        episode = selectedEpisode,
                        state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        scope = rememberCoroutineScope(),
                        columnScrollState = columnScrollState,
                        crewScrollState = crewScrollState,
                        guestStarScrollState = guestStarScrollState,
                        goToPeople = goToPeople,
                        onDismiss = { hideEpisodeDetail() }
                    )
                }
            }
            is TvState.Error -> {
                Log.e("${tvState.throwable.message}")
                LocalFirebaseLogHelper.current.sendLog(name = "TvScreen", message = "${tvState.throwable.message}")

                ConfirmDialog(
                    title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                    message = "${tvState.throwable.message}",
                    confirmPair = stringResource(id = com.bowoon.movie.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(id = com.bowoon.movie.core.ui.R.string.back_message) to goToBack
                )
            }
        }
    }
}

@Composable
fun TvDetailComponent(
    tv: TvWithFavorite,
    similarTvs: LazyPagingItems<Tv>,
    tvReviews: LazyPagingItems<ReviewDataModel>,
    tabIndex: Int,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    showEpisodeDetail: (TvEpisode) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    updateTabIndex: (Int) -> Unit,
    insertFavoriteTv: (Tv) -> Unit,
    deleteFavoriteTv: (Tv) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val tabList = TvTab.entries.map { stringResource(id = it.stringId) }.toMutableList()
    if (tv.tv.seasons.isNullOrEmpty()) {
        tabList.remove(element = stringResource(id = R.string.movie_series))
    }
    if (tvReviews.itemCount == 0) {
        tabList.remove(element = stringResource(id = R.string.movie_reviews))
    }
    val pagerState = rememberPagerState(
        initialPage = tabIndex,
        pageCount = { tabList.size }
    )
    val tabClickEvent: (Int, Int) -> Unit = { current, index ->
        scope.launch {
            pagerState.animateScrollToPage(page = index)
            updateTabIndex(index)
        }
    }
    val favoriteMessage = if (tv.isFavorite) stringResource(id = R.string.add_favorite_movie) else stringResource(id = R.string.remove_favorite_movie)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleComponent(
            title = tv.tv.title ?: "",
            isFavorite = tv.isFavorite,
            goToBack = goToBack,
            onFavoriteClick = {
                if (tv.isFavorite) {
                    deleteFavoriteTv(tv.tv)
                } else {
                    insertFavoriteTv(tv.tv)
                }
                scope.launch {
                    onShowSnackbar(favoriteMessage, null)
                }
            }
        )

        VideosComponent(
            vodList = tv.tv.videos?.results?.mapNotNull { it.key } ?: emptyList(),
            autoPlayTrailer = tv.autoPlayTrailer
        )

        TabComponent(
            tabs = tabList,
            pagerState = pagerState,
            tabClickEvent = tabClickEvent
        ) { tabs ->
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                state = pagerState,
                userScrollEnabled = false
            ) { index ->
                when (tabs[index]) {
                    stringResource(id = R.string.tv_detail) -> TvInfoComponent(tv = tv.tv)
                    stringResource(id = R.string.tv_season) -> TvSeasonComponent(
                        tvSeasons = tv.tv.seasonList,
                        showEpisodeDetail = showEpisodeDetail,
                    )
                    stringResource(id = R.string.tv_episode) -> TvEpisodeComponent(
                        tvSeasons = tv.tv.episode,
                        showEpisodeDetail = showEpisodeDetail,
                    )
                    stringResource(id = R.string.tv_reviews) -> ReviewComponent(
                        reviews = tvReviews,
                    )
                    stringResource(id = R.string.tv_actor_and_crew) -> ActorAndCrewComponent(
                        credits = tv.tv.credits,
                        goToPeople = goToPeople
                    )
                    stringResource(id = R.string.tv_images) -> {
                        val posters = tv.tv.images?.posters ?: emptyList()
                        val backdrops = tv.tv.images?.backdrops ?: emptyList()
                        ImageComponent(images = posters + backdrops)
                    }
                    stringResource(id = R.string.tv_similar_tv) -> SimilarMediaComponent(
                        similarMedia = similarTvs,
                        goToDestination = goToTv
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvSeasonComponent(
    tvSeasons: Map<String, TvSeasons>?,
    showEpisodeDetail: (TvEpisode) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .semantics { contentDescription = "tvSeasonList" }
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = dp10),
        verticalArrangement = Arrangement.spacedBy(space = dp10)
    ) {
        tvSeasons?.forEach { (key, value) ->
            stickyHeader {
                Row(
                    modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surface),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(start = dp16),
                        text = "$key (${value.episodes?.count()}부작)"
                    )
                    Text(
                        modifier = Modifier.padding(end = dp16),
                        text = value.airDate ?: ""
                    )
                }
            }
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = dp16),
                    horizontalArrangement = Arrangement.spacedBy(space = dp10)
                ) {
                    items(
                        items = value.episodes ?: emptyList(),
                        key = { it.id ?: -1 }
                    ) { episode ->
                        Column(
                            modifier = Modifier
                                .width(width = dp250)
                                .wrapContentHeight()
                                .roundedCornerClickable(onClick = { showEpisodeDetail(episode) })
                        ) {
                            EpisodeItem(episode = episode,)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvEpisodeComponent(
    tvSeasons: TvSeasons?,
    showEpisodeDetail: (TvEpisode) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .semantics { contentDescription = "tvEpisodeList" }
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = dp16, vertical = dp10),
        verticalArrangement = Arrangement.spacedBy(space = dp10)
    ) {
        items(
            items = tvSeasons?.episodes ?: emptyList(),
            key = { it.id ?: -1 }
        ) { episode ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .roundedCornerClickable(onClick = { showEpisodeDetail(episode) })
            ) {
                EpisodeItem(episode = episode,)
            }
        }
    }
}

@Composable
fun EpisodeItem(
    episode: TvEpisode
) {
    DynamicAsyncImageLoader(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 16f / 9f)
            .clip(shape = RoundedCornerShape(size = dp10)),
        source = episode.stillPath ?: "",
        contentDescription = episode.stillPath
    )
    Text(
        modifier = Modifier.semantics { contentDescription = episode.name ?: "" },
        text = episode.name ?: "",
        fontSize = sp15,
        fontWeight = FontWeight.Bold,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
        maxLines = 1
    )
    Text(
        modifier = Modifier.semantics { contentDescription = episode.airDate ?: "" },
        text = episode.airDate ?: "",
        fontSize = sp10,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
        maxLines = 1
    )
    Text(
        modifier = Modifier.semantics { contentDescription = "TvSeasonOverview" },
        text = episode.overview ?: "",
        overflow = TextOverflow.Ellipsis,
        fontSize = sp12,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
        maxLines = 2,
        minLines = 2
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailBottomSheetDialog(
    episode: TvEpisode,
    state: SheetState,
    scope: CoroutineScope,
    columnScrollState: LazyListState,
    crewScrollState: LazyListState,
    guestStarScrollState: LazyListState,
    goToPeople: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                onDismiss()
                state.hide()
            }
        },
        sheetState = state,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        sheetGesturesEnabled = false
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = columnScrollState
        ) {
            item {
                DynamicAsyncImageLoader(
                    modifier = Modifier.padding(horizontal = dp10).fillMaxWidth().aspectRatio(ratio = 16f / 9f).clip(shape = RoundedCornerShape(size = dp10)),
                    source = episode.stillPath ?: "",
                    contentDescription = episode.stillPath
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = dp16),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.semantics { contentDescription = episode.name ?: "" },
                        text = episode.name ?: "",
                        fontSize = sp15,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                        maxLines = 1
                    )
                    Text(
                        text = "${episode.runtime}분",
                        fontSize = sp15,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                }
                Text(
                    modifier = Modifier.padding(horizontal = dp16),
                    text = episode.airDate ?: "",
                    fontSize = sp10,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
                Text(
                    modifier = Modifier.padding(horizontal = dp16),
                    text = episode.overview ?: "",
                    fontSize = sp12,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
            item {
                if (!episode.guestStars.isNullOrEmpty()) {
                    Text(
                        modifier = Modifier.padding(horizontal = dp16),
                        text = "출연 배우",
                        fontSize = sp15,
                        fontWeight = FontWeight.Bold,
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        state = guestStarScrollState,
                        contentPadding = PaddingValues(horizontal = dp16),
                        horizontalArrangement = Arrangement.spacedBy(space = dp10)
                    ) {
                        items(
                            items = episode.guestStars ?: emptyList(),
                            key = { "GUEST_${it.id}_${it.name}" }
                        ) {
                            Column(
                                modifier = Modifier.roundedCornerClickable(onClick = { goToPeople(it.id ?: -1) })
                            ) {
                                DynamicAsyncImageLoader(
                                    modifier = Modifier.width(width = dp150).aspectRatio(ratio = PEOPLE_IMAGE_RATIO).clip(shape = RoundedCornerShape(size = dp10)),
                                    source = it.profilePath ?: "",
                                    contentDescription = it.name ?: ""
                                )
                                Text(
                                    text = it.name ?: "",
                                    fontSize = sp12,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                    maxLines = 1
                                )
                                Text(
                                    text = it.originalName ?: "",
                                    fontSize = sp12,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
            item {
                if (!episode.crew.isNullOrEmpty()) {
                    Text(
                        modifier = Modifier.padding(horizontal = dp16),
                        text = "스태프",
                        fontSize = sp15,
                        fontWeight = FontWeight.Bold,
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        state = crewScrollState,
                        contentPadding = PaddingValues(horizontal = dp16),
                        horizontalArrangement = Arrangement.spacedBy(space = dp10)
                    ) {
                        items(
                            items = episode.crew ?: emptyList(),
                            key = { "CREW_${it.id}_${it.name}_${it.job}" }
                        ) {
                            Column(
                                modifier = Modifier.roundedCornerClickable(onClick = { goToPeople(it.id ?: -1) })
                            ) {
                                DynamicAsyncImageLoader(
                                    modifier = Modifier.width(width = dp150).aspectRatio(ratio = PEOPLE_IMAGE_RATIO).clip(shape = RoundedCornerShape(size = dp10)),
                                    source = it.profilePath ?: "",
                                    contentDescription = it.name ?: ""
                                )
                                Text(
                                    text = it.name ?: "",
                                    fontSize = sp12,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                    maxLines = 1
                                )
                                Text(
                                    text = it.originalName ?: "",
                                    fontSize = sp12,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                    maxLines = 1
                                )
                                Text(
                                    text = it.job ?: "",
                                    fontSize = sp12,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TvInfoComponent(
    tv: Tv
) {
    val titles = tv.alternativeTitles?.titles?.fold(initial = "") { acc, title -> if (acc.isEmpty()) "${title.title}" else "$acc\n${title.title}" } ?: ""

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .padding(start = dp16, end = dp16, top = dp10)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!tv.firstAirDate.isNullOrEmpty() && !tv.lastAirDate.isNullOrEmpty()) {
                    Text(
                        text = "${tv.firstAirDate} ~ ${tv.lastAirDate}",
                        fontSize = sp10,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                } else if (!tv.firstAirDate.isNullOrEmpty()) {
                    Text(
                        text = "${tv.firstAirDate} ~ ${tv.status}",
                        fontSize = sp10,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                }
            }
        }
        item {
            tv.tagline?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    modifier = Modifier
                        .testTag(tag = "tvTagline")
                        .padding(
                            start = dp16,
                            end = dp16,
                            top = if (!tv.firstAirDate.isNullOrEmpty()) dp10 else dp20
                        )
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = it,
                    fontSize = sp15,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
            tv.title?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    modifier = Modifier
                        .testTag(tag = "movieTitle")
                        .padding(
                            start = dp16,
                            end = dp16,
                            top = if (!tv.tagline.isNullOrEmpty()) dp0 else dp20
                        )
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = it,
                    fontSize = sp20,
                    textAlign = TextAlign.Center
                )
            }
            tv.originalTitle?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    modifier = Modifier
                        .padding(top = dp5, bottom = dp5, start = dp16, end = dp16)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = it,
                    fontSize = sp10,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
            tv.genres?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    modifier = Modifier
                        .padding(horizontal = dp16)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = it.fold("") { acc, genre -> if (acc.isEmpty()) "${genre.name}" else "$acc, ${genre.name}" },
                    fontSize = sp10,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = dp16)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tv.voteAverage?.let {
                    Text(
                        text = stringResource(id = R.string.movie_vote_average, it),
                        fontSize = sp10,
                        textAlign = TextAlign.Center
                    )
                }
            }
            tv.overview?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    modifier = Modifier
                        .padding(horizontal = dp16)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = it,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        item {
            tv.productionCompanies.takeIf { !it.isNullOrEmpty() }?.let { production ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dp10, horizontal = dp16),
                    text = stringResource(id = R.string.movie_production_companies),
                    textAlign = TextAlign.Center,
                    fontSize = sp20,
                    fontWeight = FontWeight.Bold
                )
                HorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    state = rememberPagerState { production.size },
                    contentPadding = PaddingValues(horizontal = dp32),
                    key = { index -> production[index].id ?: -1 }
                ) { index ->
                    Column(
                        modifier = Modifier.padding(horizontal = dp8)
                    ) {
                        production[index].logoPath?.let {
                            DynamicAsyncImageLoader(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(dp300)
                                    .clip(shape = RoundedCornerShape(size = dp10)),
                                contentScale = ContentScale.Fit,
                                source = it,
                                contentDescription = it
                            )
                        }
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = production[index].name ?: "",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        item {
            if (titles.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dp16)
                        .wrapContentHeight(),
                    text = stringResource(id = R.string.movie_alternative_title),
                    fontSize = sp15
                )
                Text(
                    modifier = Modifier
                        .padding(top = dp5, start = dp16, end = dp16)
                        .animateContentSize()
                        .fillMaxWidth(),
                    text = titles,
                    fontSize = sp10,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
        }
    }
}