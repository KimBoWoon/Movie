package com.cheeke.surfy.detail.impl.tv

import com.cheeke.surfy.detail.impl.DetailRepository
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeasons
import kotlinx.coroutines.flow.Flow

interface TvDetailRepository : DetailRepository<Tv> {
    override fun getData(id: Int): Flow<Tv>
    fun getTvSeasons(seriesId: Int, seasonNumber: Int): Flow<TvSeasons>
    fun getTvEpisode(seriesId: Int, seasonNumber: Int, episodeNumber: Int): Flow<TvEpisode>
}