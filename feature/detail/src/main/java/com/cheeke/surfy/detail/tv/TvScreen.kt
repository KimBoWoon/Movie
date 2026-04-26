package com.cheeke.surfy.detail.tv

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.paging.compose.LazyPagingItems
import com.cheeke.surfy.analytics.LocalAnalyticsHelper
import com.cheeke.surfy.analytics.TrackScreenViewEvent
import com.cheeke.surfy.analytics.logFavorite
import com.cheeke.surfy.analytics.logSelectEpisode
import com.cheeke.surfy.analytics.logSelectSeason
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.detail.movie.AlternativeTitleComponent
import com.cheeke.surfy.domain.TvSeasonLoadState
import com.cheeke.surfy.feature.detail.R
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.model.Cast
import com.cheeke.surfy.model.Credits
import com.cheeke.surfy.model.Image
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeason
import com.cheeke.surfy.navigation.TvScreen
import com.cheeke.surfy.ui.components.CircularProgressComponent
import com.cheeke.surfy.ui.components.CreditsComponent
import com.cheeke.surfy.ui.components.ImageOverlay
import com.cheeke.surfy.ui.components.ImageType
import com.cheeke.surfy.ui.components.ImagesComponent
import com.cheeke.surfy.ui.components.MediaTitleComponent
import com.cheeke.surfy.ui.components.OverviewComponent
import com.cheeke.surfy.ui.components.ProductionComponent
import com.cheeke.surfy.ui.components.SimilarComponent
import com.cheeke.surfy.ui.components.TitleComponent
import com.cheeke.surfy.ui.components.VideosComponent
import com.cheeke.surfy.ui.dialog.ConfirmDialog
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp12
import com.cheeke.surfy.ui.utils.dp150
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp180
import com.cheeke.surfy.ui.utils.dp2
import com.cheeke.surfy.ui.utils.dp20
import com.cheeke.surfy.ui.utils.dp227
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.dp8
import com.cheeke.surfy.ui.utils.sp10
import com.cheeke.surfy.ui.utils.sp12
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@CircuitInject(screen = TvScreen::class, scope = ActivityRetainedComponent::class)
@Composable
fun TvScreen(
    modifier: Modifier,
    tvUiState: TvUiState
) {
    LocalFirebaseLogHelper.current.sendLog("DetailScreen", "detail screen start!")
    TrackScreenViewEvent(screenName = "TvScreen")

    val similarTvs = tvUiState.similarTvs
    val selectedEpisode = tvUiState.selectedEpisode

    TvScreen(
        tvUiState = tvUiState.tv,
        similarTvs = similarTvs,
        selectedEpisode = selectedEpisode,
        goToTv = { tvUiState.eventSink(TvEvent.GoToTv(id = it)) },
        goToPeople = { tvUiState.eventSink(TvEvent.GoToPeople(id = it)) },
        goToBack = { tvUiState.eventSink(TvEvent.GoToBack) },
        showEpisodeDetail = { tvUiState.eventSink(TvEvent.ShowEpisodeDetail(episode = it)) },
        hideEpisodeDetail = { tvUiState.eventSink(TvEvent.HideEpisodeDetail) },
//        onShowSnackbar = onShowSnackbar,
        insertFavoriteTv = { tvUiState.eventSink(TvEvent.InsertTv(tv = it)) },
        deleteFavoriteTv = { tvUiState.eventSink(TvEvent.DeleteTv(tv = it)) },
        restart = { tvUiState.eventSink(TvEvent.Restart) },
        onSelectSeason = { tvUiState.eventSink(TvEvent.SelectSeason(season = it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvScreen(
    tvUiState: TvState,
    similarTvs: LazyPagingItems<SimilarMedia>,
    selectedEpisode: TvEpisode?,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    showEpisodeDetail: (TvEpisode) -> Unit,
    hideEpisodeDetail: () -> Unit,
//    onShowSnackbar: suspend (String, String?) -> Boolean,
    insertFavoriteTv: (Tv) -> Unit,
    deleteFavoriteTv: (Tv) -> Unit,
    restart: () -> Unit,
    onSelectSeason: (TvSeason) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
    ) {
        when (tvUiState) {
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
                Log.d("${tvUiState.tvInfo.tv}")
                LocalFirebaseLogHelper.current.sendLog(name = "TvScreen", message = "$tvUiState")

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
                    TvDetailComponent(
                        tv = tvUiState.tvInfo,
                        similarTvs = similarTvs,
                        goToTv = goToTv,
                        goToPeople = goToPeople,
                        goToBack = goToBack,
                        showEpisodeDetail = showEpisodeDetail,
//                        onShowSnackbar = onShowSnackbar,
                        insertFavoriteTv = insertFavoriteTv,
                        deleteFavoriteTv = deleteFavoriteTv,
                        selectedImage = selectedImage,
                        selectedIndex = selectedIndex,
                        onSelect = onSelect,
                        overlayVisible = overlayVisible,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        onSelectSeason = onSelectSeason
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

                if (selectedEpisode != null) {
                    EpisodeDetailBottomSheetDialog(
                        episode = selectedEpisode,
                        state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        scope = rememberCoroutineScope(),
                        goToPeople = goToPeople,
                        onDismiss = { hideEpisodeDetail() }
                    )
                }
            }
            is TvState.Error -> {
                Log.e(tvUiState.message)
                LocalFirebaseLogHelper.current.sendLog(name = "TvScreen", message = tvUiState.message)

                ConfirmDialog(
                    title = stringResource(id = com.cheeke.surfy.core.network.R.string.network_failed),
                    message = tvUiState.message,
                    confirmPair = stringResource(id = com.cheeke.surfy.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(id = com.cheeke.surfy.core.ui.R.string.back_message) to goToBack
                )
            }
        }
    }
}

@Composable
fun TvDetailComponent(
    tv: TvInfo,
    similarTvs: LazyPagingItems<SimilarMedia>,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    showEpisodeDetail: (TvEpisode) -> Unit,
//    onShowSnackbar: suspend (String, String?) -> Boolean,
    insertFavoriteTv: (Tv) -> Unit,
    deleteFavoriteTv: (Tv) -> Unit,
    selectedImage: Image?,
    selectedIndex: Int?,
    overlayVisible: Boolean,
    onSelect: (ImageType, Image, Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    onSelectSeason: (TvSeason) -> Unit
) {
    val favoriteMessage = if (tv.isFavorite) stringResource(id = R.string.add_favorite_movie) else stringResource(id = R.string.remove_favorite_movie)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val analyticsHelper = LocalAnalyticsHelper.current

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(state = scrollState)
    ) {
        TitleComponent(
            isFavorite = tv.isFavorite,
            goToBack = goToBack,
            onFavorite = {
                if (tv.isFavorite) {
                    deleteFavoriteTv(tv.tv)
                    analyticsHelper.logFavorite(isFavorite = false, contentType = "tv", media = tv.tv)
                } else {
                    insertFavoriteTv(tv.tv)
                    analyticsHelper.logFavorite(isFavorite = true, contentType = "tv", media = tv.tv)
                }
//                scope.launch {
//                    onShowSnackbar(favoriteMessage, null)
//                }
            }
        )
        tv.tv.videos?.results?.filter { it.site == "YouTube" }?.takeIf { it.isNotEmpty() }?.let { vods ->
            VideosComponent(scope = scope, vodList = vods, autoPlayTrailer = tv.autoPlayTrailer)
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
        }
        MediaTitleComponent(media = tv.tv)
        tv.tv.alternativeTitles?.titles?.takeIf { it.isNotEmpty() }?.let { alternativeTitles ->
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
            AlternativeTitleComponent(alternativeTitles = alternativeTitles)
        }
        tv.tv.overview?.takeIf { it.trim().isNotEmpty() }?.let { overview ->
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
            OverviewComponent(overview = overview)
        }
        tv.tv.credits?.let { credits ->
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
            CreditsComponent(credits = credits, goToPeople = goToPeople)
        }
        tv.seasons.takeIf { it.isNotEmpty() }?.let { seasons ->
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
            SeasonComponent(
                tv = tv.tv,
                seasons = seasons,
                episodeState = tv.episodeState,
                episodesBySeason = tv.episodesBySeason,
                initialSeasonId = seasons.firstOrNull()?.id,
                onEpisodeClick = { episode ->
                    showEpisodeDetail(episode)
                    analyticsHelper.logSelectEpisode(
                        tvId = tv.tv.id.toString(),
                        tvTitle = tv.tv.title.toString(),
                        seasonName = seasons.find { it.seasonNumber == episode.seasonNumber }?.name.toString(),
                        seasonNumber = episode.seasonNumber.toString(),
                        episodeName = episode.name.toString(),
                        episodeNumber = episode.episodeNumber.toString()
                    )
                },
                onSelectSeason = onSelectSeason
            )
        }
        tv.tv.productionCompanies?.let { productionCompanies ->
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
            ProductionComponent(companies = productionCompanies)
        }
        tv.tv.images?.let {
            val backdrops = it.backdrops ?: emptyList()
            val posters = it.posters ?: emptyList()

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
        if (similarTvs.itemCount > 0) {
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp10))
            SimilarComponent(similar = similarTvs, goToDestination = goToTv)
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(height = dp20))
    }
}

@Composable
fun SeasonComponent(
    tv: Tv,
    seasons: List<TvSeason>,
    episodesBySeason: Map<String, List<TvEpisode>>,
    initialSeasonId: Int? = seasons.firstOrNull()?.id,
    onEpisodeClick: (TvEpisode) -> Unit = {},
    onSelectSeason: (TvSeason) -> Unit,
    episodeState: TvSeasonLoadState
) {
    var expanded by remember { mutableStateOf(value = false) }
    var selectedSeasonId by rememberSaveable { mutableStateOf(value = initialSeasonId) }
    val selectedSeason = remember(key1 = selectedSeasonId, key2 = seasons) {
        seasons.firstOrNull { it.id == selectedSeasonId } ?: seasons.firstOrNull()
    }
    val episodes = remember(key1 = selectedSeason?.id, key2 = episodesBySeason) {
        selectedSeason?.name?.let { episodesBySeason[it].orEmpty() }.orEmpty()
    }
    val episodeListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val analyticsHelper = LocalAnalyticsHelper.current

    Column(
        modifier = Modifier.fillMaxWidth().height(height = dp227)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(start = dp16),
                text = "Episodes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                minLines = 1
            )

            SeasonSpinner(
                modifier = Modifier.padding(start = dp5, end = dp16),
                seasons = seasons,
                selected = selectedSeason,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onSelect = { season ->
                    onSelectSeason(season)
                    selectedSeasonId = season.id
                    expanded = false
                    scope.launch {
                        episodeListState.scrollToItem(index = 0)
                    }
                    analyticsHelper.logSelectSeason(
                        tvId = tv.id.toString(),
                        tvTitle = tv.title ?: "",
                        seasonName = season.name ?: "",
                        seasonNumber = season.seasonNumber.toString()
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(height = dp12))

        EpisodeContents(
            modifier = Modifier.fillMaxWidth().weight(weight = 1f),
            episodeState = episodeState,
            episodes = episodes,
            episodeListState = episodeListState,
            onEpisodeClick = onEpisodeClick
        )
    }
}

@Composable
fun EpisodeContents(
    modifier: Modifier,
    episodeState: TvSeasonLoadState,
    episodes: List<TvEpisode>,
    episodeListState: LazyListState,
    onEpisodeClick: (TvEpisode) -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (episodeState) {
            is TvSeasonLoadState.Loading -> CircularProgressComponent()
            is TvSeasonLoadState.Idle -> {
                if (episodes.isEmpty()) {
                    Text(
                        text = "등록된 에피소드가 없습니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    EpisodeList(
                        episodes = episodes,
                        episodeListState = episodeListState,
                        onEpisodeClick = onEpisodeClick
                    )
                }
            }
            is TvSeasonLoadState.Error -> {
                Text(
                    text = episodeState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeasonSpinner(
    modifier: Modifier,
    seasons: List<TvSeason>,
    selected: TvSeason?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (TvSeason) -> Unit
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = selected?.name.orEmpty(),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable)
                .widthIn(min = dp180, max = dp180),
            label = { Text(text = "Season") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            seasons.forEach { season ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = season.name ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                        )
                    },
                    onClick = { onSelect(season) }
                )
            }
        }
    }
}

@Composable
private fun EpisodeList(
    episodes: List<TvEpisode>,
    episodeListState: LazyListState,
    onEpisodeClick: (TvEpisode) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(space = dp10),
        contentPadding = PaddingValues(start = dp16, end = dp16, bottom = dp8),
        state = episodeListState
    ) {
        items(
            items = episodes,
            key = { it.id ?: -1 }
        ) { e ->
            EpisodeRow(
                episode = e,
                onClick = { onEpisodeClick(e) }
            )
        }
    }
}

@Composable
private fun EpisodeRow(
    episode: TvEpisode,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(width = dp150)
            .clickable(onClick = onClick),
    ) {
        DynamicAsyncImageLoader(
            source = episode.stillPath ?: "",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 16f / 9f)
                .clip(shape = RoundedCornerShape(size = dp12)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "E${episode.episodeNumber}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(height = dp2))
        Text(
            text = episode.name ?: "",
            style = MaterialTheme.typography.bodyMedium,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailBottomSheetDialog(
    episode: TvEpisode,
    state: SheetState,
    scope: CoroutineScope,
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
        val columnScrollState = rememberScrollState()

        Column(
            modifier = Modifier.verticalScroll(state = columnScrollState)
        ) {
            DynamicAsyncImageLoader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dp10, end = dp10, bottom = dp10)
                    .aspectRatio(ratio = 16f / 9f)
                    .clip(shape = RoundedCornerShape(size = dp10)),
                source = episode.stillPath ?: "",
                contentDescription = episode.stillPath
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dp10),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .semantics { contentDescription = episode.name ?: "" }
                        .weight(weight = 1f),
                    text = episode.name ?: "",
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                episode.runtime?.let {
                    Text(
                        modifier = Modifier.padding(start = dp5),
                        text = "${it}분",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1
                    )
                }
            }
            Text(
                modifier = Modifier.padding(horizontal = dp10),
                text = episode.airDate ?: "",
                fontSize = sp10,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                modifier = Modifier.padding(horizontal = dp10),
                text = episode.overview ?: "",
                fontSize = sp12,
                style = MaterialTheme.typography.labelLarge,
            )
            CreditsComponent(
                credits = Credits(
                    cast = episode.guestStars?.map {
                        Cast(
                            adult = it.adult,
                            castId = it.id,
                            character = it.character,
                            creditId = it.creditId,
                            gender = it.gender,
                            id = it.id,
                            knownForDepartment = it.knownForDepartment,
                            name = it.name,
                            order = it.order,
                            originalName = it.originalName,
                            popularity = it.popularity,
                            profilePath = it.profilePath
                        )
                    },
                    crew = episode.crew ?: emptyList()
                ),
                goToPeople = goToPeople
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(height = dp20))
    }
}