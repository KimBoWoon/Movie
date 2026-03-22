package com.cheeke.surfy.data.repository

import com.cheeke.surfy.model.CombineCredits
import com.cheeke.surfy.model.ExternalIds
import com.cheeke.surfy.model.People
import com.cheeke.surfy.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface PeopleDetailRepository : DetailRepository<People> {
    override fun getData(id: Int): Flow<People>
    fun getCombineCredits(personId: Int): Flow<CombineCredits>
    fun getExternalIds(personId: Int): Flow<ExternalIds>
}

class PeopleDetailRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val requestOptionsProvider: DetailRequestOptionsProvider
) : PeopleDetailRepository {
    override fun getData(id: Int): Flow<People> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = apis.getPeopleDetail(personId = id, language = "${internalData.language}-${internalData.region}", includeImageLanguage = "${internalData.language},null"))
    }

    override fun getCombineCredits(personId: Int): Flow<CombineCredits> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = apis.getCombineCredits(personId = personId, language = "${internalData.language}-${internalData.region}"))
    }

    override fun getExternalIds(personId: Int): Flow<ExternalIds> = flow {
        emit(value = apis.getExternalIds(personId = personId))
    }
}