package com.cheeke.surfy.favorite.impl

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.cheeke.surfy.favorite.api.FavoriteContentType
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
    @Assisted initialTabKey: FavoriteContentType,
    val repositories: Map<FavoriteContentType, @JvmSuppressWildcards FavoriteRepository>
) : ViewModel() {
    companion object {
        internal const val TAG = "FavoriteVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(tab: FavoriteContentType): FavoriteVM
    }

    private val _currentTab = MutableStateFlow(value = initialTabKey)
    val currentTab = _currentTab.asStateFlow()
    val tabList = repositories
        .toSortedMap(comparator = compareBy { key -> key.ordinal })
        .map { (key, value) ->
            FavoriteTabUiModel(
                type = key,
                label = value.label
            )
        }
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentPagingItems = currentTab
        .flatMapLatest { key -> repositories.getValue(key = key).pagingSource }
        .cachedIn(scope = viewModelScope)

    fun updateTabKey(key: FavoriteContentType) {
        _currentTab.value = key
    }
}

data class FavoriteTabUiModel(
    val type: FavoriteContentType,
    @param:StringRes val label: Int
)