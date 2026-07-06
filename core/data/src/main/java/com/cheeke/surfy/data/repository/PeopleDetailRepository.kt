package com.cheeke.surfy.data.repository

import com.cheeke.surfy.model.CombineCredits
import com.cheeke.surfy.model.ExternalIds
import com.cheeke.surfy.model.People
import com.cheeke.surfy.network.PeopleRemoteDataSource
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

interface PeopleDetailRepository : DetailRepository<People> {
    override fun getData(id: Int): Single<People>
    fun getCombineCredits(personId: Int): Single<CombineCredits>
    fun getExternalIds(personId: Int): Single<ExternalIds>
}

class PeopleDetailRepositoryImpl @Inject constructor(
    private val apis: PeopleRemoteDataSource,
    private val requestOptionsProvider: DetailRequestOptionsProvider
) : PeopleDetailRepository {
    override fun getData(id: Int): Single<People> = requestOptionsProvider.current()
        .flatMap {
            apis.getPeopleDetail(
                personId = id,
                language = it.languageTag,
                includeImageLanguage = it.includeImageLanguage
            )
        }

    override fun getCombineCredits(personId: Int): Single<CombineCredits> = requestOptionsProvider.current()
        .flatMap {
            apis.getCombineCredits(
                personId = personId,
                language = it.languageTag
            )
        }

    override fun getExternalIds(personId: Int): Single<ExternalIds> =
        apis.getExternalIds(personId = personId)
}