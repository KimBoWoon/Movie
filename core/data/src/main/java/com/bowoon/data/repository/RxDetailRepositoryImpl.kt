package com.bowoon.data.repository

import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.CombineCredits
import com.bowoon.model.ExternalIds
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.SearchData
import com.bowoon.model.Series
import com.bowoon.network.MovieRxNetworkDataSource
import com.bowoon.network.di.RxNetwork
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx3.asObservable
import java.time.LocalDate
import javax.inject.Inject

class RxDetailRepositoryImpl @Inject constructor(
    @param:RxNetwork private val apis: MovieRxNetworkDataSource,
    private val datastore: InternalDataSource
) : RxDetailRepository {
    override fun getMovie(id: Int): Single<Movie> {
        val language = datastore.userData.map { it.language }.asObservable().blockingFirst()
        val region = datastore.userData.map { it.region }.asObservable().blockingFirst()

        return apis.getMovie(id = id, language = "$language-$region", region = region, includeImageLanguage = "$language,null")
    }

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Single<SearchData> {
        val language = datastore.userData.map { "${it.language}-${it.region}" }.asObservable().blockingFirst()
        val region = datastore.userData.map { it.region }.asObservable().blockingFirst()
        val isAdult = datastore.userData.map { it.isAdult }.asObservable().blockingFirst()

        return apis.discoverMovie(
            releaseDateGte = releaseDateGte,
            releaseDateLte = releaseDateLte,
            includeAdult = isAdult,
            language = language,
            region = region
        )
    }

    override fun getPeople(personId: Int): Single<People> {
        val language = datastore.userData.map { it.language }.asObservable().blockingFirst()
        val region = datastore.userData.map { it.region }.asObservable().blockingFirst()

        return apis.getPeopleDetail(personId = personId, language = "$language-$region", includeImageLanguage = "$language,null")
    }

    override fun getCombineCredits(personId: Int): Single<CombineCredits> {
        val language = datastore.userData.map { "${it.language}-${it.region}" }.asObservable().blockingFirst()

        return apis.getCombineCredits(personId = personId, language = language)
    }

    override fun getExternalIds(personId: Int): Single<ExternalIds> = apis.getExternalIds(personId = personId)

    override fun getMovieSeries(collectionId: Int): Single<Series> = apis.getMovieSeries(collectionId = collectionId)
        .map { series ->
            series.copy(
                parts = series.parts
                    ?.sortedBy { movie ->
                        movie.releaseDate
                            .takeIf { !it.isNullOrEmpty() }
                            .let { releaseDate ->
                                LocalDate.parse(releaseDate ?: "9999-12-31")
                            }
                    }
            )
        }
//    override fun getMovie(id: Int): Flow<Movie> = flow {
//        val language = datastore.getLanguage()
//        val region = datastore.getRegion()
//
//        emit(apis.getMovie(id = id, language = "$language-$region", region = region, includeImageLanguage = "$language,null"))
//    }
//
//    override fun discoverMovie(
//        releaseDateGte: String,
//        releaseDateLte: String
//    ): Flow<SearchData> = flow {
//        val language = "${datastore.getLanguage()}-${datastore.getRegion()}"
//        val region = datastore.getRegion()
//        val isAdult = datastore.getIsAdult()
//
//        emit(
//            apis.discoverMovie(
//                releaseDateGte = releaseDateGte,
//                releaseDateLte = releaseDateLte,
//                includeAdult = isAdult,
//                language = language,
//                region = region
//            )
//        )
//    }
//
//    override fun getPeople(personId: Int): Flow<People> = flow {
//        val language = datastore.getLanguage()
//        val region = datastore.getRegion()
//
//        emit(apis.getPeopleDetail(personId = personId, language = "$language-$region", includeImageLanguage = "$language,null"))
//    }
//
//    override fun getCombineCredits(personId: Int): Flow<CombineCredits> = flow {
//        val language = "${datastore.getLanguage()}-${datastore.getRegion()}"
//
//        emit(apis.getCombineCredits(personId = personId, language = language))
//    }
//
//    override fun getExternalIds(personId: Int): Flow<ExternalIds> = flow {
//        emit(apis.getExternalIds(personId = personId))
//    }
//
//    override fun getMovieSeries(collectionId: Int): Flow<Series> = flow {
//        val language = "${datastore.getLanguage()}-${datastore.getRegion()}"
//
//        apis.getMovieSeries(collectionId = collectionId, language = language).let { movieSeries ->
//            movieSeries.copy(
//                parts = movieSeries.parts
//                    ?.sortedBy { movie ->
//                        movie.releaseDate
//                            .takeIf { !it.isNullOrEmpty() }
//                            .let { releaseDate ->
//                                LocalDate.parse(releaseDate ?: "9999-12-31")
//                            }
//                    }
//            )
//        }.run { emit(this) }
//    }
}