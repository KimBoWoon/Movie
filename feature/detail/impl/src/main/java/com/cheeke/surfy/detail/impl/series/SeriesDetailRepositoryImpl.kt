package com.cheeke.surfy.detail.impl.series

import com.cheeke.surfy.detail.api.DetailRequestOptionsProvider
import com.cheeke.surfy.detail.api.series.SeriesDetailRepository
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.network.SeriesRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SeriesDetailRepositoryImpl @Inject constructor(
    private val apis: SeriesRemoteDataSource,
    private val requestOptionsProvider: DetailRequestOptionsProvider
) : SeriesDetailRepository {
    override fun getData(id: Int): Flow<Series> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = apis.getMovieSeries(collectionId = id, language = internalData.languageTag))
    }

    override fun getMovieSeriesImageList(collectionId: Int): Flow<ImageList> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = apis.getSeriesImages(collectionId = collectionId, includeImageLanguage = internalData.localizedImageLanguage, language = internalData.languageTag))
    }
}