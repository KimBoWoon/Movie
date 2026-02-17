package com.bowoon.my

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingVM @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val dataManager: DataManager
) : ViewModel() {
    companion object {
        private const val TAG = "SettingVM"
    }

    private val _uiState = MutableStateFlow(value = SettingsUiState())
    val uiState = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SettingsUiState()
    )
    private val getSetting = combine(
        userDataRepository.internalData,
        dataManager.movieAppData
    ) { internalData, movieAppDataState ->
        val movieAppData = movieAppDataState.getMovieAppData()

        SettingsUiState(
            sheet = SettingsSheet.Hidden,
            mainUpdateDate = internalData.updateDate,
            versionName = "",
            darkMode = movieAppData.isDarkMode,
            adultEnabled = internalData.isAdult,
            trailerAutoplay = internalData.isAutoPlayTrailer,
            language = movieAppData.language.find { it.isSelected }?.let { LocaleOption(code = it.iso6391 ?: "", label = it.englishName ?: "") } ?: LocaleOption(),
            region = movieAppData.region.find { it.isSelected }?.let { LocaleOption(code = it.iso31661 ?: "", label = it.nativeName ?: "") } ?: LocaleOption(),
//            selectedLanguage = movieAppData.language.find { it.isSelected }?.let { LocaleOption(code = it.iso6391 ?: "", label = it.englishName ?: "") },
//            selectedRegion = movieAppData.region.find { it.isSelected }?.let { LocaleOption(code = it.iso31661 ?: "", label = it.englishName ?: "") },
            imageQuality = movieAppData.imageQuality,
            imageQualityList = movieAppData.posterSize.map { it.size ?: "" },
//            selectedImageQuality = movieAppData.posterSize.find { it.isSelected }?.size,
            allLanguages = movieAppData.language.map { LocaleOption(code = it.iso6391 ?: "", label = it.englishName ?: "") },
            allRegions = movieAppData.region.map { LocaleOption(code = it.iso31661 ?: "", label = it.nativeName ?: "") }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SettingsUiState()
    )

    init {
        viewModelScope.launch {
            getSetting.collect {
                _uiState.emit(value = it)
            }
        }
    }

    fun onAction(action: SettingsAction) {
        Log.d("onAction", "$action")
        when (action) {
            SettingsAction.OpenMain -> _uiState.update { it.copy(sheet = SettingsSheet.Main) }
            SettingsAction.CloseSheet -> _uiState.update { it.copy(sheet = SettingsSheet.Hidden) }
            is SettingsAction.SetAdult -> {
                _uiState.update { it.copy(adultEnabled = action.enabled) }
                viewModelScope.launch {
                    userDataRepository.updateIsAdult(value = action.enabled)
                }
            }
            is SettingsAction.SetTrailerAutoplay -> {
                _uiState.update { it.copy(trailerAutoplay = action.enabled) }
                viewModelScope.launch {
                    userDataRepository.updateIsAutoPlayTrailer(value = action.enabled)
                }
            }
            is SettingsAction.SetDarkMode -> {
                _uiState.update { it.copy(darkMode = action.mode) }
                viewModelScope.launch {
                    userDataRepository.updateDarkMode(darkThemeConfig = action.mode)
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
                        userDataRepository.updateLanguage(value = it.code)
                    }
                    _uiState.value.selectedRegion?.let {
                        userDataRepository.updateRegion(value = it.code)
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
        }
    }
}

sealed interface SettingsSheet {
    data object Hidden : SettingsSheet
    data object Main : SettingsSheet
    data object LanguageRegion : SettingsSheet
    data object ImageQuality : SettingsSheet
}

data class SettingsUiState(
    val sheet: SettingsSheet = SettingsSheet.Hidden,
    val mainUpdateDate: String? = null,
    val versionName: String? = null,
    val darkMode: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val adultEnabled: Boolean = true,
    val trailerAutoplay: Boolean = false,
    val language: LocaleOption? = null,
    val region: LocaleOption? = null,
    val selectedLanguage: LocaleOption? = null,
    val selectedRegion: LocaleOption? = null,
    val imageQuality: String = "original",
    val imageQualityList: List<String> = emptyList(),
    val selectedImageQuality: String? = null,
    // TMDB에서 가져온 전체 목록 (여기선 샘플)
    val allLanguages: List<LocaleOption> = emptyList(),
    val allRegions: List<LocaleOption> = emptyList()
)

sealed interface SettingsAction {
    data object OpenMain : SettingsAction
    data object CloseSheet : SettingsAction

    // Main toggles
    data class SetAdult(val enabled: Boolean) : SettingsAction
    data class SetTrailerAutoplay(val enabled: Boolean) : SettingsAction
    data class SetDarkMode(val mode: DarkThemeConfig) : SettingsAction

    // Navigate to sub sheets
    data object OpenLanguageRegion : SettingsAction
    data object OpenImageQuality : SettingsAction

    // Language/Region sub sheet events
    data class PickLanguage(val option: LocaleOption) : SettingsAction
    data class PickRegion(val option: LocaleOption) : SettingsAction
    data object ConfirmLanguageRegion : SettingsAction
    data object BackToMainFromLanguageRegion : SettingsAction

    // ImageQuality sub sheet events
    data class PickImageQuality(val option: String) : SettingsAction
    data object ConfirmImageQuality : SettingsAction
    data object BackToMainFromImageQuality : SettingsAction
}