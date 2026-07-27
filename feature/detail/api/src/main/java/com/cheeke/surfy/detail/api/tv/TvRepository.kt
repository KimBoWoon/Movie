package com.cheeke.surfy.detail.api.tv

import com.cheeke.surfy.detail.api.DataBaseRepository
import com.cheeke.surfy.model.Tv

interface TvRepository : DataBaseRepository<Tv> {
    suspend fun getNextWeekReleaseTvs(): List<Tv>
}