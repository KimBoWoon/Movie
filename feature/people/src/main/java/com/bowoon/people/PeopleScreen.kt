package com.bowoon.people

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Image
import com.bowoon.model.People
import com.bowoon.model.getRelatedMovie
import com.bowoon.movie.core.ui.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.components.ExternalIdLinkComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.dialog.Indexer
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp0
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp100
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp30
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp70
import kotlinx.coroutines.launch

@Composable
fun PeopleScreen(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: PeopleVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("PeopleScreen", "people screen start!")

    val peopleState by viewModel.people.collectAsStateWithLifecycle()

    PeopleScreen(
        peopleState = peopleState,
        goToBack = goToBack,
        insertFavoritePeople = viewModel::insertPeople,
        deleteFavoritePeople = viewModel::deletePeople,
        goToMovie = goToMovie,
        onShowSnackbar = onShowSnackbar,
        restart = viewModel::restart
    )
}

@Composable
fun PeopleScreen(
    peopleState: PeopleState,
    goToBack: () -> Unit,
    insertFavoritePeople: (People) -> Unit,
    deleteFavoritePeople: (People) -> Unit,
    goToMovie: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    restart: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (peopleState) {
            is PeopleState.Loading -> {
                Log.d("loading...")
                CircularProgressComponent(
                    modifier = Modifier
                        .semantics { contentDescription = "peopleDetailLoading" }
                        .align(Alignment.Center)
                )
            }
            is PeopleState.Success -> {
                Log.d("${peopleState.data}")

                PeopleDetailComponent(
                    people = peopleState.data,
                    goToBack = goToBack,
                    goToMovie = goToMovie,
                    insertFavoritePeople = insertFavoritePeople,
                    deleteFavoritePeople = deleteFavoritePeople,
                    onShowSnackbar = onShowSnackbar
                )
            }
            is PeopleState.Error -> {
                Log.e("${peopleState.throwable.message}")
                ConfirmDialog(
                    title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                    message = "${peopleState.throwable.message}",
                    confirmPair = stringResource(id = R.string.retry_message) to { restart() },
                    dismissPair = stringResource(id = R.string.back_message) to goToBack
                )
            }
        }
    }
}

@Composable
fun PeopleDetailComponent(
    people: People,
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    insertFavoritePeople: (People) -> Unit,
    deleteFavoritePeople: (People) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val scope = rememberCoroutineScope()
    val relatedMovie = people.combineCredits?.getRelatedMovie() ?: emptyList()
    val snackbarMessage = if (people.isFavorite) stringResource(id = com.bowoon.movie.feature.people.R.string.remove_favorite_people) else stringResource(id = com.bowoon.movie.feature.people.R.string.add_favorite_people)
    val scrollState = rememberLazyGridState()
    val toggle = remember { mutableStateOf(value = false) }
    val peopleImagesHeight = remember { mutableStateOf(value = dp0) }
    val peopleInfoHeight by animateDpAsState(
        targetValue = if (toggle.value) peopleImagesHeight.value / 4 else peopleImagesHeight.value
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleComponent(
            title = people.name ?: stringResource(id = com.bowoon.movie.feature.people.R.string.title_people),
            goToBack = goToBack,
            onFavoriteClick = {
                if (people.isFavorite) {
                    deleteFavoritePeople(people)
                } else {
                    insertFavoritePeople(people)
                }
                scope.launch {
                    onShowSnackbar(snackbarMessage, null)
                }
            },
            isFavorite = people.isFavorite
        )

        Box {
            people.images.takeIf { !it.isNullOrEmpty() }?.let { images ->
                PeopleImageComponent(
                    images = images,
                    peopleImagesHeight = peopleImagesHeight,
                    toggle = toggle
                )
            }

            Column(
                modifier = Modifier
                    .height(height = if (people.images.isNullOrEmpty()) Int.MAX_VALUE.dp else peopleInfoHeight)
                    .align(alignment = Alignment.BottomCenter)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(topStart = dp30, topEnd = dp30)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = dp20),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(modifier = Modifier
                        .width(width = dp70)
                        .height(height = dp5)
                        .background(color = Color.White, shape = RoundedCornerShape(size = dp100)))
                }
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState,
                    columns = GridCells.Fixed(count = 3),
                    contentPadding = PaddingValues(all = dp10),
                    horizontalArrangement = Arrangement.spacedBy(space = dp10),
                    verticalArrangement = Arrangement.spacedBy(space = dp10)
                ) {
                    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                PeopleInfoComponent(people = people)
                                ExternalIdLinkComponent(people = people)
                            }
                        }
                    }
                    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                        people.biography?.takeIf { it.isNotEmpty() }?.let {
                            Text(
                                modifier = Modifier.semantics { contentDescription = "peopleBiography" },
                                text = it
                            )
                        }
                    }
                    items(items = relatedMovie) { movie ->
                        DynamicAsyncImageLoader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                                .clip(shape = RoundedCornerShape(size = dp10))
                                .bounceClick { goToMovie(movie.id ?: -1) },
                            source = movie.posterPath ?: "",
                            contentDescription = "RelatedMovie"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoxScope.PeopleImageComponent(
    images: List<Image>,
    peopleImagesHeight: MutableState<Dp>,
    toggle: MutableState<Boolean>
) {
    val density = LocalDensity.current
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { images.size })

    HorizontalPager(
        modifier = Modifier
            .semantics { contentDescription = "peopleImageHorizontalPager" }
            .fillMaxSize()
            .aspectRatio(ratio = POSTER_IMAGE_RATIO, matchHeightConstraintsFirst = true)
            .onSizeChanged { size ->
                peopleImagesHeight.value = if (images.isEmpty()) {
                    size.height.dp
                } else {
                    with(receiver = density) {
                        (size.height.toFloat() / 1.2f).toInt().toDp()
                    }
                }
            }
            .clickable(interactionSource = null, indication = null) { toggle.value = !toggle.value },
        state = pagerState,
//        contentPadding = PaddingValues(horizontal = dp10),
//        pageSpacing = dp5
    ) { index ->
        DynamicAsyncImageLoader(
            modifier = Modifier.fillMaxSize(),
            source = images[index].filePath ?: "",
            contentDescription = images[index].filePath
        )
    }
    Indexer(
        modifier = Modifier
            .padding(top = dp10, end = dp20)
            .wrapContentSize()
            .background(
                color = Color(color = 0x33000000),
                shape = RoundedCornerShape(size = dp20)
            )
            .align(Alignment.TopEnd),
        current = pagerState.currentPage + 1,
        size = images.size
    )
}

@Composable
fun ExternalIdLinkComponent(people: People) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        people.externalIds?.wikidataId?.let {
            ExternalIdLinkComponent(
                link = "https://www.wikidata.org/wiki/$it",
                resourceId = R.drawable.ic_wiki,
                contentDescription = "wikidataId"
            )
        }
        people.externalIds?.facebookId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.facebook.com/$it",
                resourceId = R.drawable.ic_facebook,
                contentDescription = "facebookId"
            )
        }
        people.externalIds?.twitterId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://x.com/$it",
                resourceId = R.drawable.ic_twitter,
                contentDescription = "twitterId"
            )
        }
        people.externalIds?.tiktokId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.tiktok.com/@$it",
                resourceId = R.drawable.ic_tiktok,
                contentDescription = "tiktokId"
            )
        }
        people.externalIds?.instagramId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.instagram.com/$it/",
                resourceId = R.drawable.ic_instagram,
                contentDescription = "instagramId"
            )
        }
        people.externalIds?.youtubeId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.youtube.com/$it",
                resourceId = R.drawable.ic_youtube,
                contentDescription = "youtubeId"
            )
        }
    }
}

@Composable
fun PeopleInfoComponent(
    people: People
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        people.name?.takeIf { it.isNotEmpty() }?.let {
            Text(
                modifier = Modifier.semantics { contentDescription = "peopleName" },
                text = it
            )
        }
        Text(text = "${people.birthday ?: ""}${if (!people.deathday.isNullOrEmpty()) " ~ ${people.deathday}" else ""}")
        people.placeOfBirth?.takeIf { it.isNotEmpty() }?.let {
            Text(
                modifier = Modifier.semantics { contentDescription = "peoplePlaceOfBirth" },
                text = it
            )
        }
    }
}