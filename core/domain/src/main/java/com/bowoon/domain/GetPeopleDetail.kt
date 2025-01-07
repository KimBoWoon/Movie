package com.bowoon.domain

import com.bowoon.data.repository.TMDBRepository
import com.bowoon.model.TMDBPeopleDetail
import com.bowoon.model.TMDBPeopleImages
import com.bowoon.model.TMDBPeopleProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPeopleDetail @Inject constructor(
    private val tmdbRepository: TMDBRepository
) {
    operator fun invoke(personId: Int): Flow<TMDBPeopleDetail> =
        combine(
            tmdbRepository.getPeople(personId = personId),
            tmdbRepository.posterUrl
        ) { tmdbPeopleDetail, posterUrl ->
            TMDBPeopleDetail(
                adult = tmdbPeopleDetail.adult,
                alsoKnownAs = tmdbPeopleDetail.alsoKnownAs,
                biography = tmdbPeopleDetail.biography,
                birthday = tmdbPeopleDetail.birthday,
                deathday = tmdbPeopleDetail.deathday,
                gender = tmdbPeopleDetail.gender,
                homepage = tmdbPeopleDetail.homepage,
                id = tmdbPeopleDetail.id,
                images = TMDBPeopleImages(
                    tmdbPeopleDetail.images?.profiles?.map {
                        TMDBPeopleProfile(
                            aspectRatio = it.aspectRatio,
                            filePath = "$posterUrl${it.filePath}",
                            height = it.height,
                            iso6391 = it.iso6391,
                            voteAverage = it.voteAverage,
                            voteCount = it.voteCount,
                            width = it.width
                        )
                    }
                ),
                imdbId = tmdbPeopleDetail.imdbId,
                knownForDepartment = tmdbPeopleDetail.knownForDepartment,
                name = tmdbPeopleDetail.name,
                placeOfBirth = tmdbPeopleDetail.placeOfBirth,
                popularity = tmdbPeopleDetail.popularity,
                profilePath = "$posterUrl${tmdbPeopleDetail.profilePath}"
            )
        }
}