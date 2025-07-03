package com.bowoon.data.repository

import com.bowoon.model.CombineCredits
import com.bowoon.model.ExternalIds
import com.bowoon.model.Movie
import com.bowoon.model.Series
import com.bowoon.model.People
import com.bowoon.model.SearchData
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun getMovie(id: Int): Flow<Movie>
    fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<SearchData>
    fun getPeople(personId: Int): Flow<People>
    fun getCombineCredits(personId: Int): Flow<CombineCredits>
    fun getExternalIds(personId: Int): Flow<ExternalIds>
    fun getMovieSeries(collectionId: Int): Flow<Series>
}