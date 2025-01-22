package com.bowoon.data.repository

import com.bowoon.data.util.suspendRunCatching
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.MainMenu
import com.bowoon.model.MainMovie
import com.bowoon.model.UpComingResult
import com.bowoon.model.tmdb.TMDBNowPlayingResult
import kotlinx.coroutines.flow.first
import org.threeten.bp.LocalDate
import javax.inject.Inject

class MainMenuRepositoryImpl @Inject constructor(
    private val tmdbRepository: TMDBRepository,
    private val datastore: InternalDataSource,
    private val myDataRepository: MyDataRepository
) : MainMenuRepository {
    override suspend fun syncWith(isForce: Boolean): Boolean =
        suspendRunCatching {
            val date = datastore.getMainOfDate()
            val targetDt = LocalDate.now().minusDays(1)
            val updateDate = when (date.isNotEmpty()) {
                true -> LocalDate.parse(date)
                false -> LocalDate.MIN
            }
            val isUpdate = targetDt.isAfter(updateDate)

            if (isUpdate || isForce) {
                val posterUrl = myDataRepository.posterUrl.first()
                val nowPlaying = tmdbRepository.getNowPlaying()
                val upcomingMovies = tmdbRepository.getUpcomingMovies()

                createMainMenu(posterUrl, nowPlaying, upcomingMovies).also {
                    datastore.updateMainMenu(it)
                    datastore.updateMainOfDate(targetDt.toString())
                }
            }
        }.isSuccess

    private fun createMainMenu(
        posterUrl: String,
        nowPlaying: List<TMDBNowPlayingResult>,
        upComing: List<UpComingResult>
    ): MainMenu = MainMenu(
        nowPlaying = nowPlaying.map { tmdbMovie ->
            MainMovie(
                genreIds = tmdbMovie.genreIds,
                id = tmdbMovie.id,
                originalLanguage = tmdbMovie.originalLanguage,
                originalTitle = tmdbMovie.originalTitle,
                overview = tmdbMovie.overview,
                popularity = tmdbMovie.popularity,
                posterPath = "$posterUrl${tmdbMovie.posterPath}",
                releaseDate = tmdbMovie.releaseDate,
                title = tmdbMovie.title,
                voteAverage = tmdbMovie.voteAverage,
                voteCount = tmdbMovie.voteCount
            )
        },
        upcomingMovies = upComing.map { upComingMovie ->
            MainMovie(
                genreIds = upComingMovie.genreIds,
                id = upComingMovie.id,
                title = upComingMovie.title,
                originalLanguage = upComingMovie.originalLanguage,
                originalTitle = upComingMovie.originalTitle,
                overview = upComingMovie.overview,
                popularity = upComingMovie.popularity,
                posterPath = "$posterUrl${upComingMovie.posterPath}",
                releaseDate = upComingMovie.releaseDate,
                voteAverage = upComingMovie.voteAverage,
                voteCount = upComingMovie.voteCount,
            )
        }
    )
}