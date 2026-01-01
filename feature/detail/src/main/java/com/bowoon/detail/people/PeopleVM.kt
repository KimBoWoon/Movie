package com.bowoon.detail.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.restartableStateIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.domain.GetPeopleDetailUseCase
import com.bowoon.model.People
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PeopleVM.Factory::class)
class PeopleVM @AssistedInject constructor(
    @Assisted val id: Int,
    getPeopleDetail: GetPeopleDetailUseCase,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        private const val TAG = "PeopleVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): PeopleVM
    }

    val people = getPeopleDetail(id)
        .asResult()
        .map {
            when (it) {
                is Result.Loading -> PeopleState.Loading
                is Result.Success -> PeopleState.Success(it.data)
                is Result.Error -> PeopleState.Error(it.throwable)
            }
        }.restartableStateIn(
            scope = viewModelScope,
            initialValue = PeopleState.Loading,
            started = SharingStarted.Lazily
        )

    fun restart() {
        people.restart()
    }

    fun insertPeople(people: People) {
        viewModelScope.launch {
            databaseRepository.insertPeople(people)
        }
    }

    fun deletePeople(people: People) {
        viewModelScope.launch {
            databaseRepository.deletePeople(people)
        }
    }
}

sealed interface PeopleState {
    data object Loading : PeopleState
    data class Success(val data: People) : PeopleState
    data class Error(val throwable: Throwable) : PeopleState
}