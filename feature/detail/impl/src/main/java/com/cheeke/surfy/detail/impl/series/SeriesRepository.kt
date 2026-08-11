package com.cheeke.surfy.detail.impl.series

import com.cheeke.surfy.detail.impl.DetailRepository
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Series
import kotlinx.coroutines.flow.Flow

interface SeriesRepository : DetailRepository<Series> {
    override fun getData(id: Int): Flow<Series>
    fun getMovieSeriesImageList(collectionId: Int): Flow<ImageList>
}