package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.model.People
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPeopleDetailUseCase @Inject constructor(
    private val detailRepository: DetailRepository,
    private val databaseRepository: DatabaseRepository,
) {
    operator fun invoke(personId: Int): Flow<PeopleWithFavorite> =
        combine(
            detailRepository.getPeople(personId = personId),
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