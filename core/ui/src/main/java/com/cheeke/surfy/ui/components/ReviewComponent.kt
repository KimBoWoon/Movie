package com.cheeke.surfy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.cheeke.surfy.common.toRelativeTime
import com.cheeke.surfy.core.ui.R
import com.cheeke.surfy.model.Review
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.bounceClick
import com.cheeke.surfy.ui.utils.dp1
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp20
import com.cheeke.surfy.ui.utils.dp200
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.dp60
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun ReviewComponent(
    items: List<Review>,
    reviews: Flow<PagingData<Review>>
) {
    var seeAllState by remember { mutableStateOf(value = false) }
    var showDetail by remember { mutableStateOf<Review?>(value = null) }

    Column(
        modifier = Modifier.fillMaxWidth().height(height = dp200)
    ) {
        SectionHeader(
            title = "리뷰",
            actionText = stringResource(id = R.string.see_all),
            onActionClick = { seeAllState = true }
        )

        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = rememberPagerState(pageCount = { items.size }),
            contentPadding = PaddingValues(all = dp10),
            pageSpacing = dp10
        ) { index ->
            val review = items[index]

            ReviewItem(
                review = review,
                showDetail = { showDetail = review }
            )
        }
    }

    if (seeAllState) {
        SeeAllReviewBottomSheet(
            items = reviews,
            showDetail = null,
            onDismiss = { seeAllState = false }
        )
    }

    showDetail?.let {
        ReviewDetail(
            review = it,
            onDismiss = { showDetail = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeeAllReviewBottomSheet(
    items: Flow<PagingData<Review>>,
    showDetail: (() -> Unit)?,
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
        val items = items.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(space = dp10),
            contentPadding = PaddingValues(all = dp10)
        ) {
            items(
                count = items.itemCount,
                key = { index -> "${items.peek(index)?.id}-$index" }
            ) { index ->
                items[index]?.let {
                    ReviewItem(
                        review = it,
                        showDetail = showDetail
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: Review,
    showDetail: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .then(
                other = if (showDetail == null) {
                    Modifier
                } else {
                    Modifier.bounceClick(onClick = showDetail)
                }
            ),
        shape = RoundedCornerShape(size = dp20),
        elevation = CardDefaults.cardElevation(defaultElevation = dp1),
        border = BorderStroke(width = dp1, color = MaterialTheme.colorScheme.onBackground),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = dp5),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DynamicAsyncImageLoader(
                source = review.authorDetails?.avatarPath.orEmpty(),
                contentDescription = "reviewAuthorAvatarPath",
                modifier = Modifier
                    .padding(start = dp10, top = dp10)
                    .size(size = dp60)
                    .clip(shape = CircleShape)
            )
            Column {
                Text(
                    modifier = Modifier.padding(bottom = dp5),
                    text = review.author.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = (review.updatedAt ?: review.createdAt)?.toRelativeTime().orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.padding(vertical = dp5, horizontal = dp16).fillMaxWidth().height(height = dp1).background(color = MaterialTheme.colorScheme.onBackground))

        Text(
            modifier = Modifier.padding(start = dp10, end = dp10, bottom = dp10),
            text = review.content.orEmpty(),
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ReviewDetail(
    review: Review,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier.padding(all = dp10).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ReviewItem(
                review = review,
                showDetail = null
            )
        }
    }
}