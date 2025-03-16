package com.bowoon.domain

import com.bowoon.data.repository.MyDataRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.LanguageItem
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.model.Region
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMovieAppDataUseCase @Inject constructor(
    private val myDataRepository: MyDataRepository,
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<MovieAppData> = combine(
        myDataRepository.externalData,
        userDataRepository.internalData
    ) { externalData, internalData ->
        MovieAppData(
            isAdult = internalData.isAdult,
            autoPlayTrailer = internalData.autoPlayTrailer,
            isDarkMode = internalData.isDarkMode,
            updateDate = internalData.updateDate,
            mainMenu = internalData.mainMenu,
            imageQuality = internalData.imageQuality,
            genres = externalData.genres?.genres,
            secureBaseUrl = externalData.secureBaseUrl,
            configuration = externalData.configuration,
            certification = externalData.certification,
            region = externalData.region?.results?.map {
                Region(
                    englishName = it.englishName,
                    iso31661 = it.iso31661,
                    nativeName = it.nativeName,
                    isSelected = internalData.region == it.iso31661
                )
            },
            language = externalData.language?.map {
                LanguageItem(
                    englishName = it.englishName,
                    iso6391 = it.iso6391,
                    name = it.name,
                    isSelected = internalData.language == it.iso6391
                )
            },
            posterSize = externalData.configuration?.images?.posterSizes?.map {
                PosterSize(
                    size = it,
                    isSelected = internalData.imageQuality == it
                )
            } ?: emptyList()
        )
    }
}