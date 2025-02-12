package com.bowoon.people

import android.content.Intent
import android.net.Uri
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bowoon.common.Log
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.PeopleDetail
import com.bowoon.model.getRelatedMovie
import com.bowoon.movie.core.ui.R
import com.bowoon.ui.ConfirmDialog
import com.bowoon.ui.ModalBottomSheetDialog
import com.bowoon.ui.Title
import com.bowoon.ui.bounceClick
import com.bowoon.ui.dp10
import com.bowoon.ui.dp100
import com.bowoon.ui.dp20
import com.bowoon.ui.dp5
import com.bowoon.ui.image.DynamicAsyncImageLoader
import kotlinx.coroutines.launch

@Composable
fun PeopleScreen(
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: PeopleVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("PeopleScreen", "people screen start!")

    val peopleState by viewModel.people.collectAsStateWithLifecycle()

    PeopleScreen(
        peopleState = peopleState,
        navController = navController,
        insertFavoritePeople = viewModel::insertPeople,
        deleteFavoritePeople = viewModel::deletePeople,
        onMovieClick = onMovieClick,
        onShowSnackbar = onShowSnackbar,
        restart = viewModel::restart
    )
}

@Composable
fun PeopleScreen(
    peopleState: PeopleState,
    navController: NavController,
    insertFavoritePeople: (PeopleDetail) -> Unit,
    deleteFavoritePeople: (PeopleDetail) -> Unit,
    onMovieClick: (Int) -> Unit,
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
                title = "통신 실패",
                message = "${peopleState.throwable.message}",
                confirmPair = "재시도" to { restart() },
                dismissPair = "돌아가기" to { navController.navigateUp() }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        people?.let {
            PeopleDetailComponent(
                people = it,
                navController = navController,
                onMovieClick = onMovieClick,
                insertFavoritePeople = insertFavoritePeople,
                deleteFavoritePeople = deleteFavoritePeople,
                onShowSnackbar = onShowSnackbar
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun PeopleDetailComponent(
    people: PeopleDetail,
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    insertFavoritePeople: (PeopleDetail) -> Unit,
    deleteFavoritePeople: (PeopleDetail) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val scope = rememberCoroutineScope()
    val relatedMovie = people.combineCredits?.getRelatedMovie() ?: emptyList()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Title(
            title = people.name ?: "인물 정보",
            onBackClick = { navController.navigateUp() },
            onFavoriteClick = {
                if (people.isFavorite) {
                    deleteFavoritePeople(people)
                } else {
                    insertFavoritePeople(people)
                }
                scope.launch {
                    onShowSnackbar(
                        if (people.isFavorite) "좋아하는 인물에서 제거했습니다." else "좋아하는 인물에 추가했습니다.",
                        null
                    )
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
            item(span = { GridItemSpan(3) }) {
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
            item(span = { GridItemSpan(3) }) {
                people.biography?.takeIf { it.isNotEmpty() }?.let {
                    Text(text = it)
                }
            }
            items(items = relatedMovie) { movie ->
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(POSTER_IMAGE_RATIO)
                        .bounceClick { onMovieClick(movie.id ?: -1) },
                    source = movie.posterPath ?: "",
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
    val items = people.images?.map { it.copy(filePath = "${people.posterUrl}${it.filePath}") } ?: emptyList()

    DynamicAsyncImageLoader(
        source = people.profilePath ?: "",
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
fun ExternalIdLinkComponent(people: PeopleDetail) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        people.externalIds?.wikidataId?.takeIf { it.isNotEmpty() }?.let {
            Icon(
                modifier = Modifier
                    .size(dp20)
                    .clickable {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.wikidata.org/wiki/$it")
                            )
                        )
                    },
                painter = painterResource(id = R.drawable.ic_wiki),
                contentDescription = "wikidataId"
            )
        }
        people.externalIds?.facebookId?.takeIf { it.isNotEmpty() }?.let {
            Icon(
                modifier = Modifier
                    .size(dp20)
                    .clickable {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.facebook.com/$it")
                            )
                        )
                    },
                painter = painterResource(id = R.drawable.ic_facebook),
                contentDescription = "facebookId"
            )
        }
        people.externalIds?.twitterId?.takeIf { it.isNotEmpty() }?.let {
            Icon(
                modifier = Modifier
                    .size(dp20)
                    .clickable {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://x.com/$it")
                            )
                        )
                    },
                painter = painterResource(id = R.drawable.ic_twitter),
                contentDescription = "twitterId"
            )
        }
        people.externalIds?.instagramId?.takeIf { it.isNotEmpty() }?.let {
            Icon(
                modifier = Modifier
                    .size(dp20)
                    .clickable {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.instagram.com/$it/")
                            )
                        )
                    },
                painter = painterResource(id = R.drawable.ic_instagram),
                contentDescription = "instagramId"
            )
        }
        people.externalIds?.youtubeId?.takeIf { it.isNotEmpty() }?.let {
            Icon(
                modifier = Modifier
                    .size(dp20)
                    .clickable {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/$it")
                            )
                        )
                    },
                painter = painterResource(id = R.drawable.ic_youtube),
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
            Text(text = it)
        }
        if (!people.birthday.isNullOrEmpty() && people.deathday.isNullOrEmpty()) {
            Text(text = people.birthday ?: "")
        } else if (!people.birthday.isNullOrEmpty() && !people.deathday.isNullOrEmpty()) {
            Text(text = "${people.birthday} ~ ${people.deathday}")
        }
        people.placeOfBirth?.takeIf { it.isNotEmpty() }?.let {
            Text(text = it)
        }
    }
}