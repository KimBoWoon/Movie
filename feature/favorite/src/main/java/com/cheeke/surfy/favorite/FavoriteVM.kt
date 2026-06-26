package com.cheeke.surfy.favorite

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.cheeke.surfy.data.repository.FavoriteKeys
import com.cheeke.surfy.data.repository.FavoriteRepository
import com.cheeke.surfy.feature.favorite.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel(assistedFactory = FavoriteVM.Factory::class)
class FavoriteVM @AssistedInject constructor(
    @Assisted initialTabKey: FavoriteKeys,
    val repositories: Map<FavoriteKeys, @JvmSuppressWildcards FavoriteRepository>
) : ViewModel() {
    companion object {
        internal const val TAG = "FavoriteVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(tab: FavoriteKeys): FavoriteVM
    }

    private val _currentTab = MutableStateFlow(value = initialTabKey)
    val currentTab = _currentTab.asStateFlow()
    val tabList = listOf(
        FavoriteTabUiModel(
            type = FavoriteKeys.MOVIE,
            titleRes = R.string.movie
        ),
        FavoriteTabUiModel(
            type = FavoriteKeys.TV,
            titleRes = R.string.tv
        ),
        FavoriteTabUiModel(
            type = FavoriteKeys.PEOPLE,
            titleRes = R.string.people
        )
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentPagingItems = currentTab
        .flatMapLatest { key -> repositories.getValue(key = key).pagingSource }.cachedIn(scope = viewModelScope)

    fun updateTabKey(key: FavoriteKeys) {
        _currentTab.value = key
    }
}

data class FavoriteTabUiModel(
    val type: FavoriteKeys,
    @param:StringRes val titleRes: Int
)