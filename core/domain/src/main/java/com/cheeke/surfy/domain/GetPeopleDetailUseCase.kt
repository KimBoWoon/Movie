package com.cheeke.surfy.domain

import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.data.repository.PeopleDetailRepository
import com.cheeke.surfy.model.People
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPeopleDetailUseCase @Inject constructor(
    private val detailRepository: PeopleDetailRepository,
    private val databaseRepository: DatabaseRepository,
) {
    operator fun invoke(personId: Int): Flow<PeopleWithFavorite> =
        combine(
            detailRepository.getData(id = personId),
            detailRepository.getCombineCredits(personId = personId),
            detailRepository.getExternalIds(personId = personId),
            databaseRepository.isFavoritePeople(id = personId)
        ) { peopleDetail, combineCredits, externalIds, isFavorite ->
            PeopleWithFavorite(
                people = peopleDetail.copy(
                    combineCredits = combineCredits,
                    externalIds = externalIds
                ),
                isFavorite = isFavorite
            )
        }
}

data class PeopleWithFavorite(
    val people: People,
    val isFavorite: Boolean
)