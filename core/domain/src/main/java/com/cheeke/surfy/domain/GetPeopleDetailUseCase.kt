package com.cheeke.surfy.domain

import com.cheeke.surfy.data.repository.PeopleDetailRepository
import com.cheeke.surfy.model.People
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPeopleDetailUseCase @Inject constructor(
    private val detailRepository: PeopleDetailRepository
) {
    operator fun invoke(personId: Int): Flow<People> =
        combine(
            detailRepository.getData(id = personId),
            detailRepository.getCombineCredits(personId = personId),
            detailRepository.getExternalIds(personId = personId)
        ) { peopleDetail, combineCredits, externalIds ->
            peopleDetail.copy(
                combineCredits = combineCredits,
                externalIds = externalIds
            )
        }
}