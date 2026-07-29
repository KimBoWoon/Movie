package com.cheeke.surfy.detail.impl.people

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheeke.surfy.analytics.api.AnalyticsHelper
import com.cheeke.surfy.analytics.api.logSelectContent
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.common.asResult
import com.cheeke.surfy.detail.api.people.PeopleRepository
import com.cheeke.surfy.model.People
import com.cheeke.surfy.network.api.SurfyNetworkException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PeopleVM.Factory::class)
class PeopleVM @AssistedInject constructor(
    @Assisted val id: Int,
    getPeopleDetail: GetPeopleDetailUseCase,
    private val peopleDataBaseRepository: PeopleRepository,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {
    companion object {
        private const val TAG = "PeopleVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): PeopleVM
    }

    private val reload = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val people = reload
        .flatMapLatest {
            trace(sectionName = "GetPeopleDetail") {
                val start = System.nanoTime()
                try {
                    getPeopleDetail(personId = id)
                } finally {
                    val elapsed = System.nanoTime() - start
                    Log.i(TAG, "GetPeopleDetail: ${elapsed / 1_000_000.0} ms")
                }
            }.asResult()
        }.map { result ->
            when (result) {
                is Result.Loading -> PeopleState.Loading
                is Result.Success -> {
                    analyticsHelper.logSelectContent(contentType = "people", id = id, title = result.data.title.orEmpty())
                    PeopleState.Success(data = result.data)
                }
                is Result.Error -> PeopleState.Error(result.throwable as SurfyNetworkException)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = PeopleState.Loading,
            started = SharingStarted.Lazily
        )

    init {
        viewModelScope.launch {
            reload.emit(value = Unit)
        }
    }

    fun restart() {
        viewModelScope.launch {
            reload.emit(value = Unit)
        }
    }

    fun insertPeople(people: People) {
        viewModelScope.launch {
            peopleDataBaseRepository.insert(media = people)
        }
    }

    fun deletePeople(people: People) {
        viewModelScope.launch {
            peopleDataBaseRepository.delete(media = people)
        }
    }
}

sealed interface PeopleState {
    data object Loading : PeopleState
    data class Success(val data: People) : PeopleState
    data class Error(val throwable: SurfyNetworkException) : PeopleState
}