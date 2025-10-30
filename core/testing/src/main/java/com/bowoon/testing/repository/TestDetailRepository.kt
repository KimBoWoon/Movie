package com.bowoon.testing.repository

import com.bowoon.data.repository.DetailRepository
import com.bowoon.model.CombineCredits
import com.bowoon.model.ExternalIds
import com.bowoon.model.Movie
import com.bowoon.model.MovieReviews
import com.bowoon.model.People
import com.bowoon.model.SearchData
import com.bowoon.model.Series
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.annotations.VisibleForTesting

class TestDetailRepository : DetailRepository {
    private val movie = MutableSharedFlow<Movie>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val movieSearchData = MutableSharedFlow<SearchData>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val peopleDetail = MutableSharedFlow<People>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val combineCredits = MutableSharedFlow<CombineCredits>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val externalIds = MutableSharedFlow<ExternalIds>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val movieSeries = MutableSharedFlow<Series>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val movieReviews = MutableSharedFlow<MovieReviews>(replay = 1, onBufferOverflow = DROP_OLDEST)

    override fun getMovie(id: Int): Flow<Movie> = movie

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<SearchData> = movieSearchData

    override fun getPeople(personId: Int): Flow<People> = peopleDetail

    override fun getCombineCredits(personId: Int): Flow<CombineCredits> = combineCredits

    override fun getExternalIds(personId: Int): Flow<ExternalIds> = externalIds

    override fun getMovieSeries(collectionId: Int): Flow<Series> = movieSeries

    override fun getMovieReviews(movieId: Int): Flow<MovieReviews> = movieReviews

    @VisibleForTesting
    fun setMovie(detail: Movie) {
        movie.tryEmit(detail)
    }

    @VisibleForTesting
    fun setDiscoverMovie(movie: SearchData) {
        movieSearchData.tryEmit(movie)
    }

    @VisibleForTesting
    fun setPeopleDetail(people: People) {
        peopleDetail.tryEmit(people)
    }

    @VisibleForTesting
    fun setCombineCredits(credits: CombineCredits) {
        combineCredits.tryEmit(credits)
    }

    @VisibleForTesting
    fun setExternalIds(ids: ExternalIds) {
        externalIds.tryEmit(ids)
    }

    @VisibleForTesting
    fun setMovieSeries(movieSeries: Series) {
        this@TestDetailRepository.movieSeries.tryEmit(movieSeries)
    }

    @VisibleForTesting
    fun setMovieReviews(movieReviews: MovieReviews) {
        this@TestDetailRepository.movieReviews.tryEmit(movieReviews)
    }
}