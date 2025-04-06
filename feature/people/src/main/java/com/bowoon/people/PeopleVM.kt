package com.bowoon.people

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.restartableStateIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.domain.GetPeopleDetailUseCase
import com.bowoon.model.Favorite
import com.bowoon.model.PeopleDetail
import com.bowoon.people.navigation.PeopleRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeopleVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPeopleDetail: GetPeopleDetailUseCase,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        private const val TAG = "PeopleVM"
    }

    private val id = savedStateHandle.toRoute<PeopleRoute>().id
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
            started = SharingStarted.Eagerly
        )

    fun restart() {
        people.restart()
    }

    fun insertPeople(people: Favorite) {
        viewModelScope.launch {
            databaseRepository.insertPeople(people)
        }
    }

    fun deletePeople(people: Favorite) {
        viewModelScope.launch {
            databaseRepository.deletePeople(people)
        }
    }
}

sealed interface PeopleState {
    data object Loading : PeopleState
    data class Success(val data: PeopleDetail) : PeopleState
    data class Error(val throwable: Throwable) : PeopleState
}