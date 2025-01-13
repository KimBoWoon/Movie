package com.bowoon.ui.collaps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.bowoon.model.PeopleDetail
import com.bowoon.ui.R
import com.bowoon.ui.dp80
import com.bowoon.ui.image.DynamicAsyncImageLoader
import kotlinx.coroutines.launch

private val ContentPadding = 8.dp
private val Elevation = 4.dp
private val ButtonSize = 24.dp
private const val Alpha = 0.75f

private val ExpandedPadding = 1.dp
private val CollapsedPadding = 3.dp

private val ExpandedCostaRicaHeight = 20.dp
private val CollapsedCostaRicaHeight = 16.dp

private val ExpandedWildlifeHeight = 32.dp
private val CollapsedWildlifeHeight = 24.dp

private val MapHeight = CollapsedCostaRicaHeight * 2

/**
 * 출처 : https://github.com/pencelab/CollapsingToolbarInCompose
 */
@Composable
fun CollapsingToolbar(
    modifier: Modifier = Modifier,
    people: PeopleDetail,
    progress: Float,
    insertFavoritePeople: (PeopleDetail) -> Unit,
    deleteFavoritePeople: (PeopleDetail) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val scope = rememberCoroutineScope()
    val logoPadding = with(LocalDensity.current) {
        lerp(CollapsedPadding.toPx(), ExpandedPadding.toPx(), progress).toDp()
    }
    val pagerState = rememberPagerState { people.images?.size ?: 0 }
    var isFavorite by remember { mutableStateOf(people.isFavorite) }

    Surface(
        color = Color.Black,
        shadowElevation = Elevation,
        modifier = modifier
    ) {
        Box (modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .graphicsLayer { alpha = progress },
                state = pagerState
            ) { index ->
                DynamicAsyncImageLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f),
                    source = people.images?.get(index)?.filePath ?: "",
                    contentDescription = "PeopleImages"
                )
            }
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = ContentPadding)
                    .fillMaxSize()
            ) {
                CollapsingToolbarLayout (progress = progress) {
                    //#region Logo Images
                    DynamicAsyncImageLoader(
                        source = people.images?.get(0)?.filePath ?: "",
                        contentDescription = null,
                        modifier = Modifier
                            .padding(logoPadding)
                            .size(dp80)
                            .clip(shape = CircleShape)
                            .graphicsLayer { alpha = ((0.25f - progress) * 4).coerceIn(0f, 1f) },
//                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                    )
                    Row (
                        modifier = Modifier.wrapContentSize(),
                        horizontalArrangement = Arrangement.spacedBy(ContentPadding)
                    ) {
                        IconButton(
                            onClick = {
                                if (isFavorite) {
                                    isFavorite = false
                                    deleteFavoritePeople(people)
                                } else {
                                    isFavorite = true
                                    insertFavoritePeople(people)
                                }
                                scope.launch {
                                    onShowSnackbar(
                                        if (!isFavorite) "좋아하는 인물에서 제거했습니다." else "좋아하는 인물에 추가했습니다.",
                                        null
                                    )
                                }
                            },
                            modifier = Modifier
                                .size(ButtonSize)
                                .background(
                                    color = LocalContentColor.current.copy(alpha = 0.0f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(id = if (isFavorite) R.drawable.ic_like_on else R.drawable.ic_like_off),
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CollapsingToolbarLayout(
    progress: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
//        check(measurables.size == 5) // [0]: Country Map | [1-3]: Logo Images | [4]: Buttons

        val placeables = measurables.map {
            it.measure(constraints)
        }
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {

            val countryMap = placeables[0]
            val buttons = placeables[1]
            countryMap.placeRelative(
                x = 0,
                y = 0
//                y = collapsedHorizontalGuideline - countryMap.height / 2
            )
            buttons.placeRelative(
                x = constraints.maxWidth - buttons.width,
                y = 0
//                y = lerp(
//                    start = (constraints.maxHeight - buttons.height) / 2,
//                    stop = 0,
//                    fraction = progress
//                )
            )
        }
    }
}