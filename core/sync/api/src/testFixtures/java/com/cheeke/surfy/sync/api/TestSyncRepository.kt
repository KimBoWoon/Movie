package com.cheeke.surfy.sync.api

import androidx.annotation.VisibleForTesting
import java.time.LocalDate

class TestSyncRepository : SyncRepository {
    private var date = LocalDate.now()
    private var isForce = false

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        LocalDate.now().minusDays(1).isAfter(date) || isForce

    @VisibleForTesting
    fun setDate(date: LocalDate) {
        this.date = date
    }

    @VisibleForTesting
    fun setIsForce(value: Boolean) {
        this@TestSyncRepository.isForce = value
    }
}