package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.model.PeopleDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFavoritePeopleUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val myDataRepository: MyDataRepository
) {
    operator fun invoke(): Flow<List<PeopleDetail>> = combine(
        myDataRepository.posterUrl,
        databaseRepository.getPeople(),
    ) { posterUrl, favoritePeoples ->
        favoritePeoples.map { people ->
            PeopleDetail(
                adult = people.adult,
                alsoKnownAs = people.alsoKnownAs,
                biography = people.biography,
                birthday = people.birthday,
                combineCredits = people.combineCredits,
                deathday = people.deathday,
                externalIds = people.externalIds,
                gender = people.gender,
                homepage = people.homepage,
                id = people.id,
                images = people.images,
                imdbId = people.imdbId,
                knownForDepartment = people.knownForDepartment,
                name = people.name,
                placeOfBirth = people.placeOfBirth,
                popularity = people.popularity,
                profilePath = people.profilePath,
                posterUrl = posterUrl,
                isFavorite = favoritePeoples.find { it.id == people.id } != null
            )
        }
    }
}