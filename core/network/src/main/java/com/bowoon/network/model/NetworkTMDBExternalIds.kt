package com.bowoon.network.model


import com.bowoon.model.TMDBExternalIds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBExternalIds(
    @SerialName("facebook_id")
    val facebookId: String? = null,
    @SerialName("freebase_id")
    val freebaseId: String? = null,
    @SerialName("freebase_mid")
    val freebaseMid: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("imdb_id")
    val imdbId: String? = null,
    @SerialName("instagram_id")
    val instagramId: String? = null,
    @SerialName("tiktok_id")
    val tiktokId: String? = null,
    @SerialName("tvrage_id")
    val tvrageId: Int? = null,
    @SerialName("twitter_id")
    val twitterId: String? = null,
    @SerialName("wikidata_id")
    val wikidataId: String? = null,
    @SerialName("youtube_id")
    val youtubeId: String? = null
)

fun NetworkTMDBExternalIds.asExternalModel(): TMDBExternalIds =
    TMDBExternalIds(
        facebookId = facebookId,
        freebaseId = freebaseId,
        freebaseMid = freebaseMid,
        id = id,
        imdbId = imdbId,
        instagramId = instagramId,
        tiktokId = tiktokId,
        tvrageId = tvrageId,
        twitterId = twitterId,
        wikidataId = wikidataId,
        youtubeId = youtubeId
    )