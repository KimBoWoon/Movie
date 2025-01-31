package com.bowoon.data.repository

import com.bowoon.model.CombineCredits
import com.bowoon.model.ExternalIds
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieSearchData
import com.bowoon.model.PeopleDetailData
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun getMovieDetail(id: Int): Flow<MovieDetail>
    fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<MovieSearchData>
    fun getPeople(personId: Int): Flow<PeopleDetailData>
    fun getCombineCredits(personId: Int): Flow<CombineCredits>
    fun getExternalIds(personId: Int): Flow<ExternalIds>
}