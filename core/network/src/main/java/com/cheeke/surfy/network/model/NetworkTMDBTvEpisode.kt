package com.cheeke.surfy.network.model

import com.cheeke.surfy.model.TvEpisode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBTvEpisode(
    @SerialName("air_date")
    val airDate: String? = null,
    @SerialName("credits")
    val credits: NetworkTMDBCredits? = null,
    @SerialName("crew")
    val crew: List<NetworkTMDBCrew>? = null,
    @SerialName("episode_number")
    val episodeNumber: Int? = null,
    @SerialName("episode_type")
    val episodeType: String? = null,
    @SerialName("guest_stars")
    val guestStars: List<NetworkTMDBTvGuestStar>? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("images")
    val images: NetworkTMDBImages? = null,
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
    @SerialName("still_path")
    val stillPath: String? = null,
    @SerialName("show_id")
    val showId: Int? = null,
    @SerialName("videos")
    val videos: NetworkTMDBVideos? = null,
    @SerialName("vote_average")
    val voteAverage: Float? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null
)

fun NetworkTMDBTvEpisode.asExternalModel(): TvEpisode = TvEpisode(
    airDate = airDate,
    crew = crew?.asExternalModel(),
    episodeNumber = episodeNumber,
    episodeType = episodeType,
    guestStars = guestStars?.asExternalModel(),
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