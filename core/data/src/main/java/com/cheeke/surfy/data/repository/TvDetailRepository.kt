package com.cheeke.surfy.data.repository

import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeasons
import com.cheeke.surfy.network.TvRemoteDataSource
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

interface TvDetailRepository : DetailRepository<Tv> {
    override fun getData(id: Int): Single<Tv>
    fun getTvSeasons(seriesId: Int, seasonNumber: Int): Single<TvSeasons>
    fun getTvEpisode(seriesId: Int, seasonNumber: Int, episodeNumber: Int): Single<TvEpisode>
}

class TvDetailRepositoryImpl @Inject constructor(
    private val apis: TvRemoteDataSource,
    private val requestOptionsProvider: DetailRequestOptionsProvider
) : TvDetailRepository {
    override fun getData(id: Int): Single<Tv> = requestOptionsProvider.current()
        .flatMap {
            apis.getTv(
                id = id,
                language = it.languageTag,
                includeImageLanguage = it.includeImageLanguage
            )
        }

    override fun getTvSeasons(
        seriesId: Int,
        seasonNumber: Int
    ): Single<TvSeasons> = requestOptionsProvider.current()
        .flatMap {
            apis.getTvSeasons(
                seriesId = seriesId,
                seasonNumber = seasonNumber,
                language = it.languageTag
            )
        }

    override fun getTvEpisode(
        seriesId: Int,
        seasonNumber: Int,
        episodeNumber: Int
    ): Single<TvEpisode> = requestOptionsProvider.current()
        .flatMap {
            apis.getTvEpisode(
                seriesId = seriesId,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber,
                language = it.languageTag
            )
        }
}