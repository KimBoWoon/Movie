package com.bowoon.detail.tv

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.detail.movie.AlternativeTitleComponent
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Cast
import com.bowoon.model.Credits
import com.bowoon.model.Image
import com.bowoon.model.Tv
import com.bowoon.model.TvEpisode
import com.bowoon.model.TvSeason
import com.bowoon.movie.feature.detail.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.components.CreditsComponent
import com.bowoon.ui.components.ImageOverlay
import com.bowoon.ui.components.ImageType
import com.bowoon.ui.components.ImagesComponent
import com.bowoon.ui.components.MediaTitleComponent
import com.bowoon.ui.components.OverviewComponent
import com.bowoon.ui.components.ProductionComponent
import com.bowoon.ui.components.SimilarComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.components.VideosComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp12
import com.bowoon.ui.utils.dp15
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp180
import com.bowoon.ui.utils.dp2
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp227
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp8
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp12
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

    val similarTvs = viewModel.similarTvs.collectAsLazyPagingItems()
    val selectedEpisode by viewModel.selectedEpisode.collectAsStateWithLifecycle()
    val tvUiState by viewModel.uiState.collectAsStateWithLifecycle()

    TvScreen(
        tvUiState = tvUiState,
        similarTvs = similarTvs,
        selectedEpisode = selectedEpisode,
        goToTv = goToTv,
        goToPeople = goToPeople,
        goToBack = goToBack,
        showEpisodeDetail = viewModel::showEpisodeDetail,
        hideEpisodeDetail = viewModel::hideEpisodeDetail,
        onShowSnackbar = onShowSnackbar,
        insertFavoriteTv = viewModel::insertTv,
        deleteFavoriteTv = viewModel::deleteTv,
        restart = viewModel::restart,
        onSelectSeason = viewModel::onSelectSeason
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvScreen(
    tvUiState: TvState,
    similarTvs: LazyPagingItems<Tv>,
    selectedEpisode: TvEpisode?,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    showEpisodeDetail: (TvEpisode) -> Unit,
    hideEpisodeDetail: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    insertFavoriteTv: (Tv) -> Unit,
    deleteFavoriteTv: (Tv) -> Unit,
    restart: () -> Unit,
    onSelectSeason: (TvSeason) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
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
                Log.d("${tvUiState.tvUiState.tv}")
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
                        tv = tvUiState.tvUiState,
                        similarTvs = similarTvs,
                        goToTv = goToTv,
                        goToPeople = goToPeople,
                        goToBack = goToBack,
                        showEpisodeDetail = showEpisodeDetail,
                        onShowSnackbar = onShowSnackbar,
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
                    title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                    message = tvUiState.message,
                    confirmPair = stringResource(id = com.bowoon.movie.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(id = com.bowoon.movie.core.ui.R.string.back_message) to goToBack
                )
            }
        }
    }
}

@Composable
fun TvDetailComponent(
    tv: TvUiState,
    similarTvs: LazyPagingItems<Tv>,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    showEpisodeDetail: (TvEpisode) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
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

    Column (
        modifier = Modifier.fillMaxSize(),
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
        Column(
            modifier = Modifier.verticalScroll(state = scrollState),
            verticalArrangement = Arrangement.spacedBy(space = dp15)
        ) {
            tv.tv.videos?.results?.mapNotNull { it.key }?.let { vods ->
                VideosComponent(scope = scope, vodList = vods, autoPlayTrailer = tv.autoPlayTrailer)
            }
            MediaTitleComponent(media = tv.tv)
            tv.tv.alternativeTitles?.let { alternativeTitles ->
                if (!alternativeTitles.titles.isNullOrEmpty()) {
                    AlternativeTitleComponent(alternativeTitles = alternativeTitles)
                }
            }
            tv.tv.overview?.takeIf { it.trim().isNotEmpty() }?.let { overview ->
                OverviewComponent(overview = overview)
            }
            tv.tv.credits?.let { credits ->
                CreditsComponent(credits = credits, goToPeople = goToPeople)
            }
            tv.seasons.takeIf { it.isNotEmpty() }?.let { seasons ->
                SeasonComponent(
                    seasons = seasons,
                    episodeState = tv.episodeState,
                    episodesBySeason = tv.episodesBySeason,
                    initialSeasonId = seasons.firstOrNull()?.seasonNumber,
                    onEpisodeClick = { episode -> showEpisodeDetail(episode) },
                    onSelectSeason = onSelectSeason
                )
            }
            tv.tv.productionCompanies?.let { productionCompanies ->
                ProductionComponent(companies = productionCompanies)
            }
            tv.tv.images?.let {
                val backdrops = it.backdrops ?: emptyList()
                val posters = it.posters ?: emptyList()

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
                SimilarComponent(similar = similarTvs, goToDestination = goToTv)
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(height = dp20))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonComponent(
    seasons: List<TvSeason>,
    episodesBySeason: Map<String, List<TvEpisode>>,
    initialSeasonId: Int? = seasons.firstOrNull()?.seasonNumber,
    onEpisodeClick: (TvEpisode) -> Unit = {},
    onSelectSeason: (TvSeason) -> Unit,
    episodeState: EpisodesLoadState
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
                }
            )
        }

        Spacer(Modifier.height(height = dp12))

        when (episodeState) {
            is EpisodesLoadState.Loading -> CircularProgressComponent(modifier = Modifier.fillMaxWidth().align(alignment = Alignment.CenterHorizontally))
            is EpisodesLoadState.Idle -> {
                EpisodeList(
                    episodes = episodes,
                    episodeListState = episodeListState,
                    onEpisodeClick = onEpisodeClick
                )
            }
            is EpisodesLoadState.Error -> {
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