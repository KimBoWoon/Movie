package com.bowoon.people

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.common.restartableStateIn
import com.bowoon.domain.GetPeopleDetail
import com.bowoon.model.TMDBPeopleDetail
import com.bowoon.people.navigation.PeopleRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PeopleVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getPeopleDetail: GetPeopleDetail
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
            started = SharingStarted.WhileSubscribed(5_000)
        )

    fun restart() {
        people.restart()
    }
}

sealed interface PeopleState {
    data object Loading : PeopleState
    data class Success(val data: TMDBPeopleDetail) : PeopleState
    data class Error(val throwable: Throwable) : PeopleState
}