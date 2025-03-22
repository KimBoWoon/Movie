package com.bowoon.testing.model

import com.bowoon.model.Certification
import com.bowoon.model.CertificationData
import com.bowoon.model.CertificationMap
import com.bowoon.model.Configuration
import com.bowoon.model.Images
import com.bowoon.model.LanguageItem
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import com.bowoon.model.MovieGenre
import com.bowoon.model.MovieGenreList
import com.bowoon.model.MovieSearchData
import com.bowoon.model.MovieSearchItem
import com.bowoon.model.PeopleSearchData
import com.bowoon.model.PeopleSearchItem
import com.bowoon.model.Region
import com.bowoon.model.RegionList
import com.bowoon.model.SimilarMovie
import com.bowoon.model.SimilarMovies
import java.util.concurrent.atomic.AtomicInteger

val certificationTestData = CertificationData(
    certifications = CertificationMap(
        certifications = mapOf(
            "en" to listOf(
                Certification(certification = "1", meaning = "1", order = 0),
                Certification(certification = "2", meaning = "2", order = 1)
            ),
            "ko" to listOf(
                Certification(certification = "1", meaning = "1", order = 0),
                Certification(certification = "2", meaning = "2", order = 1)
            )
        )
    )
)

val languageListTestData = listOf(
    LanguageItem(englishName = "en", iso6391 = "en", name = "en", isSelected = false),
    LanguageItem(englishName = "ko", iso6391 = "ko", name = "ko", isSelected = true)
)

val regionTestData = RegionList(
    results = listOf(
        Region(englishName = "en", iso31661 = "en", nativeName = "en", isSelected = false),
        Region(englishName = "ko", iso31661 = "ko", nativeName = "ko", isSelected = true)
    )
)

val genreListTestData = MovieGenreList(
    genres = listOf(
        MovieGenre(
            id = 0,
            name = "genre_1"
        ),
        MovieGenre(
            id = 1,
            name = "genre_2"
        ),
        MovieGenre(
            id = 3,
            name = "genre_3"
        )
    )
)

val configurationTestData = Configuration(
    changeKeys = listOf(),
    images = Images(
        baseUrl = "https://",
        secureBaseUrl = "https://",
        posterSizes = listOf("w92", "w182", "w342", "w540", "w720", "original")
    )
)

val movieSearchTestData = MovieSearchData(
    page = 1,
    results = listOf(
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
        MovieFactory.createMovieItem(),
    ),
    totalPages = 3,
    totalResults = 50
)

val peopleSearchTestData = PeopleSearchData(
    page = 1,
    results = listOf(
        MovieFactory.createPeopleItem(),
        MovieFactory.createPeopleItem(),
        MovieFactory.createPeopleItem(),
        MovieFactory.createPeopleItem(),
        MovieFactory.createPeopleItem()
    ),
    totalPages = 1,
    totalResults = 5
)

val similarMoviesTestData = SimilarMovies(
    page = 1,
    results = listOf(
        MovieFactory.createSimilarMovie(),
        MovieFactory.createSimilarMovie(),
        MovieFactory.createSimilarMovie(),
        MovieFactory.createSimilarMovie(),
        MovieFactory.createSimilarMovie()
    ),
    totalPages = 1,
    totalResults = 5
)

val nowPlayingMoviesTestData = listOf(Movie(id = 0, title = "nowPlaying_1", posterPath = "/nowPlaying_1.png"))
val upcomingMoviesTestData = listOf(Movie(id = 0, title = "upcomingMovie_1", posterPath = "/upcomingMovie_1.png"))
val mainMenuTestData = MainMenu(
    nowPlaying = listOf(Movie(id = 0, title = "nowPlaying_1", posterPath = "posterUrl/nowPlaying_1.png")),
    upcomingMovies = listOf(Movie(id = 0, title = "upcomingMovie_1", posterPath = "posterUrl/upcomingMovie_1.png"))
)

object MovieFactory {
    private val counter = AtomicInteger(0)

    fun createMovieItem(): MovieSearchItem {
        val id = counter.incrementAndGet()
        val movie = MovieSearchItem(
            id = id,
            title = "title_$id",
            tmdbId = id,
            searchTitle = "title_$id",
            imagePath = "/imagePath_$id.png",
            posterPath = "/imagePath_$id.png"
        )
        return movie
    }

    fun createPeopleItem(): PeopleSearchItem {
        val id = counter.incrementAndGet()
        val people = PeopleSearchItem(
            id = id,
            name = "name_$id",
            tmdbId = id,
            searchTitle = "name_$id",
            imagePath = "/imagePath_$id.png",
            profilePath = "/imagePath_$id.png"
        )
        return people
    }

    fun createSimilarMovie(): SimilarMovie {
        val id = counter.incrementAndGet()
        val movie = SimilarMovie(
            id = id,
            title = "title_$id",
            posterPath = "/imagePath_$id.png",
        )
        return movie
    }
}