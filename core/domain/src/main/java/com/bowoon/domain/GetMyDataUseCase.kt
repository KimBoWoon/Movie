package com.bowoon.domain

import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MyData
import com.bowoon.model.PosterSize
import com.bowoon.model.TMDBLanguageItem
import com.bowoon.model.TMDBRegion
import com.bowoon.model.TMDBRegionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMyDataUseCase @Inject constructor(
    private val tmdbRepository: TMDBRepository,
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<MyData> =
        combine(
            tmdbRepository.getConfiguration(),
            tmdbRepository.availableRegion(),
            tmdbRepository.availableLanguage(),
            userDataRepository.userData
        ) { networkConfiguration, networkRegion, networkLanguage, userData ->
            val region = TMDBRegion(
                results = networkRegion.results?.map {
                    TMDBRegionResult(
                        englishName = it.englishName,
                        iso31661 = it.iso31661,
                        nativeName = it.nativeName,
                        isSelected = userData.region == it.iso31661
                    )
                }
            )
            val language = networkLanguage.map {
                TMDBLanguageItem(
                    englishName = it.englishName,
                    iso6391 = it.iso6391,
                    name = it.name,
                    isSelected = userData.language == it.iso6391
                )
            }
            val posterSize = networkConfiguration.images?.posterSizes?.map {
                PosterSize(
                    size = it,
                    isSelected = userData.imageQuality == it
                )
            } ?: emptyList()
            MyData(
                configuration = networkConfiguration,
                region = region,
                language = language,
                posterSize = posterSize
            )
        }
}