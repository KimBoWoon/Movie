package com.bowoon.data.repository

import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.MovieDetail
import com.bowoon.model.PeopleDetail
import com.bowoon.model.SearchData
import com.bowoon.model.CombineCredits
import com.bowoon.model.ExternalIds
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource
) : DetailRepository {
    override fun getMovieDetail(id: Int): Flow<MovieDetail> = flow {
        val language = datastore.getUserData().language
        val region = datastore.getUserData().region

        emit(apis.getMovieDetail(id = id, language = "$language-$region", region = region, includeImageLanguage = "$language,null"))
    }

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<SearchData> = flow {
        val language = "${datastore.getUserData().language}-${datastore.getUserData().region}"
        val region = datastore.getUserData().region
        val isAdult = datastore.getUserData().isAdult

        emit(apis.discoverMovie(
            releaseDateGte = releaseDateGte,
            releaseDateLte = releaseDateLte,
            includeAdult = isAdult,
            language = language,
            region = region
        ))
    }

    override fun getPeople(personId: Int): Flow<PeopleDetail> = flow {
        val language = datastore.getUserData().language
        val region = datastore.getUserData().region

        emit(apis.getPeopleDetail(personId = personId, language = "$language-$region", includeImageLanguage = "$language,null"))
    }

    override fun getCombineCredits(personId: Int): Flow<CombineCredits> = flow {
        val language = "${datastore.getUserData().language}-${datastore.getUserData().region}"

        emit(apis.getCombineCredits(personId = personId, language = language))
    }

    override fun getExternalIds(personId: Int): Flow<ExternalIds> = flow {
        emit(apis.getExternalIds(personId = personId))
    }
}