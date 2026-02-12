package com.bowoon.data.repository

import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.CombineCredits
import com.bowoon.model.ExternalIds
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.SearchData
import com.bowoon.model.Series
import com.bowoon.model.Tv
import com.bowoon.model.TvEpisode
import com.bowoon.model.TvSeasons
import com.bowoon.network.MovieNetworkDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource
) : DetailRepository {
    override fun getMovie(id: Int): Flow<Movie> = flow {
        val internalData = datastore.userData.first()

        emit(value = apis.getMovie(id = id, language = "${internalData.language}-${internalData.region}", region = internalData.region, includeImageLanguage = "${internalData.language},null"))
    }

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<SearchData> = flow {
        val internalData = datastore.userData.first()

        emit(
            value = apis.discoverMovie(
                releaseDateGte = releaseDateGte,
                releaseDateLte = releaseDateLte,
                includeAdult = internalData.isAdult,
                language = internalData.language,
                region = internalData.region
            )
        )
    }

    override fun getPeople(personId: Int): Flow<People> = flow {
        val internalData = datastore.userData.first()

        emit(value = apis.getPeopleDetail(personId = personId, language = "${internalData.language}-${internalData.region}", includeImageLanguage = "${internalData.language},null"))
    }

    override fun getCombineCredits(personId: Int): Flow<CombineCredits> = flow {
        val internalData = datastore.userData.first()

        emit(value = apis.getCombineCredits(personId = personId, language = "${internalData.language}-${internalData.region}"))
    }

    override fun getExternalIds(personId: Int): Flow<ExternalIds> = flow {
        emit(value = apis.getExternalIds(personId = personId))
    }

    override fun getMovieSeries(collectionId: Int): Flow<Series> = flow {
        val internalData = datastore.userData.first()

        apis.getMovieSeries(collectionId = collectionId, language = "${internalData.language}-${internalData.region}").let { movieSeries ->
            movieSeries.copy(
                parts = movieSeries.parts
                    ?.sortedBy { movie ->
                        movie.releaseDate
                            .takeIf { !it.isNullOrEmpty() }
                            .let { releaseDate ->
                                LocalDate.parse(releaseDate ?: "9999-12-31")
                            }
                }
            )
        }
    }

    override fun getTv(id: Int): Flow<Tv> = flow {
        val internalData = datastore.userData.first()
        val tv = apis.getTv(id = id, language = "${internalData.language}-${internalData.region}", includeImageLanguage = "${internalData.language},null")
        val seasonNumbers = tv.seasons?.mapNotNull { it.seasonNumber } ?: emptyList()
        val seasons = coroutineScope {
            seasonNumbers.map { seasonNumber ->
                async {
                    getTvSeasons(seriesId = id, seasonNumber = seasonNumber).first()
                }
            }.awaitAll()
        }
        val seasonMap = buildMap {
            seasons.forEach {
                it.name?.let { name ->
                    put(key = name, value = it)
                }
            }
        }

        emit(
            value = tv.copy(
                episode = seasons.find { it.seasonNumber == tv.numberOfSeasons },
                seasonList = seasonMap
            )
        )
    }

    override fun getTvSeasons(
        seriesId: Int,
        seasonNumber: Int
    ): Flow<TvSeasons> = flow {
        val internalData = datastore.userData.first()

        emit(value = apis.getTvSeasons(seriesId = seriesId, seasonNumber = seasonNumber, language = "${internalData.language}-${internalData.region}"))
    }

    override fun getTvEpisode(
        seriesId: Int,
        seasonNumber: Int,
        episodeNumber: Int
    ): Flow<TvEpisode> = flow {
        val internalData = datastore.userData.first()

        emit(value = apis.getTvEpisode(seriesId = seriesId, seasonNumber = seasonNumber, episodeNumber = episodeNumber, language = "${internalData.language}-${internalData.region}"))
    }
}