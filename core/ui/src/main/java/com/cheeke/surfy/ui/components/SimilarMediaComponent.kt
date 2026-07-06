package com.cheeke.surfy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.cheeke.surfy.core.ui.R
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.bounceClick
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp12
import com.cheeke.surfy.ui.utils.dp120
import com.cheeke.surfy.ui.utils.dp14
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.roundedCornerClickable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun SimilarComponent(
    items: List<SimilarMedia>,
    similarMovies: Flow<PagingData<SimilarMedia>>,
    goToDestination: (Int) -> Unit
) {
    var seeAllState by remember { mutableStateOf(value = false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(space = dp12)
    ) {
        SectionHeader(
            title = stringResource(id = R.string.similar_media),
            actionText = stringResource(id = R.string.see_all),
            onActionClick = { seeAllState = true }
        )

        LazyRow(
            modifier = Modifier
                .semantics { contentDescription = "similarMovies" }
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = dp16),
            horizontalArrangement = Arrangement.spacedBy(space = dp10)
        ) {
            items(
                items = items,
                key = { similarMedia -> similarMedia.id ?: -1 }
            ) { similarMedia ->
                Box(
                    modifier = Modifier.width(width = dp120)
                ) {
                    DynamicAsyncImageLoader(
                        source = similarMedia.posterPath.orEmpty(),
                        contentDescription = similarMedia.posterPath,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                            .roundedCornerClickable(
                                onClick = { goToDestination(similarMedia.id ?: -1) },
                                cornerRadius = dp12
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

    if (seeAllState) {
        SeeAllSimilarMediaBottomSheet(
            similarMedia = similarMovies,
            goToDestination = goToDestination,
            onDismiss = { seeAllState = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeeAllSimilarMediaBottomSheet(
    similarMedia: Flow<PagingData<SimilarMedia>>,
    goToDestination: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
        val items = similarMedia.collectAsLazyPagingItems()

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(count = 3),
            verticalArrangement = Arrangement.spacedBy(space = dp10),
            horizontalArrangement = Arrangement.spacedBy(space = dp10),
            contentPadding = PaddingValues(all = dp10)
        ) {
            if (items.loadState.refresh is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressComponent(modifier = Modifier.wrapContentSize())
                    }
                }
            }

            items(
                count = items.itemCount,
                key = { index -> "${items.peek(index)?.id}-$index" }
            ) { index ->
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                        .bounceClick { goToDestination(items[index]?.id ?: -1) }
                        .clip(shape = RoundedCornerShape(size = dp14)),
                    source = items[index]?.posterPath.orEmpty(),
                    contentDescription = items[index]?.posterPath.orEmpty()
                )
            }
        }
    }
}