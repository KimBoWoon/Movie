package com.bowoon.people

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.Favorite
import com.bowoon.model.People
import com.bowoon.model.getRelatedMovie
import com.bowoon.movie.core.ui.R
import com.bowoon.ui.components.CircularProgressComponent
import com.bowoon.ui.components.ExternalIdLinkComponent
import com.bowoon.ui.components.TitleComponent
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.dialog.Indexer
import com.bowoon.ui.dialog.ModalBottomSheetDialog
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
    insertFavoritePeople: (Favorite) -> Unit,
    deleteFavoritePeople: (Favorite) -> Unit,
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
    insertFavoritePeople: (Favorite) -> Unit,
    deleteFavoritePeople: (Favorite) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val relatedMovie = people.combineCredits?.getRelatedMovie() ?: emptyList()
    val snackbarMessage = if (people.isFavorite) stringResource(id = com.bowoon.movie.feature.people.R.string.remove_favorite_people) else stringResource(id = com.bowoon.movie.feature.people.R.string.add_favorite_people)
    val scrollState = rememberLazyGridState()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { people.images?.size ?: 0 })
    var peopleImagesHeight by remember { mutableStateOf(value = dp0) }
    var toggle by remember { mutableStateOf(value = false) }
    val peopleInfoHeight by animateDpAsState(
        targetValue = if (toggle) peopleImagesHeight / 3 else peopleImagesHeight
    )

    Column {
        TitleComponent(
            modifier = Modifier.layoutId(layoutId = "peopleTitle"),
            title = people.name ?: stringResource(id = com.bowoon.movie.feature.people.R.string.title_people),
            goToBack = goToBack,
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

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                modifier = Modifier
                    .wrapContentSize()
                    .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                    .onSizeChanged { size ->
                        peopleImagesHeight = if (people.images.isNullOrEmpty()) {
                            size.height.dp
                        } else {
                            with(receiver = density) {
                                (size.height.toFloat() / 1.2f).toInt().toDp()
                            }
                        }
                    }
                    .clickable(interactionSource = null, indication = null) { toggle = !toggle },
                state = pagerState,
//                contentPadding = PaddingValues(horizontal = dp10),
//                pageSpacing = dp5
            ) { index ->
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    source = people.images?.get(index)?.filePath ?: "",
                    contentDescription = people.images?.get(index)?.filePath
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
                size = people.images?.size ?: 0
            )

            Column(
                modifier = Modifier
                    .height(height = peopleInfoHeight)
                    .align(alignment = Alignment.BottomCenter)
                    .background(
                        color = Color.Black,
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

//        Layout(
//            modifier = Modifier.layoutId(layoutId = "peopleImages"),
//            content = {
//                Box(
//                    modifier = Modifier.layoutId(layoutId = "peopleImages").clickable(interactionSource = null, indication = null) { toggle = !toggle }
//                ) {
//                    HorizontalPager(
//                        modifier = Modifier.fillMaxWidth().aspectRatio(ratio = POSTER_IMAGE_RATIO),
//                        state = pagerState,
//                        contentPadding = PaddingValues(horizontal = dp10),
//                        pageSpacing = dp5
//                    ) { index ->
//                        DynamicAsyncImageLoader(
//                            modifier = Modifier.fillMaxSize(),
//                            source = people.images?.get(index)?.filePath ?: "",
//                            contentDescription = people.images?.get(index)?.filePath
//                        )
//                    }
//                    Indexer(
//                        modifier = Modifier
//                            .padding(top = dp10, end = dp20)
//                            .wrapContentSize()
//                            .background(
//                                color = Color(color = 0x33000000),
//                                shape = RoundedCornerShape(size = dp20)
//                            )
//                            .align(Alignment.TopEnd),
//                        current = pagerState.currentPage + 1,
//                        size = people.images?.size ?: 0
//                    )
//                }
//
//                Column(
//                    modifier = Modifier
//                        .layoutId(layoutId = "peopleInfo")
//                        .background(color = Color.Black, shape = RoundedCornerShape(topStart = dp30, topEnd = dp30))
//                ) {
//                    Box(
//                        modifier = Modifier.fillMaxWidth().height(height = dp20),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Spacer(modifier = Modifier.width(width = dp70).height(height = dp5).background(color = Color.White, shape = RoundedCornerShape(size = dp100)))
//                    }
//                    LazyVerticalGrid(
//                        state = scrollState,
//                        columns = GridCells.Fixed(count = 3),
//                        contentPadding = PaddingValues(all = dp10),
//                        horizontalArrangement = Arrangement.spacedBy(space = dp10),
//                        verticalArrangement = Arrangement.spacedBy(space = dp10)
//                    ) {
//                        item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
//                            Row(
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                Column {
//                                    PeopleInfoComponent(people = people)
//                                    ExternalIdLinkComponent(people = people)
//                                }
//                            }
//                        }
//                        item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
//                            people.biography?.takeIf { it.isNotEmpty() }?.let {
//                                Text(
//                                    modifier = Modifier.semantics { contentDescription = "peopleBiography" },
//                                    text = it
//                                )
//                            }
//                        }
//                        items(items = relatedMovie) { movie ->
//                            DynamicAsyncImageLoader(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .aspectRatio(ratio = POSTER_IMAGE_RATIO)
//                                    .clip(shape = RoundedCornerShape(size = dp10))
//                                    .bounceClick { goToMovie(movie.id ?: -1) },
//                                source = movie.posterPath ?: "",
//                                contentDescription = "RelatedMovie"
//                            )
//                        }
//                    }
//                }
//            }
//        ) { measurable, constraints ->
//            val images = measurable.first { it.layoutId == "peopleImages" }.measure(constraints)
//            val info = measurable.first { it.layoutId == "peopleInfo" }.measure(Constraints(minWidth = constraints.minWidth, maxWidth = constraints.maxWidth, minHeight = constraints.minHeight, maxHeight = 1050))
//
//            peopleImagesHeight = if (people.images.isNullOrEmpty()) 0 else images.height
//
//            layout(width = constraints.maxWidth, height = constraints.maxHeight) {
//                images.placeRelative(x = 0, y = 0)
//                info.placeRelative(x = 0, y = images.height / 2)
//            }
//        }
//    }

//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        TitleComponent(
//            modifier = Modifier.layoutId(layoutId = "peopleTitle"),
//            title = people.name ?: stringResource(id = com.bowoon.movie.feature.people.R.string.title_people),
//            goToBack = goToBack,
//            onFavoriteClick = {
//                val favorite = Favorite(
//                    id = people.id,
//                    title = people.name,
//                    imagePath = people.profilePath
//                )
//                if (people.isFavorite) {
//                    deleteFavoritePeople(favorite)
//                } else {
//                    insertFavoritePeople(favorite)
//                }
//                scope.launch {
//                    onShowSnackbar(snackbarMessage, null)
//                }
//            },
//            isFavorite = people.isFavorite
//        )
//
//        Box(
//            modifier = Modifier
//                .layoutId(layoutId = "peopleImages")
//                .wrapContentSize()
//                .clickable { toggle = !toggle }
////                .graphicsLayer {
////                    scaleX = imageScale
////                    scaleY = imageScale
////                    transformOrigin = TransformOrigin.Center
////                    imageScale = 1f - (scrollState.firstVisibleItemScrollOffset.toFloat() / scrollState.layoutInfo.visibleItemsInfo[0].size.height.toFloat()).coerceIn(minimumValue = 0f, maximumValue = 1f)
////                }
//        ) {
//            HorizontalPager(
//                modifier = Modifier.fillMaxWidth().aspectRatio(ratio = POSTER_IMAGE_RATIO),
//                state = pagerState,
//                contentPadding = PaddingValues(horizontal = dp10),
//                pageSpacing = dp5
//            ) { index ->
//                DynamicAsyncImageLoader(
//                    modifier = Modifier.fillMaxSize(),
//                    source = people.images?.get(index)?.filePath ?: "",
//                    contentDescription = people.images?.get(index)?.filePath
//                )
//            }
//            Indexer(
//                modifier = Modifier
//                    .padding(top = dp10, end = dp20)
//                    .wrapContentSize()
//                    .background(color = Color(color = 0x33000000), shape = RoundedCornerShape(size = dp20))
//                    .align(Alignment.TopEnd),
//                current = pagerState.currentPage + 1,
//                size = people.images?.size ?: 0
//            )
//
//            LazyVerticalGrid(
//                modifier = Modifier
//                    .layout { measurable, constraints ->
//                        val a = measurable.measure(constraints)
//                        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
//                            a.placeRelative(x = 0, y = if (toggle) 100.dp.value.toInt() else 800.dp.value.toInt())
//                        }
//                    }
//                    .layoutId(layoutId = "peopleInfo")
//                    .fillMaxHeight(),
//                state = scrollState,
//                columns = GridCells.Fixed(count = 3),
//                contentPadding = PaddingValues(all = dp10),
//                horizontalArrangement = Arrangement.spacedBy(space = dp10),
//                verticalArrangement = Arrangement.spacedBy(space = dp10)
//            ) {
////                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
////                    Box(
////                        modifier = Modifier
////                            .layoutId(layoutId = "peopleImages")
////                            .wrapContentSize()
//////                            .graphicsLayer {
//////                                scaleX = imageScale
//////                                scaleY = imageScale
//////                                transformOrigin = TransformOrigin.Center
//////                                imageScale = 1f - (scrollState.firstVisibleItemScrollOffset.toFloat() / scrollState.layoutInfo.visibleItemsInfo[0].size.height.toFloat()).coerceIn(minimumValue = 0f, maximumValue = 1f)
//////                            }
////                    ) {
////                        HorizontalPager(
////                            modifier = Modifier.fillMaxWidth().aspectRatio(ratio = POSTER_IMAGE_RATIO),
////                            state = pagerState,
////                            contentPadding = PaddingValues(horizontal = dp10),
////                            pageSpacing = dp5
////                        ) { index ->
////                            DynamicAsyncImageLoader(
////                                modifier = Modifier.fillMaxSize(),
////                                source = people.images?.get(index)?.filePath ?: "",
////                                contentDescription = people.images?.get(index)?.filePath
////                            )
////                        }
////                        Indexer(
////                            modifier = Modifier
////                                .padding(top = dp10, end = dp20)
////                                .wrapContentSize()
////                                .background(color = Color(color = 0x33000000), shape = RoundedCornerShape(size = dp20))
////                                .align(Alignment.TopEnd),
////                            current = pagerState.currentPage + 1,
////                            size = people.images?.size ?: 0
////                        )
////                    }
////                }
//                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Column {
//                            PeopleInfoComponent(people = people)
//                            ExternalIdLinkComponent(people = people)
//                        }
//                    }
//                }
//                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
//                    people.biography?.takeIf { it.isNotEmpty() }?.let {
//                        Text(
//                            modifier = Modifier.semantics { contentDescription = "peopleBiography" },
//                            text = it
//                        )
//                    }
//                }
//                items(items = relatedMovie) { movie ->
//                    DynamicAsyncImageLoader(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .aspectRatio(ratio = POSTER_IMAGE_RATIO)
//                            .clip(shape = RoundedCornerShape(size = dp10))
//                            .bounceClick { goToMovie(movie.id ?: -1) },
//                        source = movie.posterPath ?: "",
//                        contentDescription = "RelatedMovie"
//                    )
//                }
//            }
//        }
//        Scaffold(
//            modifier = Modifier.fillMaxSize(),
//            topBar = {
//                TitleComponent(
//                    modifier = Modifier.layoutId(layoutId = "peopleTitle"),
//                    title = people.name ?: stringResource(id = com.bowoon.movie.feature.people.R.string.title_people),
//                    goToBack = goToBack,
//                    onFavoriteClick = {
//                        val favorite = Favorite(
//                            id = people.id,
//                            title = people.name,
//                            imagePath = people.profilePath
//                        )
//                        if (people.isFavorite) {
//                            deleteFavoritePeople(favorite)
//                        } else {
//                            insertFavoritePeople(favorite)
//                        }
//                        scope.launch {
//                            onShowSnackbar(snackbarMessage, null)
//                        }
//                    },
//                    isFavorite = people.isFavorite
//                )
//            }
//        ) { paddingValues ->
////            var imageScale by remember {
////                mutableFloatStateOf(
////                    value = runCatching {
////                        1f - (scrollState.firstVisibleItemScrollOffset.toFloat() / scrollState.layoutInfo.visibleItemsInfo[0].size.height.toFloat()).coerceIn(minimumValue = 0f, maximumValue = 1f)
////                    }.getOrElse { e ->
////                        Log.printStackTrace(e)
////                        1f
////                    }
////                )
////            }
//        }
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageComponent(
    people: People
) {
    var isShowing by remember { mutableStateOf(value = false) }
    var index by remember { mutableIntStateOf(value = 0) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    DynamicAsyncImageLoader(
        source = people.profilePath ?: "",
        contentDescription = "PeopleImage",
        modifier = Modifier
            .width(width = dp100)
            .aspectRatio(ratio = PEOPLE_IMAGE_RATIO)
            .clip(shape = RoundedCornerShape(size = dp10))
            .clickable {
                index = 0
                isShowing = true
            }
    )

    if (isShowing) {
        ModalBottomSheetDialog(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = PEOPLE_IMAGE_RATIO),
            state = modalBottomSheetState,
            scope = scope,
            index = index,
            imageList = people.images ?: emptyList(),
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