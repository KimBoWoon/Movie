package com.bowoon.domain

import com.bowoon.common.Log
import com.bowoon.common.di.ApplicationScope
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.InitData
import com.bowoon.model.LanguageItem
import com.bowoon.model.MovieGenre
import com.bowoon.model.PosterSize
import com.bowoon.model.Region
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetInitDataUseCase @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val myDataRepository: MyDataRepository,
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<InitData> = combine(
        myDataRepository.tmdbConfiguration,
        userDataRepository.userData
    ) { request, internalData ->
        InitData(
            internalData = internalData,
            secureBaseUrl = request.secureBaseUrl,
            configuration = request.configuration,
            certification = request.certification,
            genres = request.genres?.genres?.map {
                MovieGenre(
                    id = it.id,
                    name = it.name
                )
            },
            region = request.region?.results?.map {
                Region(
                    englishName = it.englishName,
                    iso31661 = it.iso31661,
                    nativeName = it.nativeName,
                    isSelected = internalData.region == it.iso31661
                )
            },
            language = request.language?.map {
                LanguageItem(
                    englishName = it.englishName,
                    iso6391 = it.iso6391,
                    name = it.name,
                    isSelected = internalData.language == it.iso6391
                )
            },
            posterSize = request.configuration?.images?.posterSizes?.map {
                PosterSize(
                    size = it,
                    isSelected = internalData.imageQuality == it
                )
            } ?: emptyList()
        )
    }.catch { e ->
        Log.printStackTrace(e)
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = InitData()
    )
}