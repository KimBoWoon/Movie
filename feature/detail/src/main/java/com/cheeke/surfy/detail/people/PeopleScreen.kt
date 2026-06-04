package com.cheeke.surfy.detail.people

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cheeke.surfy.analytics.LocalAnalyticsHelper
import com.cheeke.surfy.analytics.TrackScreenViewEvent
import com.cheeke.surfy.analytics.logFavorite
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
import com.cheeke.surfy.feature.detail.R
import com.cheeke.surfy.firebase.LocalFirebaseLogHelper
import com.cheeke.surfy.model.Image
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.MediaType
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.getRelatedMovie
import com.cheeke.surfy.ui.components.CircularProgressComponent
import com.cheeke.surfy.ui.components.ExternalIdLinkComponent
import com.cheeke.surfy.ui.components.TitleComponent
import com.cheeke.surfy.ui.dialog.ConfirmDialog
import com.cheeke.surfy.ui.dialog.Indexer
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp20
import com.cheeke.surfy.ui.utils.dp200
import com.cheeke.surfy.ui.utils.fullBleed
import com.cheeke.surfy.ui.utils.roundedCornerClickable
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun PeopleScreen(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: PeopleVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("PeopleScreen", "people screen start!")
    TrackScreenViewEvent(screenName = "PeopleScreen")

    val peopleState by viewModel.people.collectAsStateWithLifecycle()

    PeopleScreen(
        peopleState = peopleState,
        goToBack = goToBack,
        insertFavoritePeople = viewModel::insertPeople,
        deleteFavoritePeople = viewModel::deletePeople,
        goToMovie = goToMovie,
        goToTv = goToTv,
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
    goToTv: (Int) -> Unit,
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
                    goToTv = goToTv,
                    insertFavoritePeople = insertFavoritePeople,
                    deleteFavoritePeople = deleteFavoritePeople,
                    onShowSnackbar = onShowSnackbar
                )
            }
            is PeopleState.Error -> {
                Log.e("${peopleState.throwable.message}")

                val message = peopleState.throwable.stringRes?.let { stringResource(id = it) } ?: stringResource(id = com.cheeke.surfy.core.network.R.string.something_wrong)

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
fun PeopleDetailComponent(
    people: People,
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    insertFavoritePeople: (People) -> Unit,
    deleteFavoritePeople: (People) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val scope = rememberCoroutineScope()

    val relatedMovie = people.combineCredits?.getRelatedMovie()?.sortedWith(
        compareByDescending<Media> {
            if (it.releaseDate.isNullOrEmpty()) {
                LocalDate.MAX
            } else {
                LocalDate.parse(it.releaseDate)
            }
        }.thenByDescending { it.title }
    ).orEmpty()
    val snackbarMessage = if (people.isFavorite) stringResource(id = R.string.remove_favorite_people) else stringResource(id = R.string.add_favorite_people)
    val lazyGridScrollState = rememberLazyGridState()
    val analyticsHelper = LocalAnalyticsHelper.current

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(count = 3),
        state = lazyGridScrollState,
        contentPadding = PaddingValues(start = dp10, end = dp10, bottom = dp20),
        horizontalArrangement = Arrangement.spacedBy(space = dp10),
        verticalArrangement = Arrangement.spacedBy(space = dp10)
    ) {
        item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
            ProfileComponent(
                people = people,
                images = people.images.orEmpty(),
                goToBack = goToBack,
                onFavorite = {
                    if (people.isFavorite) {
                        deleteFavoritePeople(people)
                        analyticsHelper.logFavorite(isFavorite = false, contentType = "people", media = people)
                    } else {
                        insertFavoritePeople(people)
                        analyticsHelper.logFavorite(isFavorite = true, contentType = "people", media = people)
                    }
                    scope.launch { onShowSnackbar(snackbarMessage, null) }
                }
            )
        }

        item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
            ExternalIdLinkComponent(people = people)
        }

        if (!people.biography.isNullOrBlank()) {
            item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                Text(
                    modifier = Modifier.semantics { contentDescription = "peopleBiography" },
                    text = people.biography.orEmpty()
                )
            }
        }

        items(
            items = relatedMovie,
            key = { media -> "${media.mediaType}_${media.id}" },
            contentType = { "people_credit_poster" }
        ) { media ->
            DynamicAsyncImageLoader(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                    .roundedCornerClickable(
                        onClick = {
                            when (media.mediaType) {
                                MediaType.MOVIE -> goToMovie(media.id ?: -1)
                                MediaType.TV -> goToTv(media.id ?: -1)
                                else -> {
                                    scope.launch { onShowSnackbar("MediaType not found...", null) }
                                    return@roundedCornerClickable
                                }
                            }
                        },
                        cornerRadius = dp10
                    ),
                source = media.posterPath.orEmpty(),
                contentDescription = "RelatedMovie"
            )
        }
    }
}

@Composable
fun ProfileComponent(
    people: People,
    images: List<Image>,
    goToBack: () -> Unit,
    onFavorite: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(minimumValue = 1) })

    Box(
        modifier = Modifier.fillMaxWidth().fullBleed(horizontalPadding = dp10)
    ) {
        if (images.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .semantics { contentDescription = "peopleImageHorizontalPager" }
                    .fillMaxWidth()
                    .aspectRatio(ratio = POSTER_IMAGE_RATIO)
            ) { page ->
                val image = images.getOrNull(index = page) ?: images.firstOrNull()

                DynamicAsyncImageLoader(
                    modifier = Modifier.fillMaxSize(),
                    source = image?.filePath.orEmpty(),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }

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
        } else {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(height = dp200))
        }

        TitleComponent(
            isFavorite = people.isFavorite,
            goToBack = goToBack,
            onFavorite = onFavorite
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (images.isNotEmpty()) {
                Indexer(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(
                            color = Color(color = 0x33000000),
                            shape = RoundedCornerShape(size = dp20)
                        ),
                    current = pagerState.currentPage + 1,
                    size = images.size
                )
            }

            Text(
                text = people.title ?: "",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = people.knownForDepartment ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )

            if (!people.birthday.isNullOrEmpty() && !people.deathday.isNullOrEmpty()) {
                Text(
                    text = "${people.birthday} ~ ${people.deathday}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            } else {
                Text(
                    text = people.birthday ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ExternalIdLinkComponent(people: People) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center
    ) {
        people.externalIds?.wikidataId?.let {
            ExternalIdLinkComponent(
                link = "https://www.wikidata.org/wiki/$it",
                resourceId = com.cheeke.surfy.core.ui.R.drawable.ic_wiki,
                contentDescription = "wikidataId"
            )
        }
        people.externalIds?.facebookId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.facebook.com/$it",
                resourceId = com.cheeke.surfy.core.ui.R.drawable.ic_facebook,
                contentDescription = "facebookId"
            )
        }
        people.externalIds?.twitterId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://x.com/$it",
                resourceId = com.cheeke.surfy.core.ui.R.drawable.ic_twitter,
                contentDescription = "twitterId"
            )
        }
        people.externalIds?.tiktokId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.tiktok.com/@$it",
                resourceId = com.cheeke.surfy.core.ui.R.drawable.ic_tiktok,
                contentDescription = "tiktokId"
            )
        }
        people.externalIds?.instagramId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.instagram.com/$it/",
                resourceId = com.cheeke.surfy.core.ui.R.drawable.ic_instagram,
                contentDescription = "instagramId"
            )
        }
        people.externalIds?.youtubeId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.youtube.com/$it",
                resourceId = com.cheeke.surfy.core.ui.R.drawable.ic_youtube,
                contentDescription = "youtubeId"
            )
        }
    }
}