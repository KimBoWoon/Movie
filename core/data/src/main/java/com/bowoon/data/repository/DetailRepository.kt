package com.bowoon.data.repository

import com.bowoon.model.MovieDetail
import com.bowoon.model.PeopleDetail
import com.bowoon.model.SearchData
import com.bowoon.model.CombineCredits
import com.bowoon.model.ExternalIds
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun getMovieDetail(id: Int): Flow<MovieDetail>
    fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<SearchData>
    fun getPeople(personId: Int): Flow<PeopleDetail>
    fun getCombineCredits(personId: Int): Flow<CombineCredits>
    fun getExternalIds(personId: Int): Flow<ExternalIds>
}