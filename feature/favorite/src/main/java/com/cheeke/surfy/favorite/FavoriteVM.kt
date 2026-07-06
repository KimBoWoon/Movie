package com.cheeke.surfy.favorite

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.rxjava3.cachedIn
import com.cheeke.surfy.data.repository.FavoriteKeys
import com.cheeke.surfy.data.repository.FavoriteRepository
import com.cheeke.surfy.feature.favorite.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.processors.BehaviorProcessor
import kotlinx.coroutines.ExperimentalCoroutinesApi

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

    private val _currentTab = BehaviorProcessor.createDefault(initialTabKey)
    val currentTab = _currentTab.hide()
    val tabList = FavoriteKeys.entries.map {
        FavoriteTabUiModel(
            type = it,
            titleRes = when (it) {
                FavoriteKeys.MOVIE -> R.string.movie
                FavoriteKeys.TV -> R.string.tv
                FavoriteKeys.PEOPLE -> R.string.people
            }
        )
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentPagingItems = currentTab
        .switchMap { key -> repositories.getValue(key = key).pagingSource }
        .cachedIn(scope = viewModelScope)

    fun updateTabKey(key: FavoriteKeys) {
        _currentTab.onNext(key)
    }
}

data class FavoriteTabUiModel(
    val type: FavoriteKeys,
    @param:StringRes val titleRes: Int
)