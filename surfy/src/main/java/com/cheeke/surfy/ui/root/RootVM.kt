package com.cheeke.surfy.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.repository.MovieDataBaseRepository
import com.cheeke.surfy.data.repository.TvDataBaseRepository
import com.cheeke.surfy.data.util.NetworkMonitor
import com.cheeke.surfy.deeplink.DeepLinkManager
import com.cheeke.surfy.model.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootVM @Inject constructor(
    private val deepLinkManager: DeepLinkManager,
    private val movieDataBaseRepository: MovieDataBaseRepository,
    private val tvDataBaseRepository: TvDataBaseRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    private val _nextWeekReleaseMedias: MutableStateFlow<List<Media>> = MutableStateFlow(value = emptyList())
    val nextWeekReleaseMedias = _nextWeekReleaseMedias.asStateFlow()
    val bottomDeeplink = deepLinkManager.bottomDeeplink
    private val _isOffline = BehaviorSubject.createDefault(false)
    val isOffline: Observable<Boolean> = _isOffline.hide()
    private val disposable = CompositeDisposable()

    init {
        networkMonitor.isOnline
            .map(Boolean::not)
            .distinctUntilChanged()
            .subscribe(
                { value ->
                    _isOffline.onNext(value)
                },
                { error ->
                    Log.e("NetworkMonitor Error $error")
                }
            )
            .addTo(disposable)

        Single.zip(
            movieDataBaseRepository.getNextWeekReleaseMovies(),
            tvDataBaseRepository.getNextWeekReleaseTvs()
        ) { movies, tvs ->
            (movies + tvs).sortedBy { it.releaseDate }
        }.subscribe(
            {
                viewModelScope.launch { _nextWeekReleaseMedias.emit(it.sortedBy { it.releaseDate }) }
            }
        ).addTo(disposable)
    }

    fun consumeBottomDeepLink() {
        deepLinkManager.consumeBottomDeepLink()
    }

    override fun onCleared() {
        disposable.clear()
    }
}