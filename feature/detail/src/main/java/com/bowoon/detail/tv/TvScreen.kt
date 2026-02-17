package com.bowoon.detail.tv

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.detail.movie.AlternativeTitleSection
import com.bowoon.detail.movie.CreditsSection
import com.bowoon.detail.movie.ImagesSection
import com.bowoon.detail.movie.OverviewSection
import com.bowoon.detail.movie.ProductionSection
import com.bowoon.domain.TvWithFavorite
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Tv
import com.bowoon.model.TvEpisode
import com.bowoon.model.TvSeasons
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
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp180
import com.bowoon.ui.utils.dp2
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp72
import com.bowoon.ui.utils.dp8
import com.bowoon.ui.utils.roundedCornerClickable
import com.bowoon.ui.utils.sp10
import com.bowoon.ui.utils.sp12
import com.bowoon.ui.utils.sp15
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
    val similarTvs = viewModel.similarTvs.collectAsLazyPagingItems()
    val selectedEpisode by viewModel.selectedEpisode.collectAsStateWithLifecycle()

    TvScreen(
        tvState = tvState,
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
        restart = viewModel::restart
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvScreen(
    tvState: TvState,
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
                    goToTv = goToTv,
                    goToPeople = goToPeople,
                    goToBack = goToBack,
                    showEpisodeDetail = showEpisodeDetail,
                    onShowSnackbar = onShowSnackbar,
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
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToBack: () -> Unit,
    showEpisodeDetail: (TvEpisode) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    insertFavoriteTv: (Tv) -> Unit,
    deleteFavoriteTv: (Tv) -> Unit,
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
            modifier = Modifier.verticalScroll(state = scrollState)
        ) {
            tv.tv.videos?.results?.mapNotNull { it.key }?.let { vods ->
                VideosComponent(scope = scope, vodList = vods, autoPlayTrailer = tv.autoPlayTrailer)
            }
            TvInfoSection(tv = tv.tv)
            tv.tv.alternativeTitles?.let { alternativeTitles ->
                if (!alternativeTitles.titles.isNullOrEmpty()) {
                    AlternativeTitleSection(alternativeTitles = alternativeTitles)
                }
            }
            tv.tv.overview?.let { overview ->
                OverviewSection(overview = overview)
            }
            tv.tv.credits?.let { credits ->
                CreditsSection(credits = credits, goToPeople = goToPeople)
            }
            tv.tv.seasonList?.let {
                SeasonEpisodesSection(
                    seasons = it.values.toList(),
                    episodesBySeason = buildMap {
                        it.forEach { (key, value) ->
                            put(key = key, value = value.episodes ?: emptyList())
                        }
                    },
                    initialSeasonId = 1,
                    onEpisodeClick = { episode -> showEpisodeDetail(episode) }
                )
            }
            tv.tv.productionCompanies?.let { productionCompanies ->
                ProductionSection(companies = productionCompanies)
            }
            tv.tv.images?.let {
                val backdrops = it.backdrops ?: emptyList()
                val posters = it.posters ?: emptyList()

                ImagesSection(backdrops = backdrops, posters = posters)
            }
            if (similarTvs.itemCount > 0) {
                SimilarSection(similar = similarTvs, goToTv = goToTv)
            }
        }
    }
}

@Composable
fun TvInfoSection(
    tv: Tv
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dp16, vertical = dp10)
    ) {
        tv.tagline.takeIf { !it?.trim().isNullOrEmpty() }?.let { tagLine ->
            Text(
                modifier = Modifier
                    .semantics {
                        contentDescription = "movieTagline"
                    }
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = tagLine,
                fontSize = sp12,
                textAlign = TextAlign.Center,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
        }

        tv.title.takeIf { !it?.trim().isNullOrEmpty() }?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                fontWeight = FontWeight.Bold
            )
        }

        tv.originalTitle.takeIf { !it?.trim().isNullOrEmpty() }?.let { originalTitle ->
            Text(
                text = originalTitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(height = dp5))

        val meta = buildString {
            tv.releaseDate?.let { append(it) }
//            tv.certification?.let { if (isNotEmpty()) append(" · "); append(it) }
//            tv.runtime?.let { if (isNotEmpty()) append(" · "); append("${it}분") }
            if (!tv.genres.isNullOrEmpty()) {
                if (isNotEmpty()) {
                    append(" · ")
                }
                tv.genres?.forEachIndexed { index, genre ->
                    if (index == tv.genres?.lastIndex) {
                        append("${genre.name}")
                    } else {
                        append("${genre.name}, ")
                    }
                }
            }
            tv.voteAverage?.let { if (isNotEmpty()) append(" · "); append("★ ${"%.1f".format(it)}") }
        }

        Text(
            text = meta,
            style = MaterialTheme.typography.bodySmall.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
//            color = Color.LightGray,
            color = Color.Gray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonEpisodesSection(
    seasons: List<TvSeasons>,
    episodesBySeason: Map<String, List<TvEpisode>>,
    initialSeasonId: Int? = seasons.firstOrNull()?.seasonNumber,
    onEpisodeClick: (TvEpisode) -> Unit = {}
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
        modifier = Modifier
            .fillMaxWidth()
//            .padding(horizontal = dp16, vertical = dp12)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(weight = 1f).padding(start = dp16),
                text = "Episodes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // ✅ 스피너(드롭다운)
            SeasonSpinner(
                seasons = seasons,
                selected = selectedSeason,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onSelect = { season ->
                    selectedSeasonId = season.id
                    expanded = false
                    scope.launch {
                        episodeListState.scrollToItem(index = 0)
                    }
                }
            )
        }

        Spacer(Modifier.height(height = dp12))

        if (episodes.isEmpty()) {
            Text(
                text = "에피소드 정보가 없어요.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        } else {
            EpisodeList(
                episodes = episodes,
                episodeListState = episodeListState,
                onEpisodeClick = onEpisodeClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeasonSpinner(
    seasons: List<TvSeasons>,
    selected: TvSeasons?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (TvSeasons) -> Unit
) {
    ExposedDropdownMenuBox(
        modifier = Modifier.padding(start = dp5, end = dp16),
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        // readOnly TextField가 스피너처럼 보이게
        OutlinedTextField(
            value = selected?.name.orEmpty(),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            modifier = Modifier
                .menuAnchor() // 중요!
                .widthIn(min = dp180),
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
            key = { it.id ?: -1 } // ✅ 끊김 방지
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
            .clip(shape = RoundedCornerShape(size = dp14))
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
            style = MaterialTheme.typography.labelSmall.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(height = dp2))
        Text(
            text = episode.name ?: "",
            style = MaterialTheme.typography.bodyMedium.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SimilarSection(
    similar: LazyPagingItems<Tv>,
    goToTv: (Int) -> Unit
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
                                onClick = { goToTv(similar[index]?.id ?: -1) },
                                cornerRadius = dp12
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
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
                        modifier = Modifier.semantics { contentDescription = episode.name ?: "" }.weight(weight = 1f),
                        text = episode.name ?: "",
                        fontSize = sp15,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                        maxLines = 1
                    )
                    Text(
                        modifier = Modifier.padding(start = dp5),
                        text = "${episode.runtime}분",
                        fontSize = sp15,
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                        maxLines = 1
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
                        modifier = Modifier.fillMaxWidth(),
                        state = guestStarScrollState,
                        contentPadding = PaddingValues(horizontal = dp16),
                        horizontalArrangement = Arrangement.spacedBy(space = dp10)
                    ) {
                        items(
                            items = episode.guestStars ?: emptyList(),
                            key = { "${it.id}_${it.creditId}_${it.creditId}" }
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
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = actor.character ?: "",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = sp10,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                                )
                                Text(
                                    text = actor.name ?: "",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = sp10,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
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
                        modifier = Modifier.fillMaxWidth(),
                        state = crewScrollState,
                        contentPadding = PaddingValues(horizontal = dp16),
                        horizontalArrangement = Arrangement.spacedBy(space = dp10)
                    ) {
                        items(
                            items = episode.crew ?: emptyList(),
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
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = crew.department ?: "",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = sp10,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                                )
                                Text(
                                    text = crew.name ?: "",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = sp10,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                                )
                                Text(
                                    text = crew.job ?: "",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = sp10,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}