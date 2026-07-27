package com.cheeke.surfy.detail.impl.people

import com.cheeke.surfy.detail.api.people.PeopleDetailRepository
import com.cheeke.surfy.detail.api.people.PeopleRepository
import com.cheeke.surfy.model.People
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPeopleDetailUseCase @Inject constructor(
    private val detailRepository: PeopleDetailRepository,
    private val peopleDataBaseRepository: PeopleRepository
) {
    operator fun invoke(personId: Int): Flow<People> =
        combine(
            detailRepository.getData(id = personId),
            detailRepository.getCombineCredits(personId = personId),
            detailRepository.getExternalIds(personId = personId),
            peopleDataBaseRepository.isFavorite(id = personId)
        ) { peopleDetail, combineCredits, externalIds, isFavorite ->
            peopleDetail.copy(
                combineCredits = combineCredits,
                externalIds = externalIds,
                isFavorite = isFavorite
            )
        }
}