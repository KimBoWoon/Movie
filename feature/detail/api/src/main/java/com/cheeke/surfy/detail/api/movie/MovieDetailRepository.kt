package com.cheeke.surfy.detail.api.movie

import com.cheeke.surfy.detail.api.DetailRepository
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.MovieWatchProvider
import com.cheeke.surfy.model.Series
import kotlinx.coroutines.flow.Flow

interface MovieDetailRepository : DetailRepository<Movie> {
    override fun getData(id: Int): Flow<Movie>
    fun getMovieSeries(collectionId: Int): Flow<Series>
    fun getMovieSeriesImageList(collectionId: Int): Flow<ImageList>
    fun getMovieWatchProviders(movieId: Int): Flow<MovieWatchProvider>
}