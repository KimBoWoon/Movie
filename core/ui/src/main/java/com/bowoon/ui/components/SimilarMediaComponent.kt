package com.bowoon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.model.Media
import com.bowoon.movie.core.ui.R
import com.bowoon.ui.dialog.ConfirmDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp100
import com.bowoon.ui.utils.dp200

@Composable
fun SimilarMediaComponent(
    similarMedia: LazyPagingItems<out Media>,
    goToDestination: (Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (similarMedia.loadState.refresh is LoadState.Loading) {
            CircularProgressComponent(modifier = Modifier.align(alignment = Alignment.Center))
        } else if (similarMedia.loadState.refresh is LoadState.Error) {
            val message = (similarMedia.loadState.refresh as? LoadState.Error)?.error?.message
                ?: (similarMedia.loadState.append as? LoadState.Error)?.error?.message
                ?: stringResource(id = com.bowoon.movie.core.network.R.string.something_wrong)

            ConfirmDialog(
                title = stringResource(id = com.bowoon.movie.core.network.R.string.network_failed),
                message = message,
                confirmPair = stringResource(id = com.bowoon.movie.core.ui.R.string.retry_message) to { similarMedia.retry() },
                dismissPair = stringResource(id = com.bowoon.movie.core.ui.R.string.confirm_message) to {}
            )
        }

        MediaList(
            similarMedias = similarMedia,
            goToMovie = goToDestination
        )
    }
}

@Composable
fun BoxScope.MediaList(
    similarMedias: LazyPagingItems<out Media>,
    goToMovie: (Int) -> Unit
) {
    if (similarMedias.itemCount == 0 && similarMedias.loadState.refresh !is LoadState.Loading) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Center),
            text = stringResource(id = R.string.similar_movie_not_found)
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = dp100),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(all = dp10),
            horizontalArrangement = Arrangement.spacedBy(space = dp10),
            verticalArrangement = Arrangement.spacedBy(space = dp10)
        ) {
            items(
                count = similarMedias.itemCount
            ) { index ->
                Box(
                    modifier = Modifier
                        .width(dp200)
                        .wrapContentHeight()
                        .bounceClick { goToMovie(similarMedias[index]?.id ?: -1) }
                ) {
                    DynamicAsyncImageLoader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(POSTER_IMAGE_RATIO)
                            .clip(shape = RoundedCornerShape(size = dp10)),
                        source = similarMedias[index]?.posterPath ?: "",
                        contentDescription = "SimilarMoviePoster"
                    )
                }
            }

            if (similarMedias.loadState.append is LoadState.Loading) {
                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                    CircularProgressComponent(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                    )
                }
            }
            if (similarMedias.loadState.append is LoadState.Error) {
                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                    PagingAppendErrorComponent(retry = { similarMedias.retry() })
                }
            }
        }
    }
}