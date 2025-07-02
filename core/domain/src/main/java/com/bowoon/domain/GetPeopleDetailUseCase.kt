package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.model.People
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPeopleDetailUseCase @Inject constructor(
    private val detailRepository: DetailRepository,
    private val databaseRepository: DatabaseRepository,
    private val movieAppDataRepository: MovieAppDataRepository
) {
    val movieAppData = movieAppDataRepository.movieAppData

    operator fun invoke(personId: Int): Flow<People> =
        combine(
            detailRepository.getPeople(personId = personId),
            detailRepository.getCombineCredits(personId = personId),
            detailRepository.getExternalIds(personId = personId),
            databaseRepository.getPeople()
        ) { peopleDetail, combineCredits, externalIds, favoritePeoples ->
            peopleDetail.copy(
                combineCredits = combineCredits.copy(
                    cast = combineCredits.cast?.map {
                        it.copy(
                            backdropPath = "${movieAppData.value.getImageUrl()}${it.backdropPath}",
                            posterPath = "${movieAppData.value.getImageUrl()}${it.posterPath}"
                        )
                    },
                    crew = combineCredits.crew?.map {
                        it.copy(
                            backdropPath = "${movieAppData.value.getImageUrl()}${it.backdropPath}",
                            posterPath = "${movieAppData.value.getImageUrl()}${it.posterPath}"
                        )
                    }
                ),
                profilePath = "${movieAppData.value.getImageUrl()}${peopleDetail.profilePath}",
                images = peopleDetail.images?.map {
                    it.copy(filePath = "${movieAppData.value.getImageUrl()}${it.filePath}")
                },
                externalIds = externalIds,
                isFavorite = favoritePeoples.find { it.id == peopleDetail.id } != null
            )
        }
}