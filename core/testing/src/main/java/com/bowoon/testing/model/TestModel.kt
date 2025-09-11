package com.bowoon.testing.model

import com.bowoon.model.AlternativeTitle
import com.bowoon.model.AlternativeTitles
import com.bowoon.model.BelongsToCollection
import com.bowoon.model.Cast
import com.bowoon.model.Certification
import com.bowoon.model.CertificationData
import com.bowoon.model.CertificationMap
import com.bowoon.model.CombineCredits
import com.bowoon.model.CombineCreditsCast
import com.bowoon.model.CombineCreditsCrew
import com.bowoon.model.Configuration
import com.bowoon.model.Country
import com.bowoon.model.Credits
import com.bowoon.model.Crew
import com.bowoon.model.ExternalIds
import com.bowoon.model.Genre
import com.bowoon.model.Genres
import com.bowoon.model.Image
import com.bowoon.model.ImageInfo
import com.bowoon.model.Images
import com.bowoon.model.Keyword
import com.bowoon.model.Keywords
import com.bowoon.model.Language
import com.bowoon.model.MainMenu
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.ProductionCompany
import com.bowoon.model.ProductionCountry
import com.bowoon.model.Region
import com.bowoon.model.Regions
import com.bowoon.model.Releases
import com.bowoon.model.SearchData
import com.bowoon.model.SearchKeyword
import com.bowoon.model.Series
import com.bowoon.model.SeriesPart
import com.bowoon.model.SimilarMovie
import com.bowoon.model.SimilarMovies
import com.bowoon.model.SpokenLanguage
import com.bowoon.model.VideoInfo
import com.bowoon.model.Videos
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

val seriesSearchTestData = SearchData(
    page = 1,
    results = listOf(
        MovieFactory.createSeriesItem(),
        MovieFactory.createSeriesItem(),
        MovieFactory.createSeriesItem(),
        MovieFactory.createSeriesItem(),
        MovieFactory.createSeriesItem()
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

val nowPlayingMoviesTestData =
    listOf(Movie(id = 0, title = "nowPlaying_1", posterPath = "/nowPlaying_1.png"))
val upcomingMoviesTestData =
    listOf(Movie(id = 0, title = "upcomingMovie_1", posterPath = "/upcomingMovie_1.png"))
val mainMenuTestData = MainMenu(
    nowPlayingMovies = listOf(
        Movie(
            id = 0,
            title = "nowPlaying_1",
            posterPath = "posterUrl/nowPlaying_1.png"
        )
    ),
    upComingMovies = listOf(
        Movie(
            id = 0,
            title = "upcomingMovie_1",
            posterPath = "posterUrl/upcomingMovie_1.png"
        )
    )
)
val movieSeriesTestData = Series(
    backdropPath = "/backdropPath.png",
    id = 896,
    name = "movieSeries",
    overview = "movieSeriesOverview",
    parts = listOf(
        SeriesPart(id = 0, title = "movieSeries_0", releaseDate = "2024-09-23_0", overview = "movieSeries_0_overview", posterPath = "/movieSeriesPosterPath_1.png"),
        SeriesPart(id = 1, title = "movieSeries_1", releaseDate = "2024-09-23_1", overview = "movieSeries_1_overview", posterPath = "/movieSeriesPosterPath_2.png"),
        SeriesPart(adult = true, id = 2, title = "movieSeries_2", releaseDate = "2024-09-23_2", overview = "movieSeries_2_overview", posterPath = "/movieSeriesPosterPath_3.jpg"),
        SeriesPart(adult = false, id = 3, title = "movieSeries_3", releaseDate = "2024-09-23_3", overview = "movieSeries_3_overview", posterPath = "/movieSeriesPosterPath_3.png")
    ),
    posterPath = "/movieSeriesPosterPath.png"
)
val testRecommendedKeyword = (0..5).map {
    SearchKeyword(id = it, name = "mission$it")
}

object MovieFactory {
    private val counter = AtomicInteger(0)

    fun createMovieItem(): Movie {
        val id = counter.incrementAndGet()
        return Movie(
            id = id,
            title = "title_$id",
            posterPath = "/imagePath_$id.png",
            genres = listOf(Genre(id = Random(seed = System.currentTimeMillis()).nextInt(from = 0, until = 5)))
        )
    }

    fun createPeopleItem(): Movie {
        val id = counter.incrementAndGet()
        return Movie(
            id = id,
            title = "name_$id",
            posterPath = "/imagePath_$id.png",
        )
    }

    fun createSeriesItem(): Movie {
        val id = counter.incrementAndGet()
        return Movie(
            id = id,
            title = "name_$id",
            posterPath = "/imagePath_$id.png",
            adult = true
        )
    }

    fun createSimilarMovie(): SimilarMovie {
        val id = counter.incrementAndGet()
        return SimilarMovie(
            id = id,
            title = "title_$id",
            posterPath = "/imagePath_$id.png",
        )
    }
}

val favoriteMovieDetailTestData = Movie(
    adult = true,
    alternativeTitles = AlternativeTitles(titles = listOf(AlternativeTitle(iso31661 = "KR", title = "title_KR", type = "type_kr"))),
    backdropPath = "backdropPath",
    belongsToCollection = BelongsToCollection(backdropPath = "/backdropPath.png", id = 896, name = "name", posterPath = "/posterPath.png"),
    budget = 30_000_000_000,
    credits = Credits(cast = listOf(Cast(castId = 0, name = "cast_1")), crew = listOf(Crew(id = 0, name = "crew_1"))),
    genres = listOf(Genre(id = 0, name = "genre")),
    homepage = "homepage",
    id = 0,
    images = Images(backdrops = listOf(Image(filePath = "/backdrops_1.png")), logos = listOf(), posters = listOf(Image(filePath = "/poster_1.png"), Image(filePath = "/poster_2.png"), Image(filePath = "/poster_3.png"))),
    imdbId = "imdbId",
    keywords = Keywords(keywords = listOf(Keyword(id = 0, name = "name"))),
    originCountry = listOf("originCountry"),
    originalLanguage = "originalLanguage",
    originalTitle = "originalTitle",
    overview = "overview",
    popularity = 3.5,
    posterPath = "https://original/posterPath.png",
    productionCompanies = listOf(ProductionCompany(id = 0, logoPath = "https://original/logoPath.png", name = "name", originCountry = "originCountry")),
    productionCountries = listOf(ProductionCountry(iso31661 = "KR", name = "name")),
    releaseDate = "2025-12-25",
    releases = Releases(countries = listOf(Country(certification = "15", descriptors = listOf("descriptors"), iso31661 = "KR", primary = true, releaseDate = "2025-12-25"))),
    revenue = 30_000_000_000,
    runtime = 240,
    spokenLanguages = listOf(SpokenLanguage(englishName = "englishName", iso6391 = "ko", name = "name")),
    status = "Release",
    tagline = "tagline",
    title = "title",
    video = true,
    videos = Videos(listOf(VideoInfo(id = "", iso31661 = "", iso6391 = "", key = "", name = "", official = true, publishedAt = "", site = "", size = 19, type = ""))),
    voteAverage = 3.5,
    voteCount = 203,
    certification = "15",
    isFavorite = true
)

val unFavoriteMovieDetailTestData = Movie(
    adult = true,
    alternativeTitles = AlternativeTitles(titles = listOf(AlternativeTitle(iso31661 = "KR", title = "title_KR", type = "type_kr"))),
    backdropPath = "backdropPath",
    belongsToCollection = BelongsToCollection(backdropPath = "https://original/backdropPath.png", id = 0, name = "name", posterPath = "https://original/posterPath.png"),
    budget = 30_000_000_000,
    credits = Credits(cast = listOf(), crew = listOf()),
    genres = listOf(Genre(id = 0, name = "genre")),
    homepage = "homepage",
    id = 324,
    images = Images(backdrops = listOf(), logos = listOf(), posters = listOf()),
    imdbId = "imdbId",
    keywords = Keywords(keywords = listOf(Keyword(id = 0, name = "name"))),
    originCountry = listOf("originCountry"),
    originalLanguage = "originalLanguage",
    originalTitle = "originalTitle",
    overview = "overview",
    popularity = 3.5,
    posterPath = "https://original/posterPath.png",
    productionCompanies = listOf(ProductionCompany(id = 0, logoPath = "https://original/logoPath.png", name = "name", originCountry = "originCountry")),
    productionCountries = listOf(ProductionCountry(iso31661 = "KR", name = "name")),
    releaseDate = "2025-12-25",
    releases = Releases(countries = listOf(Country(certification = "15", descriptors = listOf("descriptors"), iso31661 = "KR", primary = true, releaseDate = "2025-12-25"))),
    revenue = 30_000_000_000,
    runtime = 240,
    spokenLanguages = listOf(SpokenLanguage(englishName = "englishName", iso6391 = "ko", name = "name")),
    status = "Release",
    tagline = "tagline",
    title = "title",
    video = true,
    videos = Videos(listOf(VideoInfo(id = "", iso31661 = "", iso6391 = "", key = "", name = "", official = true, publishedAt = "", site = "", size = 19, type = ""))),
    voteAverage = 3.5,
    voteCount = 203,
    certification = "15",
    isFavorite = false
)

val combineCreditsTestData = CombineCredits(
    cast = listOf(
        CombineCreditsCast(posterPath = "/CombineCreditsCast.png")
    ),
    crew = listOf(
        CombineCreditsCrew(posterPath = "/CombineCreditsCrew.png")
    )
)

val externalIdsTestData = ExternalIds(
    facebookId = "facebook",
    instagramId = "instagram",
    youtubeId = "youtubeId"
)

val peopleDetailTestData = People(
    adult = true,
    alsoKnownAs = listOf("alsoKnownAs"),
    biography = "biography",
    birthday = "1992-06-24",
    combineCredits = combineCreditsTestData,
    deathday = null,
    externalIds = externalIdsTestData,
    gender = 1,
    homepage = "homepage",
    id = 0,
    images = listOf(Image()),
    imdbId = "imdbId",
    knownForDepartment = "knownForDepartment",
    name = "name",
    placeOfBirth = "placeOfBirth",
    popularity = 3.5,
    profilePath = "/profilePath.png",
    isFavorite = true
)