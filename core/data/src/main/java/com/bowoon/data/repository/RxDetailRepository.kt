package com.bowoon.data.repository

import com.bowoon.model.CombineCredits
import com.bowoon.model.ExternalIds
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.SearchData
import com.bowoon.model.Series
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

interface RxDetailRepository {
    fun getMovie(id: Int): Single<Movie>
    fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Single<SearchData>
    fun getPeople(personId: Int): Single<People>
    fun getCombineCredits(personId: Int): Single<CombineCredits>
    fun getExternalIds(personId: Int): Single<ExternalIds>
    fun getMovieSeries(collectionId: Int): Single<Series>
}