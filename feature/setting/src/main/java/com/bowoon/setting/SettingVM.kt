package com.bowoon.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Log
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.DataManager
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.LocaleOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingVM @Inject constructor(
    private val userDataRepository: UserDataRepository,
    dataManager: DataManager
) : ViewModel() {
    companion object {
        private const val TAG = "SettingVM"
    }

    private val _uiState = MutableStateFlow(value = SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = combine(
        flow = userDataRepository.internalData,
        flow2 = dataManager.movieAppData,
        flow3 = _uiState
    ) { internalData, movieAppDataState, settingsUiState ->
        val movieAppData = movieAppDataState.getMovieAppData()
        val selectedLanguage = movieAppData.language.find { it.isSelected }
        val selectedRegion = movieAppData.region.find { it.isSelected }

        SettingsUiState(
            sheet = settingsUiState.sheet,
            mainUpdateDate = internalData.updateDate,
            theme = internalData.isDarkMode,
            isAdult = settingsUiState.isAdult,
            isTrailerAutoplay = settingsUiState.isTrailerAutoplay,
            language = selectedLanguage,
            region = selectedRegion,
            selectedLanguage = settingsUiState.selectedLanguage ?: selectedLanguage,
            selectedRegion = settingsUiState.selectedRegion ?: selectedRegion,
            imageQuality = internalData.imageQuality,
            imageQualityList = movieAppData.posterSize.map { it.size.orEmpty() },
            allLanguages = movieAppData.language,
            allRegions = movieAppData.region,
            selectedTheme = settingsUiState.selectedTheme,
            themeList = DarkThemeConfig.entries,
            selectedImageQuality = settingsUiState.selectedImageQuality
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SettingsUiState()
    )

    fun onAction(action: SettingsAction) {
        Log.d("onAction", "$action")
        when (action) {
            SettingsAction.OpenMain -> _uiState.update { it.copy(sheet = SettingsSheet.Main) }
            SettingsAction.CloseSheet -> _uiState.update { it.copy(sheet = SettingsSheet.Hidden) }
            is SettingsAction.SetAdult -> {
                _uiState.update { it.copy(isAdult = action.enabled) }
                viewModelScope.launch {
                    userDataRepository.updateIsAdult(value = action.enabled)
                }
            }
            is SettingsAction.SetTrailerAutoplay -> {
                _uiState.update { it.copy(isTrailerAutoplay = action.enabled) }
                viewModelScope.launch {
                    userDataRepository.updateIsAutoPlayTrailer(value = action.enabled)
                }
            }
            SettingsAction.OpenLanguageRegion -> {
                _uiState.update {
                    it.copy(
                        sheet = SettingsSheet.LanguageRegion,
                        language = it.language,
                        region = it.region,
                    )
                }
            }
            SettingsAction.OpenImageQuality -> {
                _uiState.update {
                    it.copy(
                        sheet = SettingsSheet.ImageQuality,
                        imageQuality = it.imageQuality,
                        imageQualityList = it.imageQualityList
                    )
                }
            }
            is SettingsAction.PickLanguage -> _uiState.update { it.copy(selectedLanguage = action.option) }
            is SettingsAction.PickRegion -> _uiState.update { it.copy(selectedRegion = action.option) }
            SettingsAction.ConfirmLanguageRegion -> {
                _uiState.update {
                    it.copy(
                        language = _uiState.value.selectedLanguage,
                        region = _uiState.value.selectedRegion,
                        sheet = SettingsSheet.Main
                    )
                }
                viewModelScope.launch {
                    _uiState.value.selectedLanguage?.let {
                        if (it.code != _uiState.value.language?.code) {
                            userDataRepository.updateLanguage(value = it.code)
                        }
                    }
                    _uiState.value.selectedRegion?.let {
                        if (it.code != _uiState.value.region?.code) {
                            userDataRepository.updateRegion(value = it.code)
                        }
                    }
                }
            }
            SettingsAction.BackToMainFromLanguageRegion -> {
                _uiState.update {
                    it.copy(
                        sheet = SettingsSheet.Main,
                        language = it.language,
                        region = it.region,
                        selectedLanguage = null,
                        selectedRegion = null
                    )
                }
            }
            is SettingsAction.PickImageQuality -> _uiState.update { it.copy(selectedImageQuality = action.option) }
            SettingsAction.ConfirmImageQuality -> {
                _uiState.update {
                    it.copy(
                        imageQuality = _uiState.value.selectedImageQuality ?: "original",
                        sheet = SettingsSheet.Main
                    )
                }
                viewModelScope.launch {
                    _uiState.value.selectedImageQuality?.let {
                        userDataRepository.updateImageQuality(value = it)
                    }
                }
            }
            SettingsAction.BackToMainFromImageQuality -> {
                _uiState.update {
                    it.copy(
                        sheet = SettingsSheet.Main,
                        imageQuality = it.imageQuality,
                        selectedImageQuality = null
                    )
                }
            }
            SettingsAction.BackToMainFromThemeSetting -> {
                _uiState.update {
                    it.copy(
                        sheet = SettingsSheet.Main,
                        theme = it.theme,
                        selectedTheme = null
                    )
                }
            }
            SettingsAction.ConfirmTheme -> {
                _uiState.update {
                    it.copy(
                        theme = _uiState.value.selectedTheme ?: DarkThemeConfig.FOLLOW_SYSTEM,
                        sheet = SettingsSheet.Main
                    )
                }
                viewModelScope.launch {
                    _uiState.value.selectedTheme?.let {
                        userDataRepository.updateDarkMode(darkThemeConfig = it)
                    }
                }
            }
            SettingsAction.OpenThemeSetting -> {
                _uiState.update {
                    it.copy(
                        sheet = SettingsSheet.ThemeSetting,
                        theme = it.theme,
                        themeList = DarkThemeConfig.entries
                    )
                }
            }
            is SettingsAction.PickTheme -> _uiState.update { it.copy(selectedTheme = action.option) }
        }
    }
}

sealed interface SettingsSheet {
    data object Hidden : SettingsSheet
    data object Main : SettingsSheet
    data object ThemeSetting : SettingsSheet
    data object LanguageRegion : SettingsSheet
    data object ImageQuality : SettingsSheet
}

data class SettingsUiState(
    val sheet: SettingsSheet = SettingsSheet.Hidden,
    val mainUpdateDate: String = "",
    val theme: DarkThemeConfig = DarkThemeConfig.DARK,
    val selectedTheme: DarkThemeConfig? = null,
    val themeList: List<DarkThemeConfig> = emptyList(),
    val isAdult: Boolean = true,
    val isTrailerAutoplay: Boolean = false,
    val language: LocaleOption? = null,
    val region: LocaleOption? = null,
    val selectedLanguage: LocaleOption? = null,
    val selectedRegion: LocaleOption? = null,
    val imageQuality: String = "original",
    val imageQualityList: List<String> = emptyList(),
    val selectedImageQuality: String? = null,
    val allLanguages: List<LocaleOption> = emptyList(),
    val allRegions: List<LocaleOption> = emptyList()
)

sealed interface SettingsAction {
    data object OpenMain : SettingsAction
    data object CloseSheet : SettingsAction

    // Main toggles
    data class SetAdult(val enabled: Boolean) : SettingsAction
    data class SetTrailerAutoplay(val enabled: Boolean) : SettingsAction

    // Navigate to sub sheets
    data object OpenLanguageRegion : SettingsAction
    data object OpenImageQuality : SettingsAction
    data object OpenThemeSetting : SettingsAction

    // Language/Region sub sheet events
    data class PickLanguage(val option: LocaleOption) : SettingsAction
    data class PickRegion(val option: LocaleOption) : SettingsAction
    data object ConfirmLanguageRegion : SettingsAction
    data object BackToMainFromLanguageRegion : SettingsAction

    // ImageQuality sub sheet events
    data class PickImageQuality(val option: String) : SettingsAction
    data object ConfirmImageQuality : SettingsAction
    data object BackToMainFromImageQuality : SettingsAction

    // Theme sub sheet events
    data object BackToMainFromThemeSetting : SettingsAction
    data class PickTheme(val option: DarkThemeConfig) : SettingsAction
    data object ConfirmTheme : SettingsAction
}