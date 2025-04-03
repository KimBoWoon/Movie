package com.bowoon.testing.model

import com.bowoon.model.Certification
import com.bowoon.model.CertificationData
import com.bowoon.model.CertificationMap
import com.bowoon.model.Configuration
import com.bowoon.model.Genre
import com.bowoon.model.Genres
import com.bowoon.model.ImageInfo
import com.bowoon.model.Language
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.Region
import com.bowoon.model.Regions
import com.bowoon.model.SearchData
import com.bowoon.model.SimilarMovie
import com.bowoon.model.SimilarMovies
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

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
    Language(englishName = "en", iso6391 = "en", name = "en", isSelected = false),
    Language(englishName = "ko", iso6391 = "ko", name = "ko", isSelected = true)
)

val regionTestData = Regions(
    results = listOf(
        Region(englishName = "en", iso31661 = "en", nativeName = "en", isSelected = false),
        Region(englishName = "ko", iso31661 = "ko", nativeName = "ko", isSelected = true)
    )
)

val genreListTestData = Genres(
    genres = listOf(
        Genre(
            id = 0,
            name = "genre_1"
        ),
        Genre(
            id = 1,
            name = "genre_2"
        ),
        Genre(
            id = 3,
            name = "genre_3"
        )
    )
)

val configurationTestData = Configuration(
    changeKeys = listOf(),
    images = ImageInfo(
        baseUrl = "https://",
        secureBaseUrl = "https://",
        posterSizes = listOf("w92", "w182", "w342", "w540", "w720", "original")
    )
)

val movieSearchTestData = SearchData(
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

val peopleSearchTestData = SearchData(
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
    upComingMovies = listOf(Movie(id = 0, title = "upcomingMovie_1", posterPath = "posterUrl/upcomingMovie_1.png"))
)

object MovieFactory {
    private val counter = AtomicInteger(0)

    fun createMovieItem(): Movie {
        val id = counter.incrementAndGet()
        val movie = Movie(
            id = id,
            title = "title_$id",
            imagePath = "/imagePath_$id.png",
            posterPath = "/imagePath_$id.png",
            genreIds = listOf(Random(System.currentTimeMillis()).nextInt(0, 5))
        )
        return movie
    }

    fun createPeopleItem(): People {
        val id = counter.incrementAndGet()
        val people = People(
            id = id,
            name = "name_$id",
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