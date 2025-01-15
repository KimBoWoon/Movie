package com.bowoon.data.repository

import com.bowoon.model.kobis.KOBISBoxOffice

interface KOBISRepository {
    suspend fun getDailyBoxOffice(
        key: String,
        targetDt: String
    ): KOBISBoxOffice
}