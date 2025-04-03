package com.bowoon.testing.repository

import androidx.annotation.VisibleForTesting
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalData
import com.bowoon.model.Language
import com.bowoon.model.Regions
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull

class TestMyDataRepository : MyDataRepository {
    private val _externalData = MutableSharedFlow<ExternalData>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val currentExternalData = _externalData.replayCache
    override val externalData: Flow<ExternalData> = _externalData.filterNotNull()

    override fun getConfiguration(): Flow<Configuration> = externalData.mapNotNull { it.configuration }

    override fun getAvailableLanguage(): Flow<List<Language>> = externalData.mapNotNull { it.language }

    override fun getAvailableRegion(): Flow<Regions> = externalData.mapNotNull { it.region }

    @VisibleForTesting
    fun setExternalData(externalData: ExternalData) {
        _externalData.tryEmit(externalData)
    }
}