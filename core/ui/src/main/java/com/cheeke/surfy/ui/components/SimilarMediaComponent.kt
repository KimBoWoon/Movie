package com.cheeke.surfy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import com.cheeke.surfy.core.ui.R
import com.cheeke.surfy.data.util.POSTER_IMAGE_RATIO
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp12
import com.cheeke.surfy.ui.utils.dp120
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.roundedCornerClickable

@Composable
fun SimilarComponent(
    similar: LazyPagingItems<SimilarMedia>,
    goToDestination: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = dp12)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = dp16),
            text = stringResource(id = R.string.similar_media),
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = dp16),
            horizontalArrangement = Arrangement.spacedBy(space = dp10)
        ) {
            items(
                count = similar.itemCount,
                key = { index -> similar.peek(index)?.id ?: -1 }
            ) { index ->
                Box(
                    modifier = Modifier.width(width = dp120)
                ) {
                    DynamicAsyncImageLoader(
                        source = similar[index]?.posterPath ?: "",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(ratio = POSTER_IMAGE_RATIO)
                            .roundedCornerClickable(
                                onClick = { goToDestination(similar[index]?.id ?: -1) },
                                cornerRadius = dp12
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}