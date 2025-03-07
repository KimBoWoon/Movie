package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.model.PeopleDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPeopleDetailUseCase @Inject constructor(
    private val detailRepository: DetailRepository,
    private val databaseRepository: DatabaseRepository
) {
    operator fun invoke(personId: Int): Flow<PeopleDetail> =
        combine(
            detailRepository.getPeople(personId = personId),
            detailRepository.getCombineCredits(personId = personId),
            detailRepository.getExternalIds(personId = personId),
            databaseRepository.getPeople()
        ) { peopleDetail, combineCredits, externalIds, favoritePeoples ->
            peopleDetail.copy(
                combineCredits = combineCredits,
                externalIds = externalIds,
                isFavorite = favoritePeoples.find { it.id == peopleDetail.id } != null
            )
        }
}