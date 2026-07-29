package com.cheeke.surfy.network.impl.model

import com.cheeke.surfy.model.ExternalIds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBExternalIds(
    @SerialName(value = "facebook_id")
    val facebookId: String? = null,
    @SerialName(value = "freebase_id")
    val freebaseId: String? = null,
    @SerialName(value = "freebase_mid")
    val freebaseMid: String? = null,
    @SerialName(value = "id")
    val id: Int? = null,
    @SerialName(value = "imdb_id")
    val imdbId: String? = null,
    @SerialName(value = "instagram_id")
    val instagramId: String? = null,
    @SerialName(value = "tiktok_id")
    val tiktokId: String? = null,
    @SerialName(value = "tvrage_id")
    val tvrageId: Int? = null,
    @SerialName(value = "twitter_id")
    val twitterId: String? = null,
    @SerialName(value = "wikidata_id")
    val wikidataId: String? = null,
    @SerialName(value = "youtube_id")
    val youtubeId: String? = null
)

fun NetworkTMDBExternalIds.asExternalModel(): ExternalIds =
    ExternalIds(
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