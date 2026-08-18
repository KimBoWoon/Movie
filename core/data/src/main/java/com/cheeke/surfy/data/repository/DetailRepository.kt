package com.cheeke.surfy.data.repository

import com.cheeke.surfy.model.InternalData
import com.cheeke.surfy.model.Media
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DetailRequestOptionsProvider @Inject constructor(
    private val userdata: UserDataRepository
) {
    suspend fun current(): DetailRequestOptions =
        DetailRequestOptions(internalData = userdata.internalData.first())
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