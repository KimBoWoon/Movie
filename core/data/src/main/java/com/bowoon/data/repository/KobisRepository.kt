package com.bowoon.data.repository

import com.bowoon.model.KOBISBoxOffice
import kotlinx.coroutines.flow.Flow

interface KobisRepository {
    fun getDailyBoxOffice(
        key: String,
        targetDt: String
    ): Flow<KOBISBoxOffice>
}