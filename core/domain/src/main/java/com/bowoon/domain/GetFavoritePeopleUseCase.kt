package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.model.PeopleDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritePeopleUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {
    operator fun invoke(): Flow<List<PeopleDetail>> = databaseRepository.getPeople()
}