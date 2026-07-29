package com.cheeke.surfy.detail.impl.people

import com.cheeke.surfy.detail.api.DetailRequestOptionsProvider
import com.cheeke.surfy.detail.api.people.PeopleDetailRepository
import com.cheeke.surfy.model.CombineCredits
import com.cheeke.surfy.model.ExternalIds
import com.cheeke.surfy.model.People
import com.cheeke.surfy.network.api.PeopleRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PeopleDetailRepositoryImpl @Inject constructor(
    private val apis: PeopleRemoteDataSource,
    private val requestOptionsProvider: DetailRequestOptionsProvider
) : PeopleDetailRepository {
    override fun getData(id: Int): Flow<People> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = apis.getPeopleDetail(personId = id, language = internalData.languageTag, includeImageLanguage = internalData.includeImageLanguage))
    }

    override fun getCombineCredits(personId: Int): Flow<CombineCredits> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = apis.getCombineCredits(personId = personId, language = internalData.languageTag))
    }

    override fun getExternalIds(personId: Int): Flow<ExternalIds> = flow {
        emit(value = apis.getExternalIds(personId = personId))
    }
}