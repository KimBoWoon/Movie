package com.bowoon.people

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bowoon.common.Log
import com.bowoon.model.TMDBPeopleDetail
import com.bowoon.ui.ConfirmDialog
import com.bowoon.ui.image.DynamicAsyncImageLoader

@Composable
fun PeopleScreen(
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: PeopleVM = hiltViewModel()
) {
    val peopleState by viewModel.people.collectAsStateWithLifecycle()

    PeopleScreen(
        state = peopleState,
        navController = navController,
        onMovieClick = onMovieClick,
        onShowSnackbar = onShowSnackbar,
        restart = viewModel::restart
    )
}

@Composable
fun PeopleScreen(
    state: PeopleState,
    navController: NavController,
    onMovieClick: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    restart: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var people by remember { mutableStateOf<TMDBPeopleDetail?>(null) }

    when (state) {
        is PeopleState.Loading -> {
            Log.d("loading...")
            isLoading = true
        }
        is PeopleState.Success -> {
            Log.d("${state.data}")
            isLoading = false
            people = state.data
        }
        is PeopleState.Error -> {
            Log.e("${state.throwable.message}")
            isLoading = false
            ConfirmDialog(
                title = "통신 실패",
                message = "${state.throwable.message}",
                confirmPair = "재시도" to { restart() },
                dismissPair = "돌아가기" to { navController.navigateUp() }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            val pagerState = rememberPagerState { people?.images?.profiles?.size ?: 0 }

            HorizontalPager(
                modifier = Modifier.fillMaxWidth().aspectRatio(people?.images?.profiles?.maxOfOrNull { it.aspectRatio?.toFloat() ?: 1f } ?: 1f),
                state = pagerState
            ) {index ->
                DynamicAsyncImageLoader(
                    source = people?.images?.profiles?.get(index)?.filePath ?: "",
                    contentDescription = "PeopleImages"
                )
            }

            Text(text = people?.name ?: "")
            Text(text = people?.birthday ?: "")
            Text(text = people?.deathday ?: "")
            Text(text = people?.placeOfBirth ?: "")
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}