package com.cheeke.surfy.testing.repository

import com.cheeke.surfy.data.repository.DetailRepository
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
    private val tv = MutableSharedFlow<Tv>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val tvSeasons = MutableSharedFlow<TvSeasons>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val tvEpisode = MutableSharedFlow<TvEpisode>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val watchProvider = MutableSharedFlow<MovieWatchProvider>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val imageList = MutableSharedFlow<ImageList>(replay = 1, onBufferOverflow = DROP_OLDEST)

    override fun getMovie(id: Int): Flow<Movie> = movie

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<SearchData> = movieSearchData

    override fun getPeople(personId: Int): Flow<People> = peopleDetail

    override fun getCombineCredits(personId: Int): Flow<CombineCredits> = combineCredits

    override fun getExternalIds(personId: Int): Flow<ExternalIds> = externalIds

    override fun getMovieSeries(collectionId: Int): Flow<Series> = movieSeries

    override fun getTv(id: Int): Flow<Tv> = tv

    override fun getTvSeasons(
        seriesId: Int,
        seasonNumber: Int
    ): Flow<TvSeasons> = tvSeasons

    override fun getTvEpisode(
        seriesId: Int,
        seasonNumber: Int,
        episodeNumber: Int
    ): Flow<TvEpisode> = tvEpisode

    override fun getMovieSeriesImageList(collectionId: Int): Flow<ImageList> = imageList

    override fun getMovieWatchProviders(movieId: Int): Flow<MovieWatchProvider> = watchProvider

    @VisibleForTesting
    fun setMovie(detail: Movie) {
        movie.tryEmit(value = detail)
    }

    @VisibleForTesting
    fun setDiscoverMovie(movie: SearchData) {
        movieSearchData.tryEmit(value = movie)
    }

    @VisibleForTesting
    fun setPeopleDetail(people: People) {
        peopleDetail.tryEmit(value = people)
    }

    @VisibleForTesting
    fun setCombineCredits(credits: CombineCredits) {
        combineCredits.tryEmit(value = credits)
    }

    @VisibleForTesting
    fun setExternalIds(ids: ExternalIds) {
        externalIds.tryEmit(value = ids)
    }

    @VisibleForTesting
    fun setMovieSeries(movieSeries: Series) {
        this@TestDetailRepository.movieSeries.tryEmit(value = movieSeries)
    }

    @VisibleForTesting
    fun setTv(tv: Tv) {
        this@TestDetailRepository.tv.tryEmit(value = tv)
    }

    @VisibleForTesting
    fun setTvSeason(tvSeasons: TvSeasons) {
        this@TestDetailRepository.tvSeasons.tryEmit(value = tvSeasons)
    }

    @VisibleForTesting
    fun setTvEpisode(tvEpisode: TvEpisode) {
        this@TestDetailRepository.tvEpisode.tryEmit(value = tvEpisode)
    }

    @VisibleForTesting
    fun setImageList(imageList: ImageList) {
        this@TestDetailRepository.imageList.tryEmit(value = imageList)
    }
}