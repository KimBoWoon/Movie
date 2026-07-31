//package com.cheeke.surfy.detail.api
//
//import android.annotation.SuppressLint
//import androidx.paging.PagingSource
//import com.cheeke.surfy.model.Media
//import com.cheeke.surfy.model.Review
//import com.cheeke.surfy.model.SearchKeyword
//import com.cheeke.surfy.model.SearchType
//import com.cheeke.surfy.model.SimilarMedia
//import com.cheeke.surfy.model.TrendingMediaResult
//import kotlin.collections.orEmpty
//
//class TestPagingRepository : PagingRepository {
//    @SuppressLint("VisibleForTests")
//    private val testPagingSource = (0..100).map {
//        SimilarMedia(
////            genres = listOf(Genre(id = it)),
//            releaseDate = "releaseDate_$it",
//            title = "title_$it",
//            adult = true,
//            id = it,
//            posterPath = "/imagePath_$it.png"
//        )
//    }.asPagingSourceFactory().invoke()
//
//    @SuppressLint("VisibleForTests")
//    override fun getSearchPagingSource(
//        type: SearchType,
//        query: String,
//        language: String,
//        region: String,
//        isAdult: Boolean
//    ): PagingSource<Int, Media> {
//        return (when (type) {
//            SearchType.MOVIE -> movieSearchTestData.results
//            SearchType.MULTI -> movieSearchTestData.results
//            SearchType.TV -> tvSearchTestData.results
//            SearchType.PEOPLE -> peopleSearchTestData.results
//            SearchType.SERIES -> seriesSearchTestData.results
//        }.orEmpty()).asPagingSourceFactory().invoke()
//    }
//
//    override fun getSimilarMoviePagingSource(
//        id: Int,
//        language: String,
//        region: String
//    ): PagingSource<Int, SimilarMedia> = testPagingSource
//
//    @SuppressLint("VisibleForTests")
//    override fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword> =
//        testRecommendedKeyword.asPagingSourceFactory().invoke()
//
//    @SuppressLint("VisibleForTests")
//    override fun getMovieReviews(
//        movieId: Int, language: String, region: String
//    ): PagingSource<Int, Review> = testMovieReviews.asPagingSourceFactory().invoke()
//
//    @SuppressLint("VisibleForTests")
//    override fun getSimilarTvPagingSource(
//        id: Int,
//        language: String,
//        region: String
//    ): PagingSource<Int, SimilarMedia> = (0..100).map {
//        SimilarMedia(
////            genres = listOf(Genre(id = it)),
//            firstAirDate = "firstAirDate_$it",
//            title = "title_$it",
//            adult = true,
//            id = it,
//            posterPath = "/imagePath_$it.png"
//        )
//    }.asPagingSourceFactory().invoke()
//
//    @SuppressLint("VisibleForTests")
//    override fun getTvReviews(
//        seriesId: Int,
//        language: String,
//        region: String
//    ): PagingSource<Int, Review> = testTvReviews.asPagingSourceFactory().invoke()
//
//    @SuppressLint("VisibleForTests")
//    override fun getTrendingMovie(
//        timeWindow: String,
//        language: String
//    ): PagingSource<Int, TrendingMediaResult> = testTrendingMovie.results?.asPagingSourceFactory()?.invoke()!!
//
//    @SuppressLint("VisibleForTests")
//    override fun getTrendingPeople(
//        timeWindow: String,
//        language: String
//    ): PagingSource<Int, TrendingMediaResult> = testTrendingPeople.results?.asPagingSourceFactory()?.invoke()!!
//
//    @SuppressLint("VisibleForTests")
//    override fun getTrendingTv(
//        timeWindow: String,
//        language: String
//    ): PagingSource<Int, TrendingMediaResult> = testTrendingTv.results?.asPagingSourceFactory()?.invoke()!!
//}