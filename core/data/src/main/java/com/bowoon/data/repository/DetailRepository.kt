package com.bowoon.data.repository

import com.bowoon.model.CombineCredits
import com.bowoon.model.ExternalIds
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.SearchData
import com.bowoon.model.Series
import com.bowoon.model.Tv
import com.bowoon.model.TvEpisode
import com.bowoon.model.TvSeasons
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
    fun getTv(id: Int): Flow<Tv>
    fun getTvSeasons(seriesId: Int, seasonNumber: Int): Flow<TvSeasons>
    fun getTvEpisode(seriesId: Int, seasonNumber: Int, episodeNumber: Int): Flow<TvEpisode>
}