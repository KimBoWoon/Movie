package com.bowoon.network.model

import com.bowoon.model.TvEpisode
import com.bowoon.model.TvGuestStar
import com.bowoon.model.TvSeasons
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBTvSeasons(
    @SerialName("air_date")
    val airDate: String? = null,
    @SerialName("credits")
    val credits: NetworkTMDBCredits? = null,
    @SerialName("episodes")
    val episodes: List<NetworkTMDBTvEpisode>? = null,
    @SerialName("_id")
    val _id: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("images")
    val images: NetworkTMDBImages? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("networks")
    val networks: List<NetworkTMDBTvNetwork>? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("season_number")
    val seasonNumber: Int? = null,
    @SerialName("videos")
    val videos: NetworkTMDBVideos? = null,
    @SerialName("vote_average")
    val voteAverage: Float? = null
)

//@Serializable
//data class NetworkTMDBTvEpisode(
//    @SerialName("air_date")
//    val airDate: String? = null,
//    @SerialName("crew")
//    val crew: List<NetworkTMDBCrew>? = null,
//    @SerialName("episode_number")
//    val episodeNumber: Int? = null,
//    @SerialName("episode_type")
//    val episodeType: String? = null,
//    @SerialName("guest_stars")
//    val guestStars: List<NetworkTMDBTvGuestStar>? = null,
//    @SerialName("id")
//    val id: Int? = null,
//    @SerialName("name")
//    val name: String? = null,
//    @SerialName("overview")
//    val overview: String? = null,
//    @SerialName("production_code")
//    val productionCode: String? = null,
//    @SerialName("runtime")
//    val runtime: Int? = null,
//    @SerialName("season_number")
//    val seasonNumber: Int? = null,
//    @SerialName("show_id")
//    val showId: Int? = null,
//    @SerialName("still_path")
//    val stillPath: String? = null,
//    @SerialName("vote_average")
//    val voteAverage: Double? = null,
//    @SerialName("vote_count")
//    val voteCount: Int? = null
//)

@Serializable
data class NetworkTMDBTvGuestStar(
    @SerialName("adult")
    val adult: Boolean? = null,
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

fun NetworkTMDBTvSeasons.asExternalModel(): TvSeasons = TvSeasons(
    airDate = airDate,
    credits = credits?.asExternalModel(),
    episodes = episodes?.asExternalModel(),
    _id = _id,
    id = id,
    images = images?.asExternalModel(),
    name = name,
    networks = networks?.asExternalModel(),
    overview = overview,
    posterPath = posterPath,
    seasonNumber = seasonNumber,
    videos = videos?.asExternalModel(),
    voteAverage = voteAverage
)

@JvmName("asExternalModelTvSeasons")
fun List<NetworkTMDBTvEpisode>.asExternalModel(): List<TvEpisode> = map {
    TvEpisode(
        airDate = it.airDate,
        crew = it.crew?.asExternalModel(),
        episodeNumber = it.episodeNumber,
        episodeType = it.episodeType,
        guestStars = it.guestStars?.asExternalModel(),
        id = it.id,
        name = it.name,
        overview = it.overview,
        productionCode = it.productionCode,
        runtime = it.runtime,
        seasonNumber = it.seasonNumber,
        showId = it.showId,
        stillPath = it.stillPath,
        voteAverage = it.voteAverage,
        voteCount = it.voteCount
    )
}

@JvmName("asExternalModelTvEpisode")
fun List<NetworkTMDBTvGuestStar>.asExternalModel(): List<TvGuestStar> = map {
    TvGuestStar(
        adult = it.adult,
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