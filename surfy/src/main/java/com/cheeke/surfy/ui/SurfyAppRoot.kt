package com.cheeke.surfy.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.cheeke.surfy.detail.movie.navigation.goToMovie
import com.cheeke.surfy.detail.movie.navigation.movieEntry
import com.cheeke.surfy.detail.people.navigation.goToPeople
import com.cheeke.surfy.detail.people.navigation.peopleEntry
import com.cheeke.surfy.detail.series.navigation.goToSeries
import com.cheeke.surfy.detail.series.navigation.seriesEntry
import com.cheeke.surfy.detail.tv.navigation.goToTv
import com.cheeke.surfy.detail.tv.navigation.tvEntry
import com.cheeke.surfy.search.navigation.goToSearch
import com.cheeke.surfy.search.navigation.searchEntry
import com.cheeke.surfy.ui.root.navigation.rootEntry

@Composable
fun SurfyApp(
    backstack: NavBackStack<NavKey>,
    snackbarHostState: SnackbarHostState,
    showSettingDialog: () -> Unit
) {
    val entryProvider = entryProvider {
        rootEntry(
            goToMovie = backstack::goToMovie,
            goToPeople = backstack::goToPeople,
            goToTv = backstack::goToTv,
            goToSearch = backstack::goToSearch,
            showSettingDialog = showSettingDialog
        )
        movieEntry(
            goToBack = backstack::goToBack,
            goToMovie = backstack::goToMovie,
            goToPeople = backstack::goToPeople,
            goToSeries = backstack::goToSeries,
            onShowSnackbar = { message, action ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = SnackbarDuration.Short,
                ) == SnackbarResult.ActionPerformed
            }
        )
        peopleEntry(
            goToBack = backstack::goToBack,
            goToMovie = backstack::goToMovie,
            goToTv = backstack::goToTv,
            onShowSnackbar = { message, action ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = SnackbarDuration.Short,
                ) == SnackbarResult.ActionPerformed
            }
        )
        seriesEntry(
            goToBack = backstack::goToBack,
            goToMovie = backstack::goToMovie,
        )
        tvEntry(
            goToBack = backstack::goToBack,
            goToTv = backstack::goToTv,
            goToPeople = backstack::goToPeople,
            onShowSnackbar = { message, action ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = SnackbarDuration.Short,
                ) == SnackbarResult.ActionPerformed
            }
        )
        searchEntry(
            goToMovie = backstack::goToMovie,
            goToTv = backstack::goToTv,
            goToPeople = backstack::goToPeople,
            goToSeries = backstack::goToSeries,
            onShowSnackbar = { message, action ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = SnackbarDuration.Short,
                ) == SnackbarResult.ActionPerformed
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                backStack = backstack,
                onBack = backstack::goToBack,
                entryProvider = entryProvider,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator<NavKey>(), // 백 스택의 항목 상태를 관리하는 객체
                    rememberViewModelStoreNavEntryDecorator() // 각 컴포저블 화면마다 독립적인 뷰모델을 사용하는 객체
                )
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

fun NavBackStack<NavKey>.goToBack() = removeLastOrNull()