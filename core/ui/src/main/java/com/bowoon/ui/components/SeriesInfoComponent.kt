package com.bowoon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.bowoon.model.Series
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.sp15
import com.bowoon.ui.utils.sp20

fun LazyListScope.seriesInfoComponent(
    series: Series
) {
    item {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(space = dp10)
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
            series.overview?.trim().takeIf { !it.isNullOrEmpty() }?.let {
                Text(
                    modifier = Modifier.semantics { contentDescription = "seriesOverview" },
                    text = series.overview ?: "",
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp15,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
        }
    }
}