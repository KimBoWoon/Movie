package com.cheeke.surfy.detail.api.tv

import androidx.paging.PagingSource
import com.cheeke.surfy.detail.api.DataBaseRepository
import com.cheeke.surfy.model.Review
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.model.Tv

interface TvRepository : DataBaseRepository<Tv> {
    suspend fun getNextWeekReleaseTvs(): List<Tv>
    fun getSimilarTvPagingSource(
        id: Int,
        language: String,
        region: String
    ): PagingSource<Int, SimilarMedia>

    fun getTvReviews(seriesId: Int, language: String, region: String): PagingSource<Int, Review>
}