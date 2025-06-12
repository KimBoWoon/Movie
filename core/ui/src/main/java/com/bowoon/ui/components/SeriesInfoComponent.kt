package com.bowoon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.bowoon.data.util.POSTER_IMAGE_RATIO
import com.bowoon.model.MovieSeries
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp150
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.sp15
import com.bowoon.ui.utils.sp20

fun LazyListScope.seriesInfoComponent(
    series: MovieSeries
) {
    item {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dp10)
        ) {
            Text(
                modifier = Modifier
                    .semantics { contentDescription = "belongsToCollectionName" }
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = series.name ?: "",
                fontSize = sp20,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dp5)
            ) {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .width(dp150)
                        .aspectRatio(POSTER_IMAGE_RATIO),
                    source = series.posterPath ?: "",
                    contentDescription = series.posterPath
                )
                Text(
                    text = series.overview ?: "",
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp15,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
        }
    }
}