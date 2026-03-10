package com.cheeke.surfy.network.model

import com.cheeke.surfy.model.MovieWatchProvider
import com.cheeke.surfy.model.MovieWatchProviderItem
import com.cheeke.surfy.model.MovieWatchProviderResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBMovieWatchProvider(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("results")
    val results: Map<String, NetworkTMDBMovieWatchProviderResult>? = null
)

@Serializable
data class NetworkTMDBMovieWatchProviderResult(
    @SerialName("link")
    val link: String? = null,
    @SerialName("flatrate")
    val flatrate: List<NetworkTMDBMovieWatchProviderItem>? = null,
    @SerialName("buy")
    val buy: List<NetworkTMDBMovieWatchProviderItem>? = null,
    @SerialName("rent")
    val rent: List<NetworkTMDBMovieWatchProviderItem>? = null
)

@Serializable
data class NetworkTMDBMovieWatchProviderItem(
    @SerialName("logo_path")
    val logoPath: String? = null,
    @SerialName("provider_id")
    val providerId: Int? = null,
    @SerialName("provider_name")
    val providerName: String? = null,
    @SerialName("display_priority")
    val displayPriority: Int? = null
)

fun NetworkTMDBMovieWatchProvider.asExternalModel(): MovieWatchProvider = MovieWatchProvider(
    id = id,
    results = results.orEmpty().mapValues { (_, dto) -> dto.asExternalModel() }
)

fun NetworkTMDBMovieWatchProviderResult.asExternalModel(): MovieWatchProviderResult = MovieWatchProviderResult(
    link = link,
    flatrate = flatrate?.asExternalModel(),
    buy = buy?.asExternalModel(),
    rent = rent?.asExternalModel()
)

@JvmName("asExternalModelNetworkTMDBMovieWatchProviderItem")
fun List<NetworkTMDBMovieWatchProviderItem>.asExternalModel(): List<MovieWatchProviderItem> = map {
    MovieWatchProviderItem(
        logoPath = it.logoPath,
        providerId = it.providerId,
        providerName = it.providerName,
        displayPriority = it.displayPriority
    )
}