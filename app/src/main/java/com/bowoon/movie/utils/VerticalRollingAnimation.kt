package com.bowoon.movie.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bowoon.detail.navigation.DetailRoute
import com.bowoon.model.Movie
import com.bowoon.movie.MovieAppState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun VerticalRollingAnimation(
    modifier: Modifier = Modifier,
    appState: MovieAppState,
    nextWeekReleaseMovies: List<Movie>
) {
    var hideIndex by remember { mutableIntStateOf(value = 0) }
    var hideTitle by remember { mutableStateOf(value = nextWeekReleaseMovies[hideIndex].title ?: "") }
    var showIndex by remember { mutableIntStateOf(value = 1) }
    var showTitle by remember { mutableStateOf(value = nextWeekReleaseMovies[showIndex].title ?: "") }
    val hideAnimation = remember { Animatable(initialValue = 0f) }
    val showAnimation = remember { Animatable(initialValue = 100f) }

    Box {
        Text(
            modifier = modifier
                .offset(y = hideAnimation.value.dp)
                .clickable {
                    nextWeekReleaseMovies[hideIndex].id?.let {
                        appState.navController.navigate(route = DetailRoute(id = it))
                    }
                },
            text = "$hideTitle 곧 개봉합니다!",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = modifier
                .offset(y = showAnimation.value.dp)
                .clickable {
                    nextWeekReleaseMovies[showIndex].id?.let {
                        appState.navController.navigate(route = DetailRoute(id = it))
                    }
                },
            text = "$showTitle 곧 개봉합니다!",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    LaunchedEffect(key1 = Unit) {
        launch {
            while (isActive) {
                hideAnimation.animateTo(
                    targetValue = -100f,
                    animationSpec = tween(durationMillis = 1000)
                )
                delay(timeMillis = 2000)
                hideIndex = if (hideIndex > nextWeekReleaseMovies.size) {
                    0
                } else {
                    ++hideIndex % nextWeekReleaseMovies.size
                }
                hideAnimation.snapTo(targetValue = 0f)
                hideTitle = nextWeekReleaseMovies[hideIndex].title ?: ""
            }
        }
        launch {
            while (isActive) {
                showAnimation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 1000)
                )
                delay(timeMillis = 2000)
                showAnimation.snapTo(targetValue = 100f)
                showIndex = if (showIndex > nextWeekReleaseMovies.size) {
                    0
                } else {
                    ++showIndex % nextWeekReleaseMovies.size
                }
                showTitle = nextWeekReleaseMovies[showIndex].title ?: ""
            }
        }
    }
}