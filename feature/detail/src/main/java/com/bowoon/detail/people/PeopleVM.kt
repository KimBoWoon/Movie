package com.bowoon.detail.people

import androidx.compose.ui.util.trace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.common.Result
import com.bowoon.common.asResult
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.domain.GetPeopleDetailUseCase
import com.bowoon.domain.PeopleWithFavorite
import com.bowoon.model.People
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
    private val databaseRepository: DatabaseRepository
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
            trace("GetPeopleDetail") { getPeopleDetail(personId = id).asResult() }
        }.map {
            when (it) {
                is Result.Loading -> PeopleState.Loading
                is Result.Success -> PeopleState.Success(data = it.data)
                is Result.Error -> PeopleState.Error(it.throwable)
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
    data class Success(val data: PeopleWithFavorite) : PeopleState
    data class Error(val throwable: Throwable) : PeopleState
}

enum class MediaType(val label: String) {
    MOVIE(label = "movie"), TV(label = "tv")
}