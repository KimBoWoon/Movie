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
        favoritePeoples.map {
            PeopleDetail(
                adult = it.adult,
                alsoKnownAs = it.alsoKnownAs,
                biography = it.biography,
                birthday = it.birthday,
                combineCredits = it.combineCredits,
                deathday = it.deathday,
                externalIds = it.externalIds,
                gender = it.gender,
                homepage = it.homepage,
                id = it.id,
                images = it.images,
                imdbId = it.imdbId,
                knownForDepartment = it.knownForDepartment,
                name = it.name,
                placeOfBirth = it.placeOfBirth,
                popularity = it.popularity,
                profilePath = it.profilePath,
                posterUrl = posterUrl,
                isFavorite = favoritePeoples.find { it.id == it.id } != null
            )
        }
    }
}