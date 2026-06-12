package com.cheeke.surfy.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
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
    @Assisted initialTabKey: String,
    val favoriteTabs: Map<String, @JvmSuppressWildcards FavoriteTab>
) : ViewModel() {
    companion object {
        internal const val TAG = "FavoriteVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(tab: String): FavoriteVM
    }

    private val _currentTab = MutableStateFlow(value = initialTabKey)
    val currentTab = _currentTab.asStateFlow()
    val tabList = listOf(
        favoriteTabs.getValue(key = FavoriteKeys.MOVIE),
        favoriteTabs.getValue(key = FavoriteKeys.TV),
        favoriteTabs.getValue(key = FavoriteKeys.PEOPLE)
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentPagingItems = currentTab
        .flatMapLatest { key -> favoriteTabs.getValue(key = key).pagingSource }.cachedIn(scope = viewModelScope)

    fun updateTabKey(key: String) {
        _currentTab.value = key
    }
}

object FavoriteKeys {
    const val MOVIE = "movie"
    const val TV = "tv"
    const val PEOPLE = "people"
}