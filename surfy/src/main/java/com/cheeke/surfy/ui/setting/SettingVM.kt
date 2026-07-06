package com.cheeke.surfy.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.data.util.DataManager
import com.cheeke.surfy.model.DarkThemeConfig
import com.cheeke.surfy.model.LocaleOption
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.processors.BehaviorProcessor
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

    private val disposable = CompositeDisposable()
    private val _uiState = BehaviorProcessor.createDefault(SettingsUiState())
    val uiState = Flowable.combineLatest(
        userDataRepository.internalData,
        dataManager.surfyAppData,
        _uiState
    ) { internalData, movieAppDataState, settingsUiState ->
        val movieAppData = movieAppDataState.getMovieAppData()
        val selectedLanguage = movieAppData.language.find { it.isSelected }
        val selectedRegion = movieAppData.region.find { it.isSelected }

        SettingsUiState(
            sheet = settingsUiState.sheet,
            mainUpdateDate = internalData.updateDate,
            theme = internalData.isDarkMode,
            isAdult = internalData.isAdult,
            isTrailerAutoplay = settingsUiState.isTrailerAutoplay,
            language = selectedLanguage,
            region = selectedRegion,
            selectedLanguage = settingsUiState.selectedLanguage ?: selectedLanguage,
            selectedRegion = settingsUiState.selectedRegion ?: selectedRegion,
            imageQuality = internalData.imageQuality,
            imageQualityList = movieAppData.posterSize.map { it.size.orEmpty() },
            allLanguages = movieAppData.language.sortedBy { it.label },
            allRegions = movieAppData.region.sortedBy { it.label },
            selectedTheme = settingsUiState.selectedTheme,
            themeList = DarkThemeConfig.entries,
            selectedImageQuality = settingsUiState.selectedImageQuality,
            isCheatActive = settingsUiState.isCheatActive ?: internalData.isCheatActive
        )
    }
    var titleClickCount = 0
    val _isCheatActive = BehaviorProcessor.createDefault(false)
    val isCheatActive = _isCheatActive.hide()

    init {
        userDataRepository.internalData
            .subscribe(
                { internalData ->
                    _uiState.value?.let {
                        _uiState.onNext(it.copy(isCheatActive = internalData.isCheatActive))
                    }
                    _isCheatActive.onNext(internalData.isCheatActive)
                },
                { Log.d(it.message.toString()) }
            ).addTo(disposable)
    }

    fun onAction(action: SettingsAction) {
        Log.d("onAction", "$action")
        when (action) {
            SettingsAction.OpenMain -> {
                _uiState.value?.let {
                    _uiState.onNext(it.copy(sheet = SettingsSheet.Main))
                }
            }
            SettingsAction.CloseSheet -> {
                _uiState.value?.let {
                    _uiState.onNext(it.copy(sheet = SettingsSheet.Hidden))
                }
            }
            is SettingsAction.SetAdult -> userDataRepository.updateIsAdult(value = action.enabled)
            is SettingsAction.SetTrailerAutoplay -> {
                userDataRepository.updateIsAutoPlayTrailer(value = action.enabled)
            }
            SettingsAction.OpenLanguageRegion -> {
                _uiState.value?.let {
                    _uiState.onNext(it.copy(sheet = SettingsSheet.LanguageRegion))
                }
            }
            SettingsAction.OpenImageQuality -> {
                _uiState.value?.let {
                    _uiState.onNext(it.copy(sheet = SettingsSheet.ImageQuality))
                }
            }
            is SettingsAction.PickLanguage -> {
                _uiState.value?.let {
                    _uiState.onNext(it.copy(selectedLanguage = action.option))
                }
            }
            is SettingsAction.PickRegion -> {
                _uiState.value?.let {
                    _uiState.onNext(it.copy(selectedRegion = action.option))
                }
            }
            SettingsAction.ConfirmLanguageRegion -> {
                _uiState.value?.selectedLanguage?.let {
                    if (it.code != _uiState.value?.language?.code) {
                        userDataRepository.updateLanguage(value = it.code)
                    }
                }
                _uiState.value?.selectedRegion?.let {
                    if (it.code != _uiState.value?.region?.code) {
                        userDataRepository.updateRegion(value = it.code)
                    }
                }
                _uiState.value?.let {
                    _uiState.onNext(it.copy(sheet = SettingsSheet.Main))
                }
            }
            SettingsAction.BackToMainFromLanguageRegion -> {
                _uiState.value?.let {
                    _uiState.onNext(
                        it.copy(
                            sheet = SettingsSheet.Main,
                            language = it.language,
                            region = it.region,
                            selectedRegion = null,
                            selectedLanguage = null,
                        )
                    )
                }
            }
            is SettingsAction.PickImageQuality -> {
                _uiState.value?.let {
                    _uiState.onNext(it.copy(selectedImageQuality = action.option))
                }
            }
            SettingsAction.ConfirmImageQuality -> {
                _uiState.value?.let {
                    _uiState.onNext(
                        it.copy(
                            sheet = SettingsSheet.Main,
                            imageQuality = it.selectedImageQuality ?: "original"
                        )
                    )
                }
                _uiState.value?.selectedImageQuality?.let {
                    userDataRepository.updateImageQuality(value = it)
                }
            }
            SettingsAction.BackToMainFromImageQuality -> {
                _uiState.value?.let {
                    _uiState.onNext(
                        it.copy(
                            sheet = SettingsSheet.Main,
                            imageQuality = it.imageQuality,
                            selectedLanguage = null
                        )
                    )
                }
            }
            SettingsAction.BackToMainFromThemeSetting -> {
                _uiState.value?.let {
                    _uiState.onNext(
                        it.copy(
                            sheet = SettingsSheet.Main,
                            theme = it.theme,
                            selectedTheme = null
                        )
                    )
                }
            }
            SettingsAction.ConfirmTheme -> {
                _uiState.value?.let {
                    _uiState.onNext(
                        it.copy(
                            sheet = SettingsSheet.Main,
                            theme = it.selectedTheme ?: DarkThemeConfig.FOLLOW_SYSTEM
                        )
                    )
                }
                _uiState.value?.selectedTheme?.let {
                    userDataRepository.updateDarkMode(darkThemeConfig = it)
                }
            }
            SettingsAction.OpenThemeSetting -> {
                _uiState.value?.let {
                    _uiState.onNext(
                        it.copy(
                            sheet = SettingsSheet.ThemeSetting,
                            theme = it.theme,
                            themeList = DarkThemeConfig.entries
                        )
                    )
                }
            }
            is SettingsAction.PickTheme -> {
                _uiState.value?.let {
                    _uiState.onNext(it.copy(selectedTheme = action.option))
                }
            }
            is SettingsAction.SetCheatActive -> {
                _uiState.value?.let {
                    _uiState.onNext(
                        it.copy(
                            isCheatActive = it.isCheatActive?.not()
                        )
                    )
                }
                _uiState.value?.isCheatActive?.let { isCheatActive ->
                    userDataRepository.updateIsCheatActive(value = !isCheatActive)
                }
            }
        }
    }

    fun onClickTitle() {
        viewModelScope.launch {
            titleClickCount++

            if (titleClickCount >= 10) {
                _isCheatActive.onNext(_isCheatActive.value?.not() ?: false)
                titleClickCount = 0
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
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
    val allRegions: List<LocaleOption> = emptyList(),
    val isCheatActive: Boolean? = null
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

    // Cheat sub sheet events
    data class SetCheatActive(val enabled: Boolean) : SettingsAction
}