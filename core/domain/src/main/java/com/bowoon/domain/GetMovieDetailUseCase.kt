package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.BelongsToCollection
import com.bowoon.model.Cast
import com.bowoon.model.Credits
import com.bowoon.model.Crew
import com.bowoon.model.DetailImage
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieDetailImages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val detailRepository: DetailRepository,
    private val userDataRepository: UserDataRepository,
    private val databaseRepository: DatabaseRepository,
    private val myDataRepository: MyDataRepository
) {
    operator fun invoke(id: Int): Flow<MovieDetail> =
        combine(
            detailRepository.getMovieDetail(id),
            userDataRepository.userData,
            databaseRepository.getMovies(),
            myDataRepository.posterUrl
        ) { movie, userData, favoriteMovies, posterUrl ->
            MovieDetail(
                adult = movie.adult,
                autoPlayTrailer = userData.autoPlayTrailer,
                alternativeTitles = movie.alternativeTitles,
                backdropPath = "$posterUrl${movie.backdropPath}",
                belongsToCollection = getBelongsToCollection(movie.belongsToCollection, posterUrl),
                budget = movie.budget,
                credits = getCredits(movie.credits, posterUrl),
                genres = movie.genres,
                homepage = movie.homepage,
                id = movie.id,
                images = getImages(movie.images, posterUrl),
                imdbId = movie.imdbId,
                keywords = movie.keywords,
                originCountry = movie.originCountry,
                originalLanguage = movie.originalLanguage,
                originalTitle = movie.originalTitle,
                overview = movie.overview,
                popularity = movie.popularity,
                posterPath = "${movie.posterPath}",
                productionCountries = movie.productionCountries,
                productionCompanies = movie.productionCompanies,
                releaseDate = movie.releases?.countries?.find { it.iso31661.equals(userData.region, true) }?.releaseDate,
                releases = movie.releases,
                revenue = movie.revenue,
                runtime = movie.runtime,
                spokenLanguages = movie.spokenLanguages,
                status = movie.status,
                tagline = movie.tagline,
                title = movie.title,
                translations = movie.translations,
                video = movie.video,
                videos = movie.videos,
                voteCount = movie.voteCount,
                voteAverage = movie.voteAverage,
                certification = movie.releases?.countries?.find { it.iso31661.equals(userData.region, true) }?.certification,
                favoriteMovies = favoriteMovies,
                posterUrl = posterUrl,
                isFavorite = favoriteMovies.find { it.id == movie.id } != null
            )
        }

    private fun getCredits(
        credits: Credits?,
        posterUrl: String
    ): Credits = Credits(
        cast = credits?.cast?.map {
            Cast(
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
            Crew(
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

    private fun getImages(
        images: MovieDetailImages?,
        posterUrl: String
    ): MovieDetailImages =
        MovieDetailImages(
            backdrops = images?.backdrops?.map {
                DetailImage(
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
                DetailImage(
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
                DetailImage(
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
        belongsToCollection: BelongsToCollection?,
        posterUrl: String
    ): BelongsToCollection = BelongsToCollection(
        backdropPath = "$posterUrl${belongsToCollection?.backdropPath}",
        id = belongsToCollection?.id,
        name = belongsToCollection?.name,
        posterPath = "$posterUrl${belongsToCollection?.posterPath}"
    )
}