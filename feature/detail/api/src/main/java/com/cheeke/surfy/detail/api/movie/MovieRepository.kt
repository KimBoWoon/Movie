package com.cheeke.surfy.detail.api.movie

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.cheeke.surfy.detail.api.DataBaseRepository
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.Review
import com.cheeke.surfy.model.SimilarMedia
import kotlinx.coroutines.flow.Flow

interface MovieRepository : DataBaseRepository<Movie> {
    fun getUpComingMovies(): Flow<PagingData<Movie>>
    fun getNowPlayingMovies(): Flow<PagingData<Movie>>
    fun getPopularMovies(): Flow<List<Movie>>
    suspend fun getNextWeekReleaseMovies(): List<Movie>
    fun getSimilarMoviePagingSource(
        id: Int,
        language: String,
        region: String
    ): PagingSource<Int, SimilarMedia>

    fun getMovieReviews(movieId: Int, language: String, region: String): PagingSource<Int, Review>
}