package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.model.PeopleDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoritePeopleUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {
    operator fun invoke(): Flow<List<PeopleDetail>> = databaseRepository.getPeople()
        .map { peoples ->
            peoples.map { people ->
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
                    isFavorite = true
                )
            }
        }
}