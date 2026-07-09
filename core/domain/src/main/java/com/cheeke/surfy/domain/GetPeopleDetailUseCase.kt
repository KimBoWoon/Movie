package com.cheeke.surfy.domain

import com.cheeke.surfy.data.repository.PeopleDataBaseRepository
import com.cheeke.surfy.data.repository.PeopleDetailRepository
import com.cheeke.surfy.model.People
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class GetPeopleDetailUseCase @Inject constructor(
    private val detailRepository: PeopleDetailRepository,
    private val peopleDataBaseRepository: PeopleDataBaseRepository
) {
    operator fun invoke(personId: Int): Flowable<People> =
        Flowable.combineLatest(
            detailRepository.getData(id = personId).toFlowable(),
            detailRepository.getCombineCredits(personId = personId).toFlowable(),
            detailRepository.getExternalIds(personId = personId).toFlowable(),
            peopleDataBaseRepository.isFavorite(id = personId).toFlowable(BackpressureStrategy.LATEST)
        ) { peopleDetail, combineCredits, externalIds, isFavorite ->
            peopleDetail.copy(
                combineCredits = combineCredits,
                externalIds = externalIds,
                isFavorite = isFavorite
            )
        }
}