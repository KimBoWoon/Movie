package com.cheeke.surfy.data.repository

import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.model.CombineCredits
import com.cheeke.surfy.model.ExternalIds
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.MovieWatchProvider
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.SearchData
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.model.TvEpisode
import com.cheeke.surfy.model.TvSeasons
import com.cheeke.surfy.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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

        emit(value = apis.getMovieSeries(collectionId = collectionId, language = "${internalData.language}-${internalData.region}"))
    }

    override fun getTv(id: Int): Flow<Tv> = flow {
        val internalData = datastore.userData.first()
        val tv = apis.getTv(id = id, language = "${internalData.language}-${internalData.region}", includeImageLanguage = "${internalData.language},null")
        emit(value = tv)
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

    override fun getMovieWatchProviders(movieId: Int): Flow<MovieWatchProvider> = flow {
        emit(value = apis.getMovieWatchProvider(movieId = movieId))
    }
}