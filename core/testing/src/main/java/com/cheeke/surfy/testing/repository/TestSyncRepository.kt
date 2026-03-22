package com.cheeke.surfy.testing.repository

import androidx.annotation.VisibleForTesting
import com.cheeke.surfy.data.repository.SyncRepository
import com.cheeke.surfy.data.util.Synchronizer
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