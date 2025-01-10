package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFavoriteMoviesUseCase @Inject constructor(
    private val tmdbRepository: TMDBRepository,
    private val databaseRepository: DatabaseRepository,
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<List<MovieDetail>> = combine(
        tmdbRepository.posterUrl,
        databaseRepository.getMovies(),
        userDataRepository.userData
    ) { posterUrl, favoriteMovies, userData ->
        favoriteMovies.map {
            MovieDetail(
                adult = it.adult,
                alternativeTitles = it.alternativeTitles,
                backdropPath = "${it.backdropPath}",
                belongsToCollection = it.belongsToCollection,
                budget = it.budget,
                changes = it.changes,
                credits = it.credits,
                genres = it.genres,
                homepage = it.homepage,
                id = it.id,
                images = it.images,
                imdbId = it.imdbId,
                keywords = it.keywords,
                originCountry = it.originCountry,
                originalLanguage = it.originalLanguage,
                originalTitle = it.originalTitle,
                overview = it.overview,
                popularity = it.popularity,
                posterPath = "${it.posterPath}",
                productionCountries = it.productionCountries,
                productionCompanies = it.productionCompanies,
                releaseDate = it.releases?.countries?.find { it.iso31661.equals(userData.region, true) }?.releaseDate,
                releases = it.releases,
                revenue = it.revenue,
                runtime = it.runtime,
                spokenLanguages = it.spokenLanguages,
                status = it.status,
                tagline = it.tagline,
                title = it.title,
                translations = it.translations,
                video = it.video,
                videos = it.videos,
                voteCount = it.voteCount,
                voteAverage = it.voteAverage,
                similar = it.similar,
                certification = it.releases?.countries?.find { it.iso31661.equals(userData.region, true) }?.certification,
                favoriteMovies = favoriteMovies,
                posterUrl = posterUrl,
                isFavorite = favoriteMovies.find { it.id == it.id } != null
            )
        }
    }
}