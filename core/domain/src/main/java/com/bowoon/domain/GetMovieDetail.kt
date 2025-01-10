package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MovieDetail
import com.bowoon.model.TMDBMovieDetailBackdrop
import com.bowoon.model.TMDBMovieDetailBelongsToCollection
import com.bowoon.model.TMDBMovieDetailCast
import com.bowoon.model.TMDBMovieDetailCredits
import com.bowoon.model.TMDBMovieDetailCrew
import com.bowoon.model.TMDBMovieDetailImages
import com.bowoon.model.TMDBMovieDetailLogo
import com.bowoon.model.TMDBMovieDetailPoster
import com.bowoon.model.TMDBMovieDetailSimilar
import com.bowoon.model.TMDBMovieDetailSimilarResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMovieDetail @Inject constructor(
    private val tmdbRepository: TMDBRepository,
    private val userDataRepository: UserDataRepository,
    private val databaseRepository: DatabaseRepository
) {
    operator fun invoke(id: Int): Flow<MovieDetail> =
        combine(
            tmdbRepository.getMovieDetail(id),
            userDataRepository.userData,
            databaseRepository.getMovies(),
            tmdbRepository.posterUrl
        ) { tmdbMovieInfo, userData, favoriteMovies, posterUrl ->
            MovieDetail(
                adult = tmdbMovieInfo.adult,
                alternativeTitles = tmdbMovieInfo.alternativeTitles,
                backdropPath = "$posterUrl${tmdbMovieInfo.backdropPath}",
                belongsToCollection = getBelongsToCollection(tmdbMovieInfo.belongsToCollection, posterUrl),
                budget = tmdbMovieInfo.budget,
                changes = tmdbMovieInfo.changes,
                credits = getCredits(tmdbMovieInfo.credits, posterUrl),
                genres = tmdbMovieInfo.genres?.fold("") { acc, tmdbMovieDetailGenre -> if (acc.isEmpty()) "${tmdbMovieDetailGenre.name}" else "$acc, ${tmdbMovieDetailGenre.name}" },
                homepage = tmdbMovieInfo.homepage,
                id = tmdbMovieInfo.id,
                images = getImages(tmdbMovieInfo.images, posterUrl),
                imdbId = tmdbMovieInfo.imdbId,
                keywords = tmdbMovieInfo.keywords,
                originCountry = tmdbMovieInfo.originCountry,
                originalLanguage = tmdbMovieInfo.originalLanguage,
                originalTitle = tmdbMovieInfo.originalTitle,
                overview = tmdbMovieInfo.overview,
                popularity = tmdbMovieInfo.popularity,
                posterPath = "${tmdbMovieInfo.posterPath}",
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
                similar = getSimilar(tmdbMovieInfo.similar, posterUrl),
                certification = tmdbMovieInfo.releases?.countries?.find { it.iso31661.equals(userData.region, true) }?.certification,
                favoriteMovies = favoriteMovies,
                posterUrl = posterUrl,
                isFavorite = favoriteMovies.find { it.id == tmdbMovieInfo.id } != null
            )
        }

    private fun getCredits(
        credits: TMDBMovieDetailCredits?,
        posterUrl: String
    ): TMDBMovieDetailCredits = TMDBMovieDetailCredits(
        cast = credits?.cast?.map {
            TMDBMovieDetailCast(
                adult = it.adult,
                castId = it.castId,
                character = it.character,
                creditId = it.creditId,
                gender = it.gender,
                id = it.id,
                knownForDepartment = it.knownForDepartment,
                name = it.name,
                order = it.order,
                originalName = it.originalName,
                popularity = it.popularity,
                profilePath = "$posterUrl${it.profilePath}"
            )
        },
        crew = credits?.crew?.map {
            TMDBMovieDetailCrew(
                adult = it.adult,
                creditId = it.creditId,
                department = it.department,
                gender = it.gender,
                id = it.id,
                job = it.job,
                knownForDepartment = it.knownForDepartment,
                name = it.name,
                originalName = it.originalName,
                popularity = it.popularity,
                profilePath = "$posterUrl${it.profilePath}"
            )
        }
    )

    private fun getSimilar(
        similar: TMDBMovieDetailSimilar?,
        posterUrl: String
    ): TMDBMovieDetailSimilar =
        TMDBMovieDetailSimilar(
            page = similar?.page,
            results = similar?.results?.map {
                TMDBMovieDetailSimilarResult(
                    adult = it.adult,
                    backdropPath = "$posterUrl${it.backdropPath}",
                    genreIds = it.genreIds,
                    id = it.id,
                    originalLanguage = it.originalLanguage,
                    originalTitle = it.originalTitle,
                    overview = it.overview,
                    popularity = it.popularity,
                    posterPath = "$posterUrl${it.posterPath}",
                    releaseDate = it.releaseDate,
                    title = it.title,
                    video = it.video,
                    voteAverage = it.voteAverage,
                    voteCount = it.voteCount
                )
            },
            totalPages = similar?.totalPages,
            totalResults = similar?.totalResults
        )

    private fun getImages(
        images: TMDBMovieDetailImages?,
        posterUrl: String
    ): TMDBMovieDetailImages =
        TMDBMovieDetailImages(
            backdrops = images?.backdrops?.map {
                TMDBMovieDetailBackdrop(
                    aspectRatio = it.aspectRatio,
                    filePath = "$posterUrl${it.filePath}",
                    height = it.height,
                    iso6391 = it.iso6391,
                    voteAverage = it.voteAverage,
                    voteCount = it.voteCount,
                    width = it.width
                )
            },
            logos = images?.logos?.map {
                TMDBMovieDetailLogo(
                    aspectRatio = it.aspectRatio,
                    filePath = "$posterUrl${it.filePath}",
                    height = it.height,
                    iso6391 = it.iso6391,
                    voteAverage = it.voteAverage,
                    voteCount = it.voteCount,
                    width = it.width
                )
            },
            posters = images?.posters?.map {
                TMDBMovieDetailPoster(
                    aspectRatio = it.aspectRatio,
                    filePath = "$posterUrl${it.filePath}",
                    height = it.height,
                    iso6391 = it.iso6391,
                    voteAverage = it.voteAverage,
                    voteCount = it.voteCount,
                    width = it.width
                )
            }
        )

    private fun getBelongsToCollection(
        belongsToCollection: TMDBMovieDetailBelongsToCollection?,
        posterUrl: String
    ): TMDBMovieDetailBelongsToCollection = TMDBMovieDetailBelongsToCollection(
        backdropPath = "$posterUrl${belongsToCollection?.backdropPath}",
        id = belongsToCollection?.id,
        name = belongsToCollection?.name,
        posterPath = "$posterUrl${belongsToCollection?.posterPath}"
    )
}