package com.bowoon.people

import android.content.Intent
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Favorite
import com.bowoon.model.PeopleDetail
import com.bowoon.model.getRelatedMovie
import com.bowoon.movie.core.ui.R
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.dialog.ModalBottomSheetDialog
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp100
import com.bowoon.ui.utils.dp20
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.image.DynamicAsyncImageLoader
import kotlinx.coroutines.launch

@Composable
fun PeopleScreen(
    onBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: PeopleVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("PeopleScreen", "people screen start!")

    val peopleState by viewModel.people.collectAsStateWithLifecycle()

    PeopleScreen(
        peopleState = peopleState,
        onBack = onBack,
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
    onBack: () -> Unit,
    insertFavoritePeople: (Favorite) -> Unit,
    deleteFavoritePeople: (Favorite) -> Unit,
    goToMovie: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    restart: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var people by remember { mutableStateOf<PeopleDetail?>(null) }

    when (peopleState) {
        is PeopleState.Loading -> {
            Log.d("loading...")
            isLoading = true
        }
        is PeopleState.Success -> {
            Log.d("${peopleState.data}")
            isLoading = false
            people = peopleState.data
        }
        is PeopleState.Error -> {
            Log.e("${peopleState.throwable.message}")
            isLoading = false
            ConfirmDialog(
                title = stringResource(com.bowoon.movie.core.network.R.string.network_failed),
                message = "${peopleState.throwable.message}",
                confirmPair = stringResource(R.string.retry_message) to { restart() },
                dismissPair = stringResource(R.string.back_message) to onBack
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        people?.let {
            PeopleDetailComponent(
                people = it,
                onBack = onBack,
                goToMovie = goToMovie,
                insertFavoritePeople = insertFavoritePeople,
                deleteFavoritePeople = deleteFavoritePeople,
                onShowSnackbar = onShowSnackbar
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.semantics { contentDescription = "peopleDetailLoading" }.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun PeopleDetailComponent(
    people: PeopleDetail,
    onBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    insertFavoritePeople: (Favorite) -> Unit,
    deleteFavoritePeople: (Favorite) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()
    val scope = rememberCoroutineScope()
    val relatedMovie = people.combineCredits?.getRelatedMovie() ?: emptyList()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val snackbarMessage = if (people.isFavorite) stringResource(com.bowoon.movie.feature.people.R.string.remove_favorite_people) else stringResource(com.bowoon.movie.feature.people.R.string.add_favorite_people)

        TitleComponent(
            title = people.name ?: stringResource(com.bowoon.movie.feature.people.R.string.title_people),
            onBackClick = onBack,
            onFavoriteClick = {
                val favorite = Favorite(
                    id = people.id,
                    title = people.name,
                    imagePath = people.profilePath
                )
                if (people.isFavorite) {
                    deleteFavoritePeople(favorite)
                } else {
                    insertFavoritePeople(favorite)
                }
                scope.launch {
                    onShowSnackbar(snackbarMessage, null)
                }
            },
            isFavorite = people.isFavorite
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(dp10),
            horizontalArrangement = Arrangement.spacedBy(dp10),
            verticalArrangement = Arrangement.spacedBy(dp10)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ImageComponent(people)
                    Spacer(modifier = Modifier.width(dp5))
                    Column {
                        PeopleInfoComponent(people = people)
                        ExternalIdLinkComponent(people = people)
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
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
                        .aspectRatio(POSTER_IMAGE_RATIO)
                        .bounceClick { goToMovie(movie.id ?: -1) },
                    source = "$posterUrl${movie.posterPath}",
                    contentDescription = "RelatedMovie"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageComponent(
    people: PeopleDetail
) {
    var isShowing by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val posterUrl = LocalMovieAppDataComposition.current.getImageUrl()
    val items = people.images?.map { it.copy(filePath = "$posterUrl${it.filePath}") } ?: emptyList()

    DynamicAsyncImageLoader(
        source = "$posterUrl${people.profilePath}",
        contentDescription = "PeopleImage",
        modifier = Modifier
            .width(dp100)
            .aspectRatio(PEOPLE_IMAGE_RATIO)
            .clickable {
                index = 0
                isShowing = true
            }
    )

    if (isShowing) {
        ModalBottomSheetDialog(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PEOPLE_IMAGE_RATIO),
            state = modalBottomSheetState,
            scope = scope,
            index = index,
            imageList = items,
            onClickCancel = {
                scope.launch {
                    isShowing = false
                    modalBottomSheetState.hide()
                }
            }
        )
    }
}

@Composable
fun ExternalIdLinkComponent(
    link: String,
    @DrawableRes resourceId: Int,
    contentDescription: String
) {
    val context = LocalContext.current

    link.takeIf { it.isNotEmpty() }?.let {
        Icon(
            modifier = Modifier
                .size(dp20)
                .clickable {
                    context.startActivity(Intent(Intent.ACTION_VIEW, link.toUri()))
                },
            painter = painterResource(id = resourceId),
            contentDescription = contentDescription
        )
    }
}

@Composable
fun ExternalIdLinkComponent(people: PeopleDetail) {
    val context = LocalContext.current

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
    people: PeopleDetail
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