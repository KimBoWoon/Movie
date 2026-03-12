package com.cheeke.surfy.data.repository

import com.cheeke.surfy.model.CombineCredits
import com.cheeke.surfy.model.ExternalIds
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.MovieWatchProvider
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.SearchData
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeasons
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
    fun getMovieSeriesImageList(collectionId: Int): Flow<ImageList>
    fun getTv(id: Int): Flow<Tv>
    fun getTvSeasons(seriesId: Int, seasonNumber: Int): Flow<TvSeasons>
    fun getTvEpisode(seriesId: Int, seasonNumber: Int, episodeNumber: Int): Flow<TvEpisode>
    fun getMovieWatchProviders(movieId: Int): Flow<MovieWatchProvider>
}