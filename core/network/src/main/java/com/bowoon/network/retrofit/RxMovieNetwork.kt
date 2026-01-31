package com.bowoon.network.retrofit

import com.bowoon.model.CertificationData
import com.bowoon.model.CombineCredits
import com.bowoon.model.Configuration
import com.bowoon.model.ExternalIds
import com.bowoon.model.Genres
import com.bowoon.model.Language
import com.bowoon.model.Movie
import com.bowoon.model.MovieResult
import com.bowoon.model.MovieReviews
import com.bowoon.model.People
import com.bowoon.model.Regions
import com.bowoon.model.SearchData
import com.bowoon.model.SearchKeywordData
import com.bowoon.model.Series
import com.bowoon.model.SimilarMovies
import com.bowoon.model.asExternalMovie
import com.bowoon.network.MovieRxNetworkDataSource
import com.bowoon.network.TMDBRxApis
import com.bowoon.network.di.RxNetwork
import com.bowoon.network.model.asExternalModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import javax.inject.Inject
import javax.inject.Singleton

@RxNetwork
@Singleton
class RxMovieNetwork @Inject constructor(
    tmdbUrl: String,
    client: OkHttpClient,
    serialization: Json,
    jsonMediaType: MediaType
): MovieRxNetworkDataSource {
    private val tmdbApis = Retrofit.Builder()
        .baseUrl(tmdbUrl)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(serialization.asConverterFactory(jsonMediaType))
        .client(client)
        .build()
        .create(TMDBRxApis::class.java)

    override fun getConfiguration(): Single<Configuration> =
        tmdbApis.getConfiguration()
            .map {
                it.asExternalModel()
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    override fun getCertification(): Single<CertificationData> =
        tmdbApis.getCertification()
            .map {
                it.asExternalModel()
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    override fun getGenres(language: String): Single<Genres> =
        tmdbApis.getGenres(language = language)
            .map {
                it.asExternalModel()
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    override fun getNowPlaying(
        language: String,
        region: String,
        page: Int
    ): Single<List<Movie>> = tmdbApis.getNowPlaying(language = language, region = region, page = page)
        .map {
            it.asExternalModel().results?.map(transform = MovieResult::asExternalMovie) ?: emptyList()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getUpcomingMovie(
        language: String,
        region: String,
        page: Int
    ): Single<List<Movie>> = tmdbApis.getUpcomingMovie(language = language, region = region, page = page)
        .map {
            it.asExternalModel().results?.map(transform = MovieResult::asExternalMovie) ?: emptyList()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun searchMovies(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): Single<SearchData> = tmdbApis.searchMovies(query = query, includeAdult = includeAdult, language = language, region = region, page = page)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun searchPeople(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): Single<SearchData> = tmdbApis.searchPeople(query = query, includeAdult = includeAdult, language = language, region = region, page = page)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun searchSeries(
        query: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int
    ): Single<SearchData> = tmdbApis.searchMovieSeries(query = query, includeAdult = includeAdult, language = language, region = region, page = page)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getMovieSeries(
        collectionId: Int,
        language: String
    ): Single<Series> = tmdbApis.getMovieSeries(collectionId = collectionId, language = language)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getMovie(
        id: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String,
        region: String
    ): Single<Movie> = tmdbApis.getMovie(id = id, appendToResponse = appendToResponse, language = language, includeImageLanguage = includeImageLanguage)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getSimilarMovies(
        id: Int,
        language: String,
        page: Int
    ): Single<SimilarMovies> = tmdbApis.getSimilarMovies(id = id, language = language, page = page)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String,
        includeAdult: Boolean,
        language: String,
        region: String,
        page: Int,
        sortBy: String,
        withReleaseType: String
    ): Single<SearchData> = tmdbApis.discoverMovie(releaseDateGte = releaseDateGte, releaseDateLte = releaseDateLte, includeAdult = includeAdult, language = language, region = region, page = page, sortBy = sortBy, withReleaseType = withReleaseType)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getAvailableLanguage(): Single<List<Language>> = tmdbApis.getAvailableLanguage()
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getAvailableRegion(): Single<Regions> = tmdbApis.getAvailableRegion()
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getPeopleDetail(
        personId: Int,
        appendToResponse: String,
        language: String,
        includeImageLanguage: String
    ): Single<People> = tmdbApis.getPeopleDetail(personId = personId, appendToResponse = appendToResponse, language = language, includeImageLanguage = includeImageLanguage)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getCombineCredits(
        personId: Int,
        language: String
    ): Single<CombineCredits> = tmdbApis.getCombineCredits(personId = personId, language = language)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getExternalIds(personId: Int): Single<ExternalIds> = tmdbApis.getExternalIds(personId = personId)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getSearchKeyword(
        query: String,
        page: Int
    ): Single<SearchKeywordData> = tmdbApis.getSearchKeyword(query = query, page = page)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

    override fun getMovieReviews(
        movieId: Int,
        language: String,
        page: Int
    ): Single<MovieReviews> = tmdbApis.getMovieReview(movieId = movieId, language = language, page = page)
        .map {
            it.asExternalModel()
        }.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
}