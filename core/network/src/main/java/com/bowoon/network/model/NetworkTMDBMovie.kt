package com.bowoon.network.model


import com.bowoon.model.AlternativeTitle
import com.bowoon.model.AlternativeTitles
import com.bowoon.model.BelongsToCollection
import com.bowoon.model.Cast
import com.bowoon.model.Country
import com.bowoon.model.Credits
import com.bowoon.model.Crew
import com.bowoon.model.Genre
import com.bowoon.model.Image
import com.bowoon.model.Images
import com.bowoon.model.Keyword
import com.bowoon.model.Keywords
import com.bowoon.model.Movie
import com.bowoon.model.ProductionCompany
import com.bowoon.model.ProductionCountry
import com.bowoon.model.Releases
import com.bowoon.model.SimilarMovie
import com.bowoon.model.SimilarMovies
import com.bowoon.model.SpokenLanguage
import com.bowoon.model.Translation
import com.bowoon.model.TranslationInfo
import com.bowoon.model.Translations
import com.bowoon.model.VideoInfo
import com.bowoon.model.Videos
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBMovie(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("alternative_titles")
    val alternativeTitles: NetworkTMDBMovieDetailAlternativeTitles? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("belongs_to_collection")
    val belongsToCollection: NetworkTMDBMovieDetailBelongsToCollection? = null,
    @SerialName("budget")
    val budget: Long? = null,
    @SerialName("credits")
    val credits: NetworkTMDBMovieDetailCredits? = null,
    @SerialName("genres")
    val genres: List<NetworkTMDBMovieDetailGenre>? = null,
    @SerialName("homepage")
    val homepage: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("images")
    val images: NetworkTMDBMovieDetailImages? = null,
    @SerialName("imdb_id")
    val imdbId: String? = null,
    @SerialName("keywords")
    val keywords: NetworkTMDBMovieDetailKeywords? = null,
    @SerialName("origin_country")
    val originCountry: List<String>? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("production_companies")
    val productionCompanies: List<NetworkTMDBMovieDetailProductionCompany>? = null,
    @SerialName("production_countries")
    val productionCountries: List<NetworkTMDBMovieDetailProductionCountry>? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("releases")
    val releases: NetworkTMDBMovieDetailReleases? = null,
    @SerialName("revenue")
    val revenue: Long? = null,
    @SerialName("runtime")
    val runtime: Int? = null,
    @SerialName("spoken_languages")
    val spokenLanguages: List<NetworkTMDBMovieDetailSpokenLanguage>? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("tagline")
    val tagline: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("video")
    val video: Boolean? = null,
    @SerialName("videos")
    val videos: NetworkTMDBMovieDetailVideos? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

@Serializable
data class NetworkTMDBMovieDetailAlternativeTitles(
    @SerialName("titles")
    val titles: List<NetworkTMDBMovieDetailTitle>? = null
)

@Serializable
data class NetworkTMDBMovieDetailTitle(
    @SerialName("iso_3166_1")
    val iso31661: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("type")
    val type: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailBelongsToCollection(
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailCredits(
    @SerialName("cast")
    val cast: List<NetworkTMDBMovieDetailCast>? = null,
    @SerialName("crew")
    val crew: List<NetworkTMDBMovieDetailCrew>? = null
)

@Serializable
data class NetworkTMDBMovieDetailCast(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("cast_id")
    val castId: Int? = null,
    @SerialName("character")
    val character: String? = null,
    @SerialName("credit_id")
    val creditId: String? = null,
    @SerialName("gender")
    val gender: Int? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("order")
    val order: Int? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("profile_path")
    val profilePath: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailCrew(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("credit_id")
    val creditId: String? = null,
    @SerialName("department")
    val department: String? = null,
    @SerialName("gender")
    val gender: Int? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("job")
    val job: String? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("profile_path")
    val profilePath: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailGenre(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailImages(
    @SerialName("backdrops")
    val backdrops: List<NetworkTMDBMovieDetailImage>? = null,
    @SerialName("logos")
    val logos: List<NetworkTMDBMovieDetailImage>? = null,
    @SerialName("posters")
    val posters: List<NetworkTMDBMovieDetailImage>? = null
)

@Serializable
data class NetworkTMDBMovieDetailImage(
    @SerialName("aspect_ratio")
    val aspectRatio: Double? = null,
    @SerialName("file_path")
    val filePath: String? = null,
    @SerialName("height")
    val height: Int? = null,
    @SerialName("iso_639_1")
    val iso6391: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null,
    @SerialName("width")
    val width: Int? = null
)

@Serializable
data class NetworkTMDBMovieDetailKeywords(
    @SerialName("keywords")
    val keywords: List<NetworkTMDBMovieDetailKeyword>? = null
)

@Serializable
data class NetworkTMDBMovieDetailKeyword(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null
)

//    @Serializable
//    data class Lists(
//        @SerialName("page")
//        val page: Int? = null,
//        @SerialName("results")
//        val results: List<Any>? = null,
//        @SerialName("total_pages")
//        val totalPages: Int? = null,
//        @SerialName("total_results")
//        val totalResults: Int? = null
//    )

@Serializable
data class NetworkTMDBMovieDetailProductionCompany(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("logo_path")
    val logoPath: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("origin_country")
    val originCountry: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailProductionCountry(
    @SerialName("iso_3166_1")
    val iso31661: String? = null,
    @SerialName("name")
    val name: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailReleases(
    @SerialName("countries")
    val countries: List<NetworkTMDBMovieDetailCountry>? = null
)

@Serializable
data class NetworkTMDBMovieDetailCountry(
    @SerialName("certification")
    val certification: String? = null,
    @SerialName("descriptors")
    val descriptors: List<String>? = null,
    @SerialName("iso_3166_1")
    val iso31661: String? = null,
    @SerialName("primary")
    val primary: Boolean? = null,
    @SerialName("release_date")
    val releaseDate: String? = null
)

//    @Serializable
//    data class Reviews(
//        @SerialName("page")
//        val page: Int? = null,
//        @SerialName("results")
//        val results: List<Any>? = null,
//        @SerialName("total_pages")
//        val totalPages: Int? = null,
//        @SerialName("total_results")
//        val totalResults: Int? = null
//    )

@Serializable
data class NetworkTMDBMovieDetailSpokenLanguage(
    @SerialName("english_name")
    val englishName: String? = null,
    @SerialName("iso_639_1")
    val iso6391: String? = null,
    @SerialName("name")
    val name: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailTranslations(
    @SerialName("translations")
    val translations: List<NetworkTMDBMovieDetailTranslation>? = null
)

@Serializable
data class NetworkTMDBMovieDetailTranslation(
    @SerialName("data")
    val `data`: NetworkTMDBMovieDetailData? = null,
    @SerialName("english_name")
    val englishName: String? = null,
    @SerialName("iso_3166_1")
    val iso31661: String? = null,
    @SerialName("iso_639_1")
    val iso6391: String? = null,
    @SerialName("name")
    val name: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailData(
    @SerialName("homepage")
    val homepage: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("runtime")
    val runtime: Int? = null,
    @SerialName("tagline")
    val tagline: String? = null,
    @SerialName("title")
    val title: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailVideos(
    @SerialName("results")
    val results: List<NetworkTMDBVideoResult>? = null
)

@Serializable
data class NetworkTMDBVideoResult(
    @SerialName("id")
    val id: String? = null,
    @SerialName("iso_3166_1")
    val iso31661: String? = null,
    @SerialName("iso_639_1")
    val iso6391: String? = null,
    @SerialName("key")
    val key: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("official")
    val official: Boolean? = null,
    @SerialName("published_at")
    val publishedAt: String? = null,
    @SerialName("site")
    val site: String? = null,
    @SerialName("size")
    val size: Int? = null,
    @SerialName("type")
    val type: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailSimilar(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<NetworkTMDBMovieDetailSimilarResult>? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class NetworkTMDBMovieDetailSimilarResult(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("video")
    val video: Boolean? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

fun NetworkTMDBMovie.asExternalModel(): Movie =
    Movie(
        adult = adult,
        alternativeTitles = alternativeTitles?.asExternalModel(),
        backdropPath = backdropPath,
        belongsToCollection = belongsToCollection?.asExternalModel(),
        budget = budget,
        credits = credits?.asExternalModel(),
        genres = genres?.asExternalModel(),
        homepage = homepage,
        id = id,
        images = images?.asExternalModel(),
        imdbId = imdbId,
        keywords = keywords?.asExternalModel(),
        originCountry = originCountry,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        productionCountries = productionCountries?.asExternalModel(),
        productionCompanies = productionCompanies?.asExternalModel(),
        releaseDate = releaseDate,
        releases = releases?.asExternalModel(),
        revenue = revenue,
        runtime = runtime,
        spokenLanguages = spokenLanguages?.asExternalModel(),
        status = status,
        tagline = tagline,
        title = title,
        video = video,
        videos = videos?.asExternalModel(),
        voteCount = voteCount,
        voteAverage = voteAverage
    )

fun NetworkTMDBMovieDetailAlternativeTitles.asExternalModel(): AlternativeTitles =
    AlternativeTitles(
        titles = titles?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailTitleAsExternalModel")
fun List<NetworkTMDBMovieDetailTitle>.asExternalModel(): List<AlternativeTitle> =
    map {
        AlternativeTitle(
            iso31661 = it.iso31661,
            title = it.title,
            type = it.type
        )
    }

fun NetworkTMDBMovieDetailBelongsToCollection.asExternalModel(): BelongsToCollection =
    BelongsToCollection(
        backdropPath = backdropPath,
        id = id,
        name = name,
        posterPath = posterPath
    )

//fun NetworkTMDBMovieDetailChanges.asExternalModel(): Changes =
//    Changes(
//        changes = changes?.asExternalModel()
//    )
//
//@JvmName("NetworkTMDBMovieDetailChangeAsExternalModel")
//fun List<NetworkTMDBMovieDetailChange>.asExternalModel(): List<Change> =
//    map {
//        Change(
//            items = it.items?.asExternalModel(),
//            key = it.key
//        )
//    }
//
//@JvmName("NetworkTMDBMovieDetailItemAsExternalModel")
//fun List<NetworkTMDBMovieDetailItem>.asExternalModel(): List<ChangeItem> =
//    map {
//        ChangeItem(
//            action = it.action,
//            id = it.id,
//            iso31661 = it.iso31661,
//            iso6391 = it.iso6391,
//            originalValue = it.originalValue,
//            time = it.time,
//            value = it.value
//        )
//    }

fun NetworkTMDBMovieDetailCredits.asExternalModel(): Credits =
    Credits(
        cast = cast?.asExternalModel(),
        crew = crew?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailCastAsExternalModel")
fun List<NetworkTMDBMovieDetailCast>.asExternalModel(): List<Cast> =
    map {
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
            profilePath = it.profilePath
        )
    }

@JvmName("NetworkTMDBMovieDetailCrewAsExternalModel")
fun List<NetworkTMDBMovieDetailCrew>.asExternalModel(): List<Crew> =
    map {
        Crew(
            adult = it.adult,
            creditId = it.creditId,
            gender = it.gender,
            id = it.id,
            knownForDepartment = it.knownForDepartment,
            name = it.name,
            originalName = it.originalName,
            popularity = it.popularity,
            profilePath = it.profilePath,
            department = it.department,
            job = it.job
        )
    }

@JvmName("NetworkTMDBMovieDetailGenreAsExternalModel")
fun List<NetworkTMDBMovieDetailGenre>.asExternalModel(): List<Genre> =
    map {
        Genre(
            id = it.id,
            name = it.name
        )
    }

fun NetworkTMDBMovieDetailImages.asExternalModel(): Images =
    Images(
        backdrops = backdrops?.asExternalModel(),
        logos = logos?.asExternalModel(),
        posters = posters?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailImageAsExternalModel")
fun List<NetworkTMDBMovieDetailImage>.asExternalModel(): List<Image> =
    map {
        Image(
            aspectRatio = it.aspectRatio,
            filePath = it.filePath,
            height = it.height,
            iso6391 = it.iso6391,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount,
            width = it.width
        )
    }

fun NetworkTMDBMovieDetailKeywords.asExternalModel(): Keywords =
    Keywords(
        keywords = keywords?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailKeywordAsExternalModel")
fun List<NetworkTMDBMovieDetailKeyword>.asExternalModel(): List<Keyword> =
    map {
        Keyword(
            id = it.id,
            name = it.name
        )
    }

@JvmName("NetworkTMDBMovieDetailProductionCountryAsExternalModel")
fun List<NetworkTMDBMovieDetailProductionCountry>.asExternalModel(): List<ProductionCountry> =
    map {
        ProductionCountry(
            iso31661 = it.iso31661,
            name = it.name
        )
    }

@JvmName("NetworkTMDBMovieDetailProductionCompanyAsExternalModel")
fun List<NetworkTMDBMovieDetailProductionCompany>.asExternalModel(): List<ProductionCompany> =
    map {
        ProductionCompany(
            id = it.id,
            logoPath = it.logoPath,
            name = it.name,
            originCountry = it.originCountry
        )
    }

fun NetworkTMDBMovieDetailReleases.asExternalModel(): Releases =
    Releases(
        countries = countries?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailCountryAsExternalModel")
fun List<NetworkTMDBMovieDetailCountry>.asExternalModel(): List<Country> =
    map {
        Country(
            certification = it.certification,
            descriptors = it.descriptors,
            iso31661 = it.iso31661,
            primary = it.primary,
            releaseDate = it.releaseDate
        )
    }

@JvmName("NetworkTMDBMovieDetailSpokenLanguageAsExternalModel")
fun List<NetworkTMDBMovieDetailSpokenLanguage>.asExternalModel(): List<SpokenLanguage> =
    map {
        SpokenLanguage(
            englishName = it.englishName,
            iso6391 = it.iso6391,
            name = it.name
        )
    }

fun NetworkTMDBMovieDetailTranslations.asExternalModel(): Translations =
    Translations(
        translations = translations?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailTranslationAsExternalModel")
fun List<NetworkTMDBMovieDetailTranslation>.asExternalModel(): List<Translation> =
    map {
        Translation(
            translationInfo = it.data?.asExternalModel(),
            englishName = it.englishName,
            iso6391 = it.iso6391,
            iso31661 = it.iso31661,
            name = it.name
        )
    }

fun NetworkTMDBMovieDetailData.asExternalModel(): TranslationInfo =
    TranslationInfo(
        homepage = homepage,
        overview = overview,
        runtime = runtime,
        tagline = tagline,
        title = title
    )

fun NetworkTMDBMovieDetailVideos.asExternalModel(): Videos =
    Videos(
        results = results?.asExternalModel()
    )

@JvmName("NetworkTMDBVideoResultAsExternalModel")
fun List<NetworkTMDBVideoResult>.asExternalModel(): List<VideoInfo> =
    map {
        VideoInfo(
            id = it.id,
            iso31661 = it.iso31661,
            iso6391 = it.iso6391,
            key = it.key,
            name = it.name,
            official = it.official,
            publishedAt = it.publishedAt,
            site = it.site,
            size = it.size,
            type = it.type
        )
    }

fun NetworkTMDBMovieDetailSimilar.asExternalModel(): SimilarMovies =
    SimilarMovies(
        page = page,
        results = results?.asExternalModel(),
        totalPages = totalPages,
        totalResults = totalResults
    )

fun List<NetworkTMDBMovieDetailSimilarResult>.asExternalModel(): List<SimilarMovie> =
    map {
        SimilarMovie(
            adult = it.adult,
            backdropPath = it.backdropPath,
            genreIds = it.genreIds,
            id = it.id,
            originalLanguage = it.originalLanguage,
            originalTitle = it.originalTitle,
            overview = it.overview,
            popularity = it.popularity,
            posterPath = it.posterPath,
            releaseDate = it.releaseDate,
            title = it.title,
            video = it.video,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount
        )
    }