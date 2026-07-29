package com.cheeke.surfy.network.impl.model

import com.cheeke.surfy.model.ImageList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBImageList(
    @SerialName("backdrops")
    val backdrops: List<NetworkTMDBImage>? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("posters")
    val posters: List<NetworkTMDBImage>? = null
)

fun NetworkTMDBImageList.asExternalModel(): ImageList = ImageList(
    backdrops = backdrops?.asExternalModel(),
    id = id,
    posters = posters?.asExternalModel()
)