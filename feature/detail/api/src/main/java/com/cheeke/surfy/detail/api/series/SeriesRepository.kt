package com.cheeke.surfy.detail.api.series

import com.cheeke.surfy.detail.api.DetailRepository
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Series
import kotlinx.coroutines.flow.Flow

interface SeriesDetailRepository : DetailRepository<Series> {
    override fun getData(id: Int): Flow<Series>
    fun getMovieSeriesImageList(collectionId: Int): Flow<ImageList>
}