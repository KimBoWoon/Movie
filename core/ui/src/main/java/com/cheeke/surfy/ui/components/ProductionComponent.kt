package com.cheeke.surfy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.cheeke.surfy.core.ui.R
import com.cheeke.surfy.model.ProductionCompany
import com.cheeke.surfy.ui.image.DynamicAsyncImageLoader
import com.cheeke.surfy.ui.utils.dp10
import com.cheeke.surfy.ui.utils.dp12
import com.cheeke.surfy.ui.utils.dp120
import com.cheeke.surfy.ui.utils.dp14
import com.cheeke.surfy.ui.utils.dp16
import com.cheeke.surfy.ui.utils.dp64

@Composable
fun ProductionComponent(companies: List<ProductionCompany>) {
    if (companies.isNotEmpty()) {
        Column {
            SectionHeader(title = stringResource(id = R.string.production))
            Spacer(modifier = Modifier.height(height = dp12))

            LazyRow(contentPadding = PaddingValues(horizontal = dp16)) {
                items(items = companies) { company ->
                    Column(
                        modifier = Modifier
                            .width(width = dp120)
                            .padding(end = dp12),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        DynamicAsyncImageLoader(
                            source = company.logoPath.orEmpty(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = dp64)
                                .clip(shape = RoundedCornerShape(size = dp14))
                                .background(color = Color.DarkGray.copy(alpha = 0.35f))
                                .padding(all = dp10),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = company.name.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}