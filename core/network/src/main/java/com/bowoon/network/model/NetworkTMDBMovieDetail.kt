package com.bowoon.network.model


import com.bowoon.model.TMBDMovieDetailVideos
import com.bowoon.model.TMDBMovieDetail
import com.bowoon.model.TMDBMovieDetailAlternativeTitles
import com.bowoon.model.TMDBMovieDetailBackdrop
import com.bowoon.model.TMDBMovieDetailBelongsToCollection
import com.bowoon.model.TMDBMovieDetailCast
import com.bowoon.model.TMDBMovieDetailChange
import com.bowoon.model.TMDBMovieDetailChanges
import com.bowoon.model.TMDBMovieDetailCountry
import com.bowoon.model.TMDBMovieDetailCredits
import com.bowoon.model.TMDBMovieDetailCrew
import com.bowoon.model.TMDBMovieDetailData
import com.bowoon.model.TMDBMovieDetailGenre
import com.bowoon.model.TMDBMovieDetailImages
import com.bowoon.model.TMDBMovieDetailItem
import com.bowoon.model.TMDBMovieDetailKeyword
import com.bowoon.model.TMDBMovieDetailKeywords
import com.bowoon.model.TMDBMovieDetailLogo
import com.bowoon.model.TMDBMovieDetailPoster
import com.bowoon.model.TMDBMovieDetailProductionCompany
import com.bowoon.model.TMDBMovieDetailProductionCountry
import com.bowoon.model.TMDBMovieDetailReleases
import com.bowoon.model.TMDBMovieDetailSimilar
import com.bowoon.model.TMDBMovieDetailSimilarResult
import com.bowoon.model.TMDBMovieDetailSpokenLanguage
import com.bowoon.model.TMDBMovieDetailTitle
import com.bowoon.model.TMDBMovieDetailTranslation
import com.bowoon.model.TMDBMovieDetailTranslations
import com.bowoon.model.TMDBMovieDetailVideoResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBMovieDetail(
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
//    @SerialName("changes")
//    val changes: NetworkTMDBMovieDetailChanges? = null,
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
//    @SerialName("lists")
//    val lists: Lists? = null,
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
//    @SerialName("reviews")
//    val reviews: Reviews? = null,
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
    @SerialName("translations")
    val translations: NetworkTMDBMovieDetailTranslations? = null,
    @SerialName("video")
    val video: Boolean? = null,
    @SerialName("videos")
    val videos: NetworkTMDBMovieDetailVideos? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null,
    @SerialName("similar")
    val similar: NetworkTMDBMovieDetailSimilar? = null
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
data class NetworkTMDBMovieDetailChanges(
    @SerialName("changes")
    val changes: List<NetworkTMDBMovieDetailChange>? = null
)

@Serializable
data class NetworkTMDBMovieDetailChange(
    @SerialName("items")
    val items: List<NetworkTMDBMovieDetailItem>? = null,
    @SerialName("key")
    val key: String? = null
)

@Serializable
data class NetworkTMDBMovieDetailItem(
    @SerialName("action")
    val action: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("iso_3166_1")
    val iso31661: String? = null,
    @SerialName("iso_639_1")
    val iso6391: String? = null,
    @SerialName("original_value")
    val originalValue: List<String>? = null,
    @SerialName("time")
    val time: String? = null,
    @SerialName("value")
    val value: List<String>? = null
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
    val backdrops: List<NetworkTMDBMovieDetailBackdrop>? = null,
    @SerialName("logos")
    val logos: List<NetworkTMDBMovieDetailLogo>? = null,
    @SerialName("posters")
    val posters: List<NetworkTMDBMovieDetailPoster>? = null
)

@Serializable
data class NetworkTMDBMovieDetailBackdrop(
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
data class NetworkTMDBMovieDetailLogo(
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
data class NetworkTMDBMovieDetailPoster(
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

fun NetworkTMDBMovieDetail.asExternalModel(): TMDBMovieDetail =
    TMDBMovieDetail(
        adult = adult,
        alternativeTitles = alternativeTitles?.asExternalModel(),
        backdropPath = backdropPath,
        belongsToCollection = belongsToCollection?.asExternalModel(),
        budget = budget,
//        changes = changes?.asExternalModel(),
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
        translations = translations?.asExternalModel(),
        video = video,
        videos = videos?.asExternalModel(),
        voteCount = voteCount,
        voteAverage = voteAverage,
        similar = similar?.asExternalModel()
    )

fun NetworkTMDBMovieDetailAlternativeTitles.asExternalModel(): TMDBMovieDetailAlternativeTitles =
    TMDBMovieDetailAlternativeTitles(
        titles = titles?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailTitleAsExternalModel")
fun List<NetworkTMDBMovieDetailTitle>.asExternalModel(): List<TMDBMovieDetailTitle> =
    map {
        TMDBMovieDetailTitle(
            iso31661 = it.iso31661,
            title = it.title,
            type = it.type
        )
    }

fun NetworkTMDBMovieDetailBelongsToCollection.asExternalModel(): TMDBMovieDetailBelongsToCollection =
    TMDBMovieDetailBelongsToCollection(
        backdropPath = backdropPath,
        id = id,
        name = name,
        posterPath = posterPath
    )

fun NetworkTMDBMovieDetailChanges.asExternalModel(): TMDBMovieDetailChanges =
    TMDBMovieDetailChanges(
        changes = changes?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailChangeAsExternalModel")
fun List<NetworkTMDBMovieDetailChange>.asExternalModel(): List<TMDBMovieDetailChange> =
    map {
        TMDBMovieDetailChange(
            items = it.items?.asExternalModel(),
            key = it.key
        )
    }

@JvmName("NetworkTMDBMovieDetailItemAsExternalModel")
fun List<NetworkTMDBMovieDetailItem>.asExternalModel(): List<TMDBMovieDetailItem> =
    map {
        TMDBMovieDetailItem(
            action = it.action,
            id = it.id,
            iso31661 = it.iso31661,
            iso6391 = it.iso6391,
            originalValue = it.originalValue,
            time = it.time,
            value = it.value
        )
    }

fun NetworkTMDBMovieDetailCredits.asExternalModel(): TMDBMovieDetailCredits =
    TMDBMovieDetailCredits(
        cast = cast?.asExternalModel(),
        crew = crew?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailCastAsExternalModel")
fun List<NetworkTMDBMovieDetailCast>.asExternalModel(): List<TMDBMovieDetailCast> =
    map {
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
            profilePath = it.profilePath
        )
    }

@JvmName("NetworkTMDBMovieDetailCrewAsExternalModel")
fun List<NetworkTMDBMovieDetailCrew>.asExternalModel(): List<TMDBMovieDetailCrew> =
    map {
        TMDBMovieDetailCrew(
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
fun List<NetworkTMDBMovieDetailGenre>.asExternalModel(): List<TMDBMovieDetailGenre> =
    map {
        TMDBMovieDetailGenre(
            id = it.id,
            name = it.name
        )
    }

fun NetworkTMDBMovieDetailImages.asExternalModel(): TMDBMovieDetailImages =
    TMDBMovieDetailImages(
        backdrops = backdrops?.asExternalModel(),
        logos = logos?.asExternalModel(),
        posters = posters?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailBackdropAsExternalModel")
fun List<NetworkTMDBMovieDetailBackdrop>.asExternalModel(): List<TMDBMovieDetailBackdrop> =
    map {
        TMDBMovieDetailBackdrop(
            aspectRatio = it.aspectRatio,
            filePath = it.filePath,
            height = it.height,
            iso6391 = it.iso6391,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount,
            width = it.width
        )
    }

@JvmName("NetworkTMDBMovieDetailLogoAsExternalModel")
fun List<NetworkTMDBMovieDetailLogo>.asExternalModel(): List<TMDBMovieDetailLogo> =
    map {
        TMDBMovieDetailLogo(
            aspectRatio = it.aspectRatio,
            filePath = it.filePath,
            height = it.height,
            iso6391 = it.iso6391,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount,
            width = it.width
        )
    }

@JvmName("NetworkTMDBMovieDetailPosterAsExternalModel")
fun List<NetworkTMDBMovieDetailPoster>.asExternalModel(): List<TMDBMovieDetailPoster> =
    map {
        TMDBMovieDetailPoster(
            aspectRatio = it.aspectRatio,
            filePath = it.filePath,
            height = it.height,
            iso6391 = it.iso6391,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount,
            width = it.width
        )
    }

fun NetworkTMDBMovieDetailKeywords.asExternalModel(): TMDBMovieDetailKeywords =
    TMDBMovieDetailKeywords(
        keywords = keywords?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailKeywordAsExternalModel")
fun List<NetworkTMDBMovieDetailKeyword>.asExternalModel(): List<TMDBMovieDetailKeyword> =
    map {
        TMDBMovieDetailKeyword(
            id = it.id,
            name = it.name
        )
    }

@JvmName("NetworkTMDBMovieDetailProductionCountryAsExternalModel")
fun List<NetworkTMDBMovieDetailProductionCountry>.asExternalModel(): List<TMDBMovieDetailProductionCountry> =
    map {
        TMDBMovieDetailProductionCountry(
            iso31661 = it.iso31661,
            name = it.name
        )
    }

@JvmName("NetworkTMDBMovieDetailProductionCompanyAsExternalModel")
fun List<NetworkTMDBMovieDetailProductionCompany>.asExternalModel(): List<TMDBMovieDetailProductionCompany> =
    map {
        TMDBMovieDetailProductionCompany(
            id = it.id,
            logoPath = it.logoPath,
            name = it.name,
            originCountry = it.originCountry
        )
    }

fun NetworkTMDBMovieDetailReleases.asExternalModel(): TMDBMovieDetailReleases =
    TMDBMovieDetailReleases(
        countries = countries?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailCountryAsExternalModel")
fun List<NetworkTMDBMovieDetailCountry>.asExternalModel(): List<TMDBMovieDetailCountry> =
    map {
        TMDBMovieDetailCountry(
            certification = it.certification,
            descriptors = it.descriptors,
            iso31661 = it.iso31661,
            primary = it.primary,
            releaseDate = it.releaseDate
        )
    }

@JvmName("NetworkTMDBMovieDetailSpokenLanguageAsExternalModel")
fun List<NetworkTMDBMovieDetailSpokenLanguage>.asExternalModel(): List<TMDBMovieDetailSpokenLanguage> =
    map {
        TMDBMovieDetailSpokenLanguage(
            englishName = it.englishName,
            iso6391 = it.iso6391,
            name = it.name
        )
    }

fun NetworkTMDBMovieDetailTranslations.asExternalModel(): TMDBMovieDetailTranslations =
    TMDBMovieDetailTranslations(
        translations = translations?.asExternalModel()
    )

@JvmName("NetworkTMDBMovieDetailTranslationAsExternalModel")
fun List<NetworkTMDBMovieDetailTranslation>.asExternalModel(): List<TMDBMovieDetailTranslation> =
    map {
        TMDBMovieDetailTranslation(
            data = it.data?.asExternalModel(),
            englishName = it.englishName,
            iso6391 = it.iso6391,
            iso31661 = it.iso31661,
            name = it.name
        )
    }

fun NetworkTMDBMovieDetailData.asExternalModel(): TMDBMovieDetailData =
    TMDBMovieDetailData(
        homepage = homepage,
        overview = overview,
        runtime = runtime,
        tagline = tagline,
        title = title
    )

fun NetworkTMDBMovieDetailVideos.asExternalModel(): TMBDMovieDetailVideos =
    TMBDMovieDetailVideos(
        results = results?.asExternalModel()
    )

@JvmName("NetworkTMDBVideoResultAsExternalModel")
fun List<NetworkTMDBVideoResult>.asExternalModel(): List<TMDBMovieDetailVideoResult> =
    map {
        TMDBMovieDetailVideoResult(
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

fun NetworkTMDBMovieDetailSimilar.asExternalModel(): TMDBMovieDetailSimilar =
    TMDBMovieDetailSimilar(
        page = page,
        results = results?.asExternalModel(),
        totalPages = totalPages,
        totalResults = totalResults
    )

@JvmName("NetworkTMDBMovieDetailSimilarResultAsExternalModel")
fun List<NetworkTMDBMovieDetailSimilarResult>.asExternalModel(): List<TMDBMovieDetailSimilarResult> =
    map {
        TMDBMovieDetailSimilarResult(
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