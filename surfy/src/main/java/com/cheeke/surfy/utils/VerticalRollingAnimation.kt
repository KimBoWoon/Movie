package com.cheeke.surfy.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.cheeke.surfy.R
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Tv
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun VerticalRollingAnimation(
    modifier: Modifier = Modifier,
    nextWeekReleaseMovies: List<Media>,
    goToMovie: (Int) -> Unit,
    gotoTv: (Int) -> Unit
) {
    if (nextWeekReleaseMovies.isEmpty()) return

    var index by remember { mutableIntStateOf(value = 0) }

    LaunchedEffect(key1 = nextWeekReleaseMovies) {
        index = 0
        if (nextWeekReleaseMovies.size <= 1) return@LaunchedEffect

        while (isActive) {
            delay(timeMillis = 2_000)
            index = (index + 1) % nextWeekReleaseMovies.size
        }
    }

    val current: Media = nextWeekReleaseMovies[index]
    val title: String = current.title.orEmpty()

    AnimatedContent(
        targetState = title,
        transitionSpec = {
            (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
        },
        label = "NextWeekReleaseRolling"
    ) { animatedTitle ->
        Text(
            modifier = modifier.clickable {
                when (current) {
                    is Movie -> goToMovie(current.id ?: -1)
                    is Tv -> gotoTv(current.id ?: -1)
                }
            },
            text = stringResource(id = R.string.next_week_release_movie, animatedTitle),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}