package com.cheeke.surfy.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.repository.MovieDataBaseRepository
import com.cheeke.surfy.data.repository.TvDataBaseRepository
import com.cheeke.surfy.deeplink.DeepLinkManager
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.navigation.LocalRootNavigator
import com.cheeke.surfy.navigation.RootScreen
import com.cheeke.surfy.navigation.goToMovie
import com.cheeke.surfy.navigation.goToPeople
import com.cheeke.surfy.navigation.goToSearch
import com.cheeke.surfy.navigation.goToTv
import com.cheeke.surfy.ui.setting.SettingRepository
import com.cheeke.surfy.ui.setting.SettingsAction
import com.cheeke.surfy.ui.setting.SettingsUiState
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.flow.Flow

class RootPresenter @AssistedInject constructor(
    private val movieDataBaseRepository: MovieDataBaseRepository,
    private val tvDataBaseRepository: TvDataBaseRepository,
    private val settingRepository: SettingRepository,
    private val deepLinkManager: DeepLinkManager
) : Presenter<RootState> {
    @CircuitInject(screen = RootScreen::class, scope = ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create() : RootPresenter
    }

    @Composable
    override fun present(): RootState {
        val rootNavigator = LocalRootNavigator.current
        val nextWeekReleaseMedia by produceRetainedState(initialValue = emptyList()) {
            Log.d("nextReleaseMedias")
            value = movieDataBaseRepository.getNextWeekReleaseMovies() + tvDataBaseRepository.getNextWeekReleaseTvs()
        }
        val bottomDeeplink = deepLinkManager.bottomDeeplink
        val settingsUiState by settingRepository.uiState.collectAsStateWithLifecycle()

        return RootState(
            rootUiState = RootUiState(
                nextWeekReleaseMedia = nextWeekReleaseMedia,
                bottomDeeplink = bottomDeeplink,
                settingsUiState = settingsUiState
            ),
            eventSink = { event ->
                when (event) {
                    is RootEvent.GoToBack -> rootNavigator.pop()
                    is RootEvent.GoToMovie -> rootNavigator.goToMovie(id = event.id)
                    is RootEvent.GoToPeople -> rootNavigator.goToPeople(id = event.id)
                    is RootEvent.GoToSetting -> { settingRepository.onAction(action = SettingsAction.OpenMain) }
                    is RootEvent.GoToTv -> rootNavigator.goToTv(id = event.id)
                    is RootEvent.OnSettingTitleClick -> settingRepository.onClickTitle()
                    is RootEvent.OnSettingsAction -> settingRepository.onAction(action = event.action)
                    is RootEvent.OpenSearch -> rootNavigator.goToSearch(query = event.query, searchType = event.searchType)
                    is RootEvent.ConsumeBottomDeepLink -> deepLinkManager.consumeBottomDeepLink()
                }
            }
        )
    }
}

data class RootUiState(
    val nextWeekReleaseMedia: List<Media>,
    val bottomDeeplink: Flow<List<Screen>>,
    val settingsUiState: SettingsUiState
)

sealed interface RootEvent : CircuitUiEvent {
    object GoToBack : RootEvent
    data class GoToMovie(val id: Int) : RootEvent
    data class GoToPeople(val id: Int) : RootEvent
    data class GoToTv(val id: Int) : RootEvent
    object GoToSetting : RootEvent
    data class OpenSearch(val query: String, val searchType: SearchType) : RootEvent
    data class OnSettingsAction(val action: SettingsAction) : RootEvent
    object OnSettingTitleClick : RootEvent
    object ConsumeBottomDeepLink : RootEvent
}

data class RootState(
    val rootUiState: RootUiState,
    val eventSink: (RootEvent) -> Unit
) : CircuitUiState

enum class RootTab {
    HOME, FAVORITE
}