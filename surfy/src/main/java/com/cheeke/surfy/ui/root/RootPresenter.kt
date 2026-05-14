package com.cheeke.surfy.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.deeplink.DeepLinkManager
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.navigation.LocalAppNavigator
import com.cheeke.surfy.navigation.RootScreen
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.flow.StateFlow

class RootPresenter @AssistedInject constructor(
    private val databaseRepository: DatabaseRepository,
    private val deepLinkManager: DeepLinkManager
) : Presenter<RootState> {
    @CircuitInject(screen = RootScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create() : RootPresenter
    }

    @Composable
    override fun present(): RootState {
        val navigator = LocalAppNavigator.current
        val nextWeekReleaseMedia by produceRetainedState(initialValue = emptyList()) {
            Log.d("nextReleaseMedias")
            value = databaseRepository.getNextWeekReleaseMovies() + databaseRepository.getNextWeekReleaseTvs()
        }
        val bottomDeeplink = deepLinkManager.bottomDeeplink

        return RootState(
            nextWeekReleaseMedia = nextWeekReleaseMedia,
            bottomDeeplink = bottomDeeplink,
            consumeBottomDeepLink = ::consumeBottomDeepLink,
            openSearch = navigator::goToSearch,
            goToMovie = navigator::goToMovie,
            goToPeople = navigator::goToPeople,
            goToTv = navigator::goToTv
        )
    }

    fun consumeBottomDeepLink() {
        deepLinkManager.consumeBottomDeepLink()
    }
}

data class RootState(
    val nextWeekReleaseMedia: List<Media>,
    val bottomDeeplink: StateFlow<List<Screen>>,
    val consumeBottomDeepLink: () -> Unit,
    val openSearch: () -> Unit,
    val goToMovie: (Int) -> Unit,
    val goToPeople: (Int) -> Unit,
    val goToTv: (Int) -> Unit
) : CircuitUiState

enum class RootTab {
    HOME, FAVORITE
}