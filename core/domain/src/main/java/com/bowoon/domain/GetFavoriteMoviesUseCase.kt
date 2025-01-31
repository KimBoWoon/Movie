package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFavoriteMoviesUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val userDataRepository: UserDataRepository,
    private val myDataRepository: MyDataRepository
) {
    operator fun invoke(): Flow<List<MovieDetail>> = combine(
        myDataRepository.posterUrl,
        databaseRepository.getMovies(),
        userDataRepository.userData
    ) { posterUrl, favoriteMovies, userData ->
        favoriteMovies.map { movie ->
            MovieDetail(
                adult = movie.adult,
                alternativeTitles = movie.alternativeTitles,
                backdropPath = "${movie.backdropPath}",
                belongsToCollection = movie.belongsToCollection,
                budget = movie.budget,
                credits = movie.credits,
                genres = movie.genres,
                homepage = movie.homepage,
                id = movie.id,
                images = movie.images,
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
                releaseDate = movie.releases?.countries?.find {
                    it.iso31661.equals(
                        userData.region,
                        true
                    )
                }?.releaseDate,
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
                certification = movie.releases?.countries?.find {
                    it.iso31661.equals(
                        userData.region,
                        true
                    )
                }?.certification,
                favoriteMovies = favoriteMovies,
                posterUrl = posterUrl,
                isFavorite = favoriteMovies.find { it.id == movie.id } != null
            )
        }
    }
}