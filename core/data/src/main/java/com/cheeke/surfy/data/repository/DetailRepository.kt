package com.cheeke.surfy.data.repository

import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.model.InternalData
import com.cheeke.surfy.model.Media
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DetailRequestOptionsProvider @Inject constructor(
    private val datastore: InternalDataSource
) {
    fun current(): Single<DetailRequestOptions> =
        datastore.userData
            .firstOrError()
            .map { internalData -> DetailRequestOptions(internalData) }
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
    fun getData(id: Int): Single<T>
}