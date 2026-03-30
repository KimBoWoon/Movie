package com.cheeke.surfy.data.repository

import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeasons
import com.cheeke.surfy.network.TvRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface TvDetailRepository : DetailRepository<Tv> {
    override fun getData(id: Int): Flow<Tv>
    fun getTvSeasons(seriesId: Int, seasonNumber: Int): Flow<TvSeasons>
    fun getTvEpisode(seriesId: Int, seasonNumber: Int, episodeNumber: Int): Flow<TvEpisode>
}

class TvDetailRepositoryImpl @Inject constructor(
    private val apis: TvRemoteDataSource,
    private val requestOptionsProvider: DetailRequestOptionsProvider
) : TvDetailRepository {
    override fun getData(id: Int): Flow<Tv> = flow {
        val internalData = requestOptionsProvider.current()
        val tv = apis.getTv(id = id, language = internalData.languageTag, includeImageLanguage = internalData.includeImageLanguage)
        emit(value = tv)
    }

    override fun getTvSeasons(
        seriesId: Int,
        seasonNumber: Int
    ): Flow<TvSeasons> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = apis.getTvSeasons(seriesId = seriesId, seasonNumber = seasonNumber, language = internalData.languageTag))
    }

    override fun getTvEpisode(
        seriesId: Int,
        seasonNumber: Int,
        episodeNumber: Int
    ): Flow<TvEpisode> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = apis.getTvEpisode(seriesId = seriesId, seasonNumber = seasonNumber, episodeNumber = episodeNumber, language = internalData.languageTag))
    }
}