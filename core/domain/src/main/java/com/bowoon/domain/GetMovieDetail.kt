package com.bowoon.domain

import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMovieDetail @Inject constructor(
    private val tmdbRepository: TMDBRepository,
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(id: Int): Flow<MovieDetail> =
        combine(
            tmdbRepository.getMovieDetail(id),
            userDataRepository.userData
        ) { tmdbMovieInfo, userData ->
            MovieDetail(
                adult = tmdbMovieInfo.adult,
                alternativeTitles = tmdbMovieInfo.alternativeTitles,
                backdropPath = tmdbMovieInfo.backdropPath,
                belongsToCollection = tmdbMovieInfo.belongsToCollection,
                budget = tmdbMovieInfo.budget,
                changes = tmdbMovieInfo.changes,
                credits = tmdbMovieInfo.credits,
                genres = tmdbMovieInfo.genres?.fold("") { acc, tmdbMovieDetailGenre -> if (acc.isEmpty()) "${tmdbMovieDetailGenre.name}" else "$acc, ${tmdbMovieDetailGenre.name}" },
                homepage = tmdbMovieInfo.homepage,
                id = tmdbMovieInfo.id,
                images = tmdbMovieInfo.images,
                imdbId = tmdbMovieInfo.imdbId,
                keywords = tmdbMovieInfo.keywords,
                originCountry = tmdbMovieInfo.originCountry,
                originalLanguage = tmdbMovieInfo.originalLanguage,
                originalTitle = tmdbMovieInfo.originalTitle,
                overview = tmdbMovieInfo.overview,
                popularity = tmdbMovieInfo.popularity,
                posterPath = "https://image.tmdb.org/t/p/original${tmdbMovieInfo.posterPath}",
                productionCountries = tmdbMovieInfo.productionCountries,
                productionCompanies = tmdbMovieInfo.productionCompanies,
                releaseDate = tmdbMovieInfo.releases?.countries?.find { it.iso31661.equals(userData.region, true) }?.releaseDate,
                releases = tmdbMovieInfo.releases,
                revenue = tmdbMovieInfo.revenue,
                runtime = tmdbMovieInfo.runtime,
                spokenLanguages = tmdbMovieInfo.spokenLanguages,
                status = tmdbMovieInfo.status,
                tagline = tmdbMovieInfo.tagline,
                title = tmdbMovieInfo.title,
                translations = tmdbMovieInfo.translations,
                video = tmdbMovieInfo.video,
                videos = tmdbMovieInfo.videos,
                voteCount = tmdbMovieInfo.voteCount,
                voteAverage = tmdbMovieInfo.voteAverage,
                similar = tmdbMovieInfo.similar,
                certification = tmdbMovieInfo.releases?.countries?.find { it.iso31661.equals(userData.region, true) }?.certification,
                favoriteMovies = userData.favoriteMovies,
                isFavorite = userData.favoriteMovies.find {
                    it.title == tmdbMovieInfo.title &&
                            it.originalTitle == tmdbMovieInfo.originalTitle &&
                            it.releaseDate == tmdbMovieInfo.releases?.countries?.find { it.iso31661.equals(userData.region, true) }?.releaseDate
                } != null
            )
        }
}