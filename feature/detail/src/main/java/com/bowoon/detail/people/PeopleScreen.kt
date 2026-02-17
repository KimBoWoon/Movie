package com.bowoon.detail.people

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.bowoon.common.Log
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.domain.PeopleWithFavorite
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.MediaType
import com.bowoon.model.People
import com.bowoon.model.getRelatedMovie
import com.bowoon.movie.feature.detail.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.components.ExternalIdLinkComponent
import com.bowoon.ui.components.FavoriteButtonComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.dialog.Indexer
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.roundedCornerClickable
import kotlinx.coroutines.launch

@Composable
fun PeopleScreen(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
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
                ConfirmDialog(
                    title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                    message = "${peopleState.throwable.message}",
                    confirmPair = stringResource(id = com.bowoon.movie.core.ui.R.string.retry_message) to { restart() },
                    dismissPair = stringResource(id = com.bowoon.movie.core.ui.R.string.back_message) to goToBack
                )
            }
        }
    }
}

@Composable
fun PeopleDetailComponent(
    people: PeopleWithFavorite,
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    insertFavoritePeople: (People) -> Unit,
    deleteFavoritePeople: (People) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val scope = rememberCoroutineScope()
    val relatedMovie = people.people.combineCredits?.getRelatedMovie() ?: emptyList()
    val snackbarMessage = if (people.isFavorite) stringResource(id = R.string.remove_favorite_people) else stringResource(id = R.string.add_favorite_people)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(state = scrollState),
    ) {
        ProfileHeader(
            people = people,
            images = people.people.images?.mapNotNull { it.filePath } ?: emptyList(),
            onBack = goToBack,
            onFavorite = {
                if (people.isFavorite) {
                    deleteFavoritePeople(people.people)
                } else {
                    insertFavoritePeople(people.people)
                }
                scope.launch {
                    onShowSnackbar(snackbarMessage, null)
                }
            }
        )
        ExternalIdLinkComponent(people = people.people)
        people.people.biography?.takeIf { it.isNotEmpty() }?.let {
            Spacer(modifier = Modifier.padding(vertical = dp10))
            Text(
                modifier = Modifier
                    .semantics { contentDescription = "peopleBiography" }
                    .padding(horizontal = dp10),
                text = it
            )
        }
        Spacer(modifier = Modifier.padding(vertical = dp10))
        val rows = remember(key1 = relatedMovie) { relatedMovie.chunked(size = 3) }

        Column(
            modifier = Modifier.padding(start = dp10, end = dp10, bottom = dp20),
            verticalArrangement = Arrangement.spacedBy(space = dp10)
        ) {
            rows.forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(space = dp10)) {
                    rowItems.forEach { media ->
                        DynamicAsyncImageLoader(
                            modifier = Modifier
                                .weight(weight = 1f)
                                .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                                .roundedCornerClickable(
                                    onClick = {
                                        when (media.mediaType) {
                                            MediaType.NONE -> {
                                                scope.launch {
                                                    onShowSnackbar("MediaType not found...", null)
                                                }
                                                return@roundedCornerClickable
                                            }

                                            MediaType.MOVIE -> goToMovie(media.id ?: -1)
                                            MediaType.TV -> goToTv(media.id ?: -1)
                                        }
                                    }, cornerRadius = dp10
                                ),
                            source = media.posterPath ?: "",
                            contentDescription = "RelatedMovie"
                        )
                    }
                    // 마지막 줄이 3개 미만일 때 빈칸 채우기
                    repeat(times = 3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(weight = 1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    people: PeopleWithFavorite,
    images: List<String>,
    onBack: () -> Unit,
    onFavorite: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(minimumValue = 1) })

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (images.isNotEmpty()) {
            // 배경 Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().aspectRatio(ratio = POSTER_IMAGE_RATIO)
            ) { page ->
                val url = images.getOrNull(index = page) ?: images.firstOrNull()

                DynamicAsyncImageLoader(
                    modifier = Modifier.fillMaxSize(),
                    source = url ?: "",
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }

            // Dim + Gradient(텍스트/카드 가독성)
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
        }

        // 상단 아이콘 Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.weight(weight = 1f))

            FavoriteButtonComponent(
                modifier = Modifier
                    .padding(end = dp16)
                    .wrapContentSize(),
                isFavorite = people.isFavorite,
                onClick = { onFavorite() }
            )
        }

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
            // 중앙 타이틀 (목업 느낌: 이름을 헤더 중앙에)
            Text(
                text = people.people.title ?: "",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = people.people.knownForDepartment ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )

            if (!people.people.birthday.isNullOrEmpty() && !people.people.deathday.isNullOrEmpty()) {
                Text(
                    text = "${people.people.birthday} ~ ${people.people.deathday}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            } else {
                Text(
                    text = people.people.birthday ?: "",
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
                resourceId = com.bowoon.movie.core.ui.R.drawable.ic_wiki,
                contentDescription = "wikidataId"
            )
        }
        people.externalIds?.facebookId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.facebook.com/$it",
                resourceId = com.bowoon.movie.core.ui.R.drawable.ic_facebook,
                contentDescription = "facebookId"
            )
        }
        people.externalIds?.twitterId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://x.com/$it",
                resourceId = com.bowoon.movie.core.ui.R.drawable.ic_twitter,
                contentDescription = "twitterId"
            )
        }
        people.externalIds?.tiktokId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.tiktok.com/@$it",
                resourceId = com.bowoon.movie.core.ui.R.drawable.ic_tiktok,
                contentDescription = "tiktokId"
            )
        }
        people.externalIds?.instagramId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.instagram.com/$it/",
                resourceId = com.bowoon.movie.core.ui.R.drawable.ic_instagram,
                contentDescription = "instagramId"
            )
        }
        people.externalIds?.youtubeId?.takeIf { it.isNotEmpty() }?.let {
            ExternalIdLinkComponent(
                link = "https://www.youtube.com/$it",
                resourceId = com.bowoon.movie.core.ui.R.drawable.ic_youtube,
                contentDescription = "youtubeId"
            )
        }
    }
}