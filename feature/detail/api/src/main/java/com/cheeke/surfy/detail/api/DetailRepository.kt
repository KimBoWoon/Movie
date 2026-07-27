package com.cheeke.surfy.detail.api

import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.model.InternalData
import com.cheeke.surfy.model.Media
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DetailRequestOptionsProvider @Inject constructor(
    private val datastore: InternalDataSource
) {
    suspend fun current(): DetailRequestOptions =
        DetailRequestOptions(internalData = datastore.userData.first())
}

data class DetailRequestOptions(
    private val internalData: InternalData
) {
    val region: String = internalData.region
    val language: String = internalData.language
    val languageTag: String = "${internalData.language}-${internalData.region}"
    val includeImageLanguage: String = "${internalData.language},null"
    val localizedImageLanguage: String = "$languageTag,null"
    val includeAdult: Boolean = internalData.isAdult
}

interface DetailRepository<T : Media> {
    fun getData(id: Int): Flow<T>
}