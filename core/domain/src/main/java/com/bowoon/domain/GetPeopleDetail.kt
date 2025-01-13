package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.model.CombineCredits
import com.bowoon.model.CombineCreditsCast
import com.bowoon.model.CombineCreditsCrew
import com.bowoon.model.PeopleDetail
import com.bowoon.model.PeopleExternalIds
import com.bowoon.model.PeopleImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPeopleDetail @Inject constructor(
    private val tmdbRepository: TMDBRepository,
    private val databaseRepository: DatabaseRepository
) {
    operator fun invoke(personId: Int): Flow<PeopleDetail> =
        combine(
            tmdbRepository.getPeople(personId = personId),
            tmdbRepository.getCombineCredits(personId = personId),
            tmdbRepository.getExternalIds(personId = personId),
            tmdbRepository.posterUrl,
            databaseRepository.getPeople()
        ) { tmdbPeopleDetail, tmdbCombineCredits, tmdbExternalIds, posterUrl, favoritePeoples ->
            PeopleDetail(
                adult = tmdbPeopleDetail.adult,
                alsoKnownAs = tmdbPeopleDetail.alsoKnownAs,
                biography = tmdbPeopleDetail.biography,
                birthday = tmdbPeopleDetail.birthday,
                combineCredits = CombineCredits(
                    cast = tmdbCombineCredits.cast?.map {
                        CombineCreditsCast(
                            adult = it.adult,
                            backdropPath = it.backdropPath,
                            character = it.character,
                            creditId = it.creditId,
                            episodeCount = it.episodeCount,
                            firstAirDate = it.firstAirDate,
                            genreIds = it.genreIds,
                            id = it.id,
                            mediaType = it.mediaType,
                            name = it.name,
                            order = it.order,
                            originCountry = it.originCountry,
                            originalLanguage = it.originalLanguage,
                            originalName = it.originalName,
                            originalTitle = it.originalTitle,
                            overview = it.overview,
                            popularity = it.popularity,
                            posterPath = "$posterUrl${it.posterPath}",
                            releaseDate = it.releaseDate,
                            title = it.title,
                            video = it.video,
                            voteAverage = it.voteAverage,
                            voteCount = it.voteCount
                        )
                    },
                    crew = tmdbCombineCredits.crew?.map {
                        CombineCreditsCrew(
                            adult = it.adult,
                            backdropPath = it.backdropPath,
                            creditId = it.creditId,
                            episodeCount = it.episodeCount,
                            firstAirDate = it.firstAirDate,
                            genreIds = it.genreIds,
                            id = it.id,
                            mediaType = it.mediaType,
                            name = it.name,
                            originCountry = it.originCountry,
                            originalLanguage = it.originalLanguage,
                            originalName = it.originalName,
                            originalTitle = it.originalTitle,
                            overview = it.overview,
                            popularity = it.popularity,
                            posterPath = "$posterUrl${it.posterPath}",
                            releaseDate = it.releaseDate,
                            title = it.title,
                            video = it.video,
                            voteAverage = it.voteAverage,
                            voteCount = it.voteCount,
                            department = it.department,
                            job = it.job
                        )
                    },
                    id = tmdbCombineCredits.id
                ),
                deathday = tmdbPeopleDetail.deathday,
                externalIds = PeopleExternalIds(
                    facebookId = tmdbExternalIds.facebookId,
                    freebaseId = tmdbExternalIds.freebaseId,
                    freebaseMid = tmdbExternalIds.freebaseMid,
                    id = tmdbExternalIds.id,
                    imdbId = tmdbExternalIds.imdbId,
                    instagramId = tmdbExternalIds.instagramId,
                    tiktokId = tmdbExternalIds.tiktokId,
                    tvrageId = tmdbExternalIds.tvrageId,
                    twitterId = tmdbExternalIds.twitterId,
                    wikidataId = tmdbExternalIds.wikidataId,
                    youtubeId = tmdbExternalIds.youtubeId
                ),
                gender = tmdbPeopleDetail.gender,
                homepage = tmdbPeopleDetail.homepage,
                id = tmdbPeopleDetail.id,
                images = tmdbPeopleDetail.images?.profiles?.map {
                    PeopleImage(
                        aspectRatio = it.aspectRatio,
                        filePath = "$posterUrl${it.filePath}",
                        height = it.height,
                        iso6391 = it.iso6391,
                        voteAverage = it.voteAverage,
                        voteCount = it.voteCount,
                        width = it.width
                    )
                },
                imdbId = tmdbPeopleDetail.imdbId,
                knownForDepartment = tmdbPeopleDetail.knownForDepartment,
                name = tmdbPeopleDetail.name,
                placeOfBirth = tmdbPeopleDetail.placeOfBirth,
                popularity = tmdbPeopleDetail.popularity,
                profilePath = "$posterUrl${tmdbPeopleDetail.profilePath}",
                posterUrl = posterUrl,
                isFavorite = favoritePeoples.find { it.id == tmdbPeopleDetail.id } != null
            )
        }
}