package com.cheeke.surfy.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.ReviewDataModel
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.dp1
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp20
import com.cheeke.surfy.ui.utils.dp5
import com.cheeke.surfy.ui.utils.dp60
import com.cheeke.surfy.ui.utils.sp10
import com.cheeke.surfy.ui.utils.sp15
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ReviewComponent(
    reviews: LazyPagingItems<ReviewDataModel>
) {
    if (reviews.itemCount == 0 && reviews.loadState.refresh !is LoadState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "리뷰가 없습니다.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(space = dp10),
            contentPadding = PaddingValues(all = dp10)
        ) {
            items(count = reviews.itemCount) {
                when (reviews[it]) {
                    is ReviewDataModel.Separator -> {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(height = dp1)
                            .padding(horizontal = dp20)
                            .background(color = MaterialTheme.colorScheme.onSurface))
                    }
                    is ReviewDataModel.Item -> {
                        val review = (reviews[it] as ReviewDataModel.Item).review

                        Column(
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(space = dp5),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DynamicAsyncImageLoader(
                                    source = review.authorDetails?.avatarPath ?: "",
                                    contentDescription = "reviewAuthorAvatarPath",
                                    modifier = Modifier.Companion
                                        .size(size = dp60)
                                        .clip(shape = CircleShape)
                                )
                                Column {
                                    Text(
                                        modifier = Modifier.padding(bottom = dp5),
                                        text = review.author ?: "",
                                        fontSize = sp15,
                                        style = TextStyle(
                                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                                        )
                                    )
                                    val time = if (!review.updatedAt?.trim().isNullOrEmpty()) {
                                        "${Instant.from(DateTimeFormatter.ISO_INSTANT.parse(review.updatedAt)).let { instant ->
                                            LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
                                        }} (수정 됨)"
                                    } else {
                                        Instant.from(DateTimeFormatter.ISO_INSTANT.parse(review.createdAt)).let { instant ->
                                            LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
                                        }
                                    }
                                    Text(
                                        text = time,
                                        fontSize = sp10,
                                        style = TextStyle(
                                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                                        )
                                    )
                                }
                            }
                            Text(text = review.content ?: "")
                        }
                    }
                    else -> {
//                        LocalFirebaseLogHelper.current.sendLog("MovieReviewComponent", "ReviewDataModel not found...")
                        Log.d("ReviewDataModel not found...")
                    }
                }
            }
        }
    }
}