package com.cheeke.surfy.detail.impl

import com.cheeke.surfy.model.InternalData
import com.cheeke.surfy.userdata.api.UserDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DetailRequestOptionsProvider @Inject constructor(
    private val userdataRepository: UserDataRepository
) {
    suspend fun current(): DetailRequestOptions =
        DetailRequestOptions(internalData = userdataRepository.internalData.first())
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