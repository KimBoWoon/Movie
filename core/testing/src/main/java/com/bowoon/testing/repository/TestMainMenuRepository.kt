package com.bowoon.testing.repository

import androidx.annotation.VisibleForTesting
import com.bowoon.data.repository.MainMenuRepository
import com.bowoon.data.util.Synchronizer
import java.time.LocalDate

class TestMainMenuRepository : MainMenuRepository {
    private var date = LocalDate.now()

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        LocalDate.now().minusDays(1).isAfter(date)

    @VisibleForTesting
    fun setDate(date: LocalDate) {
        this.date = date
    }
}