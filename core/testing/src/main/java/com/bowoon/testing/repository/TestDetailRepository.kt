package com.bowoon.testing.repository

import com.bowoon.data.repository.DetailRepository
import com.bowoon.model.AlternativeTitle
import com.bowoon.model.AlternativeTitles
import com.bowoon.model.BelongsToCollection
import com.bowoon.model.Cast
import com.bowoon.model.CombineCredits
import com.bowoon.model.CombineCreditsCast
import com.bowoon.model.CombineCreditsCrew
import com.bowoon.model.Country
import com.bowoon.model.Credits
import com.bowoon.model.Crew
import com.bowoon.model.ExternalIds
import com.bowoon.model.Genre
import com.bowoon.model.Image
import com.bowoon.model.Images
import com.bowoon.model.Keyword
import com.bowoon.model.Keywords
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieLists
import com.bowoon.model.PeopleDetail
import com.bowoon.model.ProductionCompany
import com.bowoon.model.ProductionCountry
import com.bowoon.model.Releases
import com.bowoon.model.Reviews
import com.bowoon.model.SearchData
import com.bowoon.model.SpokenLanguage
import com.bowoon.model.Translation
import com.bowoon.model.TranslationInfo
import com.bowoon.model.Translations
import com.bowoon.model.VideoInfo
import com.bowoon.model.Videos
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.annotations.VisibleForTesting

class TestDetailRepository : DetailRepository {
    private val movieDetail = MutableSharedFlow<MovieDetail>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val movieSearchData = MutableSharedFlow<SearchData>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val peopleDetail = MutableSharedFlow<PeopleDetail>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val combineCredits = MutableSharedFlow<CombineCredits>(replay = 1, onBufferOverflow = DROP_OLDEST)
    private val externalIds = MutableSharedFlow<ExternalIds>(replay = 1, onBufferOverflow = DROP_OLDEST)

    override fun getMovieDetail(id: Int): Flow<MovieDetail> = movieDetail

    override fun discoverMovie(
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<SearchData> = movieSearchData

    override fun getPeople(personId: Int): Flow<PeopleDetail> = peopleDetail

    override fun getCombineCredits(personId: Int): Flow<CombineCredits> = combineCredits

    override fun getExternalIds(personId: Int): Flow<ExternalIds> = externalIds

    @VisibleForTesting
    fun setMovieDetail(detail: MovieDetail) {
        movieDetail.tryEmit(detail)
    }

    @VisibleForTesting
    fun setDiscoverMovie(movie: SearchData) {
        movieSearchData.tryEmit(movie)
    }

    @VisibleForTesting
    fun setPeopleDetail(people: PeopleDetail) {
        peopleDetail.tryEmit(people)
    }

    @VisibleForTesting
    fun setCombineCredits(credits: CombineCredits) {
        combineCredits.tryEmit(credits)
    }

    @VisibleForTesting
    fun setExternalIds(ids: ExternalIds) {
        externalIds.tryEmit(ids)
    }
}

val favoriteMovieDetailTestData = MovieDetail(
    adult = true,
    autoPlayTrailer = true,
    alternativeTitles = AlternativeTitles(titles = listOf(AlternativeTitle(iso31661 = "KR", title = "title_KR", type = "type_kr"))),
    backdropPath = "backdropPath",
    belongsToCollection = BelongsToCollection(backdropPath = "/backdropPath.png", id = 0, name = "name", posterPath = "/posterPath.png"),
    budget = 30_000_000_000,
    credits = Credits(cast = listOf(Cast(castId = 0, name = "cast_1")), crew = listOf(Crew(id = 0, name = "crew_1"))),
    genres = listOf(Genre(id = 0, name = "genre")),
    homepage = "homepage",
    id = 0,
    images = Images(backdrops = listOf(Image(filePath = "/backdrops_1.png")), logos = listOf(), posters = listOf(Image(filePath = "/poster_1.png"), Image(filePath = "/poster_2.png"), Image(filePath = "/poster_3.png"))),
    imdbId = "imdbId",
    keywords = Keywords(keywords = listOf(Keyword(id = 0, name = "name"))),
    lists = MovieLists(id = 0, page = 1, results = listOf(), totalPages = 1, totalResults = 0),
    originCountry = listOf("originCountry"),
    originalLanguage = "originalLanguage",
    originalTitle = "originalTitle",
    overview = "overview",
    popularity = 3.5,
    posterPath = "/posterPath.png",
    productionCompanies = listOf(ProductionCompany(id = 0, logoPath = "/logoPath.png", name = "name", originCountry = "originCountry")),
    productionCountries = listOf(ProductionCountry(iso31661 = "KR", name = "name")),
    releaseDate = "2025-12-25",
    releases = Releases(countries = listOf(Country(certification = "15", descriptors = listOf("descriptors"), iso31661 = "KR", primary = true, releaseDate = "2025-12-25"))),
    revenue = 30_000_000_000,
    reviews = Reviews(id = 0, page = 1, results = listOf(), totalPages = 1, totalResults = 1),
    runtime = 240,
    spokenLanguages = listOf(SpokenLanguage(englishName = "englishName", iso6391 = "ko", name = "name")),
    status = "Release",
    tagline = "tagline",
    title = "title",
    translations = Translations(translations = listOf(Translation(translationInfo = TranslationInfo(homepage = "", overview = "", runtime = 240, tagline = "", title = ""), englishName = "", iso6391 = "", iso31661 = "", name = "name"))),
    video = true,
    videos = Videos(listOf(VideoInfo(id = "", iso31661 = "", iso6391 = "", key = "", name = "", official = true, publishedAt = "", site = "", size = 19, type = ""))),
    voteAverage = 3.5,
    voteCount = 203,
    certification = "15",
    isFavorite = true
)

val unFavoriteMovieDetailTestData = MovieDetail(
    adult = true,
    autoPlayTrailer = true,
    alternativeTitles = AlternativeTitles(titles = listOf(AlternativeTitle(iso31661 = "KR", title = "title_KR", type = "type_kr"))),
    backdropPath = "backdropPath",
    belongsToCollection = BelongsToCollection(backdropPath = "/backdropPath.png", id = 0, name = "name", posterPath = "/posterPath.png"),
    budget = 30_000_000_000,
    credits = Credits(cast = listOf(), crew = listOf()),
    genres = listOf(Genre(id = 0, name = "genre")),
    homepage = "homepage",
    id = 324,
    images = Images(backdrops = listOf(), logos = listOf(), posters = listOf()),
    imdbId = "imdbId",
    keywords = Keywords(keywords = listOf(Keyword(id = 0, name = "name"))),
    lists = MovieLists(id = 0, page = 1, results = listOf(), totalPages = 1, totalResults = 0),
    originCountry = listOf("originCountry"),
    originalLanguage = "originalLanguage",
    originalTitle = "originalTitle",
    overview = "overview",
    popularity = 3.5,
    posterPath = "/posterPath.png",
    productionCompanies = listOf(ProductionCompany(id = 0, logoPath = "/logoPath.png", name = "name", originCountry = "originCountry")),
    productionCountries = listOf(ProductionCountry(iso31661 = "KR", name = "name")),
    releaseDate = "2025-12-25",
    releases = Releases(countries = listOf(Country(certification = "15", descriptors = listOf("descriptors"), iso31661 = "KR", primary = true, releaseDate = "2025-12-25"))),
    revenue = 30_000_000_000,
    reviews = Reviews(id = 0, page = 1, results = listOf(), totalPages = 1, totalResults = 1),
    runtime = 240,
    spokenLanguages = listOf(SpokenLanguage(englishName = "englishName", iso6391 = "ko", name = "name")),
    status = "Release",
    tagline = "tagline",
    title = "title",
    translations = Translations(translations = listOf(Translation(translationInfo = TranslationInfo(homepage = "", overview = "", runtime = 240, tagline = "", title = ""), englishName = "", iso6391 = "", iso31661 = "", name = "name"))),
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

val peopleDetailTestData = PeopleDetail(
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