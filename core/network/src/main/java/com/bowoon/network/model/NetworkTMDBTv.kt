package com.bowoon.network.model


import com.bowoon.model.Tv
import com.bowoon.model.TvAlternativeTitles
import com.bowoon.model.TvCreatedBy
import com.bowoon.model.TvKeywords
import com.bowoon.model.TvLastEpisodeToAir
import com.bowoon.model.TvNetwork
import com.bowoon.model.TvNextEpisodeToAir
import com.bowoon.model.TvSeason
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBTv(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("alternative_titles")
    val alternativeTitles: NetworkTMDBAlternativeTitles? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("created_by")
    val createdBy: List<NetworkTMDBTvCreatedBy>? = null,
    @SerialName("credits")
    val credits: NetworkTMDBCredits? = null,
    @SerialName("episode_run_time")
    val episodeRunTime: List<Int>? = null,
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("genres")
    val genres: List<NetworkTMDBGenre>? = null,
    @SerialName("homepage")
    val homepage: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("images")
    val images: NetworkTMDBImages? = null,
    @SerialName("in_production")
    val inProduction: Boolean? = null,
    @SerialName("keywords")
    val keywords: NetworkTMDBTvKeywords? = null,
    @SerialName("languages")
    val languages: List<String>? = null,
    @SerialName("last_air_date")
    val lastAirDate: String? = null,
    @SerialName("last_episode_to_air")
    val lastEpisodeToAir: NetworkTMDBTvLastEpisodeToAir? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("networks")
    val networks: List<NetworkTMDBTvNetwork>? = null,
    @SerialName("next_episode_to_air")
    val nextEpisodeToAir: NetworkTMDBTvNextEpisodeToAir? = null,
    @SerialName("number_of_episodes")
    val numberOfEpisodes: Int? = null,
    @SerialName("number_of_seasons")
    val numberOfSeasons: Int? = null,
    @SerialName("origin_country")
    val originCountry: List<String>? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("production_companies")
    val productionCompanies: List<NetworkTMDBProductionCompany>? = null,
    @SerialName("production_countries")
    val productionCountries: List<NetworkTMDBProductionCountry>? = null,
    @SerialName("seasons")
    val seasons: List<NetworkTMDBTvSeason>? = null,
    @SerialName("spoken_languages")
    val spokenLanguages: List<NetworkTMDBSpokenLanguage>? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("tagline")
    val tagline: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("videos")
    val videos: NetworkTMDBVideos? = null,
    @SerialName("vote_average")
    val voteAverage: Float? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

@Serializable
data class NetworkTMDBTvAlternativeTitles(
    @SerialName("results")
    val results: List<NetworkTMDBAlternativeTitle>? = null
)

@Serializable
data class NetworkTMDBTvCreatedBy(
    @SerialName("credit_id")
    val creditId: String? = null,
    @SerialName("gender")
    val gender: Int? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("profile_path")
    val profilePath: String? = null
)

@Serializable
data class NetworkTMDBTvKeywords(
    @SerialName("results")
    val results: List<NetworkTMDBKeyword>? = null
)

@Serializable
data class NetworkTMDBTvLastEpisodeToAir(
    @SerialName("air_date")
    val airDate: String? = null,
    @SerialName("episode_number")
    val episodeNumber: Int? = null,
    @SerialName("episode_type")
    val episodeType: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("production_code")
    val productionCode: String? = null,
    @SerialName("runtime")
    val runtime: Int? = null,
    @SerialName("season_number")
    val seasonNumber: Int? = null,
    @SerialName("show_id")
    val showId: Int? = null,
    @SerialName("still_path")
    val stillPath: String? = null,
    @SerialName("vote_average")
    val voteAverage: Float? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

@Serializable
data class NetworkTMDBTvNetwork(
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
data class NetworkTMDBTvNextEpisodeToAir(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("vote_average")
    val voteAverage: Float? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null,
    @SerialName("air_date")
    val airDate: String? = null,
    @SerialName("episode_number")
    val episodeNumber: Int? = null,
    @SerialName("episode_type")
    val episodeType: String? = null,
    @SerialName("production_code")
    val productionCode: String? = null,
    @SerialName("runtime")
    val runtime: Int? = null,
    @SerialName("season_number")
    val seasonNumber: Int? = null,
    @SerialName("show_id")
    val showId: Int? = null,
    @SerialName("still_path")
    val stillPath: String? = null
)

@Serializable
data class NetworkTMDBTvSeason(
    @SerialName("air_date")
    val airDate: String? = null,
    @SerialName("episode_count")
    val episodeCount: Int? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("season_number")
    val seasonNumber: Int? = null,
    @SerialName("vote_average")
    val voteAverage: Float? = null
)

fun NetworkTMDBTv.asExternalModel(): Tv = Tv(
    adult = adult,
    alternativeTitles = alternativeTitles?.asExternalModel(),
    backdropPath = backdropPath,
    createdBy = createdBy?.asExternalModel(),
    credits = credits?.asExternalModel(),
    episodeRunTime = episodeRunTime,
    firstAirDate = firstAirDate,
    genres = genres?.asExternalModel(),
    homepage = homepage,
    id = id,
    images = images?.asExternalModel(),
    inProduction = inProduction,
    keywords = keywords?.asExternalModel(),
    languages = languages,
    lastAirDate = lastAirDate,
    lastEpisodeToAir = lastEpisodeToAir?.asExternalModel(),
    title = name,
    networks = networks?.asExternalModel(),
    nextEpisodeToAir = nextEpisodeToAir?.asExternalModel(),
    numberOfEpisodes = numberOfEpisodes,
    numberOfSeasons = numberOfSeasons,
    originCountry = originCountry,
    originalLanguage = originalLanguage,
    originalTitle = originalName,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    productionCompanies = productionCompanies?.asExternalModel(),
    productionCountries = productionCountries?.asExternalModel(),
    seasons = seasons?.asExternalModel(),
    spokenLanguages = spokenLanguages?.asExternalModel(),
    status = status,
    tagline = tagline,
    type = type,
    videos = videos?.asExternalModel(),
    voteAverage = voteAverage,
    voteCount = voteCount,
    releaseDate = firstAirDate
)

fun NetworkTMDBTvAlternativeTitles.asExternalModel(): TvAlternativeTitles = TvAlternativeTitles(
    results = results?.asExternalModel()
)

@JvmName("asExternalModelTvCreatedBy")
fun List<NetworkTMDBTvCreatedBy>.asExternalModel(): List<TvCreatedBy> = map {
    TvCreatedBy(
        creditId = it.creditId,
        gender = it.gender,
        id = it.id,
        name = it.name,
        originalName = it.originalName,
        profilePath = it.profilePath
    )
}

fun NetworkTMDBTvKeywords.asExternalModel(): TvKeywords = TvKeywords(
    results = results?.asExternalModel()
)

fun NetworkTMDBTvLastEpisodeToAir.asExternalModel(): TvLastEpisodeToAir = TvLastEpisodeToAir(
    airDate = airDate,
    episodeNumber = episodeNumber,
    episodeType = episodeType,
    id = id,
    name = name,
    overview = overview,
    productionCode = productionCode,
    runtime = runtime,
    seasonNumber = seasonNumber,
    showId = showId,
    stillPath = stillPath,
    voteAverage = voteAverage,
    voteCount = voteCount
)

@JvmName("asExternalModelTvNetwork")
fun List<NetworkTMDBTvNetwork>.asExternalModel(): List<TvNetwork> = map {
    TvNetwork(
        id = it.id,
        logoPath = it.logoPath,
        name = it.name,
        originCountry = it.originCountry
    )
}

fun NetworkTMDBTvNextEpisodeToAir.asExternalModel(): TvNextEpisodeToAir = TvNextEpisodeToAir(
    id = id,
    name = name,
    overview = overview,
    voteAverage = voteAverage,
    voteCount = voteCount,
    airDate = airDate,
    episodeNumber = episodeNumber,
    episodeType = episodeType,
    productionCode = productionCode,
    runtime = runtime,
    seasonNumber = seasonNumber,
    showId = showId,
    stillPath = stillPath
)

@JvmName("asExternalModelTvSeason")
fun List<NetworkTMDBTvSeason>.asExternalModel(): List<TvSeason> = map {
    TvSeason(
        airDate = it.airDate,
        episodeCount = it.episodeCount,
        id = it.id,
        name = it.name,
        overview = it.overview,
        posterPath = it.posterPath,
        seasonNumber = it.seasonNumber,
        voteAverage = it.voteAverage
    )
}