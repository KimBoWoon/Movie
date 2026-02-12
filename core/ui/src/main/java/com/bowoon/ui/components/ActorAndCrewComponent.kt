package com.bowoon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.bowoon.data.util.PEOPLE_IMAGE_RATIO
import com.bowoon.model.Cast
import com.bowoon.model.CreditInfo
import com.bowoon.model.Credits
import com.bowoon.model.Crew
import com.bowoon.movie.core.ui.R
import com.bowoon.ui.image.DynamicAsyncImageLoader
import com.bowoon.ui.utils.bounceClick
import com.bowoon.ui.utils.dp10
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp200
import com.bowoon.ui.utils.sp12
import com.bowoon.ui.utils.sp15
import com.bowoon.ui.utils.sp20

@Composable
fun ActorAndCrewComponent(
    credits: Credits?,
    goToPeople: (Int) -> Unit
) {
    if (credits?.crew.isNullOrEmpty() && credits?.cast.isNullOrEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = R.string.movie_crew_not_found)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .testTag(tag = "castAndCrew")
                .fillMaxSize()
        ) {
            item {
                credits.cast.takeIf { !it.isNullOrEmpty() }?.let { casts ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dp16),
                        text = stringResource(id = R.string.movie_actor),
                        fontSize = sp20,
                        textAlign = TextAlign.Center
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentPadding = PaddingValues(horizontal = dp16),
                        horizontalArrangement = Arrangement.spacedBy(space = dp10)
                    ) {
                        items(
                            items = casts
                        ) { cast ->
                            CreditInfoComponent(creditInfo = cast, goToPeople = goToPeople)
                        }
                    }
                }
                credits.crew.takeIf { !it.isNullOrEmpty() }?.let { crews ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dp16),
                        text = stringResource(id = R.string.movie_staff),
                        fontSize = sp20,
                        textAlign = TextAlign.Center
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentPadding = PaddingValues(horizontal = dp16),
                        horizontalArrangement = Arrangement.spacedBy(space = dp10)
                    ) {
                        items(
                            items = crews
                        ) { crew ->
                            CreditInfoComponent(creditInfo = crew, goToPeople = goToPeople)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreditInfoComponent(
    creditInfo: CreditInfo,
    goToPeople: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(width = dp200)
            .wrapContentHeight()
            .bounceClick { goToPeople(creditInfo.id ?: -1) }
    ) {
        when (creditInfo) {
            is Cast -> {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = PEOPLE_IMAGE_RATIO)
                        .clip(shape = RoundedCornerShape(size = dp10)),
                    source = creditInfo.profilePath ?: "",
                    contentDescription = "ProfileImage"
                )
                Text(
                    text = creditInfo.character ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp12,
                )
                Text(
                    text = creditInfo.name ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp15
                )
                Text(
                    text = creditInfo.originalName ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp15
                )
            }
            is Crew -> {
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = PEOPLE_IMAGE_RATIO)
                        .clip(shape = RoundedCornerShape(size = dp10)),
                    source = creditInfo.profilePath ?: "",
                    contentDescription = "ProfileImage"
                )
                Text(
                    text = creditInfo.department ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp12
                )
                Text(
                    text = creditInfo.name ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp15
                )
                Text(
                    text = creditInfo.originalName ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp15
                )
                Text(
                    text = creditInfo.job ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = sp12
                )
            }
        }
    }
}