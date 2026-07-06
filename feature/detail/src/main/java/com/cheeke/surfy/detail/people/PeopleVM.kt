package com.cheeke.surfy.detail.people

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheeke.surfy.analytics.AnalyticsHelper
import com.cheeke.surfy.analytics.logSelectContent
import com.cheeke.surfy.common.Result
import com.cheeke.surfy.data.repository.PeopleDataBaseRepository
import com.cheeke.surfy.domain.GetPeopleDetailUseCase
import com.cheeke.surfy.model.People
import com.cheeke.surfy.network.model.SurfyNetworkException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.processors.BehaviorProcessor
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PeopleVM.Factory::class)
class PeopleVM @AssistedInject constructor(
    @Assisted val id: Int,
    getPeopleDetail: GetPeopleDetailUseCase,
    private val peopleDataBaseRepository: PeopleDataBaseRepository,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {
    companion object {
        private const val TAG = "PeopleVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): PeopleVM
    }

    private val reload = BehaviorProcessor.createDefault<Unit>(Unit)
    private val detail = reload
        .switchMap {
            trace("GetPeopleDetail") {
                getPeopleDetail(id)
                    .map<Result<People>> { Result.Success(it) }
                    .startWithItem(Result.Loading)
                    .onErrorReturn { Result.Error(it) }
            }
        }
    val people = detail.map { result ->
        when (result) {
            Result.Loading -> PeopleState.Loading
            is Result.Success -> {
                analyticsHelper.logSelectContent(contentType = "movie", media = result.data)
                PeopleState.Success(data = result.data)
            }
            is Result.Error -> PeopleState.Error(result.throwable as SurfyNetworkException)
        }
    }.replay(1)
        .refCount()

    init {
        viewModelScope.launch {
            reload.onNext(Unit)
        }
    }

    fun restart() {
        viewModelScope.launch {
            reload.onNext(Unit)
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