package com.bowoon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.bowoon.model.Credits
import com.bowoon.movie.core.ui.R
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp12
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp5
import com.bowoon.ui.utils.dp72

@Composable
fun CreditsComponent(
    credits: Credits,
    goToPeople: (Int) -> Unit
) {
    val castScrollState = rememberLazyListState()
    val crewScrollState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!credits.cast.isNullOrEmpty()) {
            Text(
                text = stringResource(id = R.string.movie_actor),
                modifier = Modifier.padding(horizontal = dp16, vertical = dp5),
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow(
                state = castScrollState,
                contentPadding = PaddingValues(horizontal = dp16),
                horizontalArrangement = Arrangement.spacedBy(space = dp12)
            ) {
                items(
                    items = credits.cast ?: emptyList(),
                    key = { "${it.id}_${it.creditId}_${it.castId}" }
                ) { actor ->
                    Column(
                        modifier = Modifier
                            .width(width = dp72)
                            .bounceClick(onClick = { goToPeople(actor.id ?: -1) }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DynamicAsyncImageLoader(
                            source = actor.profilePath ?: "",
                            contentDescription = null,
                            modifier = Modifier
                                .size(size = dp72)
                                .clip(shape = CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = actor.character ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = actor.name ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        if (!credits.crew.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(height = dp5))

            Text(
                text = stringResource(id = R.string.movie_staff),
                modifier = Modifier.padding(horizontal = dp16, vertical = dp5),
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow(
                state = crewScrollState,
                contentPadding = PaddingValues(horizontal = dp16),
                horizontalArrangement = Arrangement.spacedBy(space = dp12)
            ) {
                items(
                    items = credits.crew ?: emptyList(),
                    key = { "${it.id}_${it.job}" }
                ) { crew ->
                    Column(
                        modifier = Modifier
                            .width(width = dp72)
                            .bounceClick(onClick = { goToPeople(crew.id ?: -1) }),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DynamicAsyncImageLoader(
                            source = crew.profilePath ?: "",
                            contentDescription = null,
                            modifier = Modifier
                                .size(size = dp72)
                                .clip(shape = CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = crew.department ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = crew.name ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = crew.job ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}