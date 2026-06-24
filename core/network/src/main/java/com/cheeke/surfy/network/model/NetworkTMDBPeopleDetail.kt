package com.cheeke.surfy.network.model

import com.cheeke.surfy.model.Image
import com.cheeke.surfy.model.People
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBPeopleDetail(
    @SerialName(value = "adult")
    val adult: Boolean? = null,
    @SerialName(value = "also_known_as")
    val alsoKnownAs: List<String>? = null,
    @SerialName(value = "biography")
    val biography: String? = null,
    @SerialName(value = "birthday")
    val birthday: String? = null,
    @SerialName(value = "deathday")
    val deathday: String? = null,
    @SerialName(value = "gender")
    val gender: Int? = null,
    @SerialName(value = "homepage")
    val homepage: String? = null,
    @SerialName(value = "id")
    val id: Int? = null,
    @SerialName(value = "images")
    val images: NetworkTMDBPeopleImages? = null,
    @SerialName(value = "imdb_id")
    val imdbId: String? = null,
    @SerialName(value = "known_for_department")
    val knownForDepartment: String? = null,
    @SerialName(value = "name")
    val name: String? = null,
    @SerialName(value = "place_of_birth")
    val placeOfBirth: String? = null,
    @SerialName(value = "popularity")
    val popularity: Double? = null,
    @SerialName(value = "profile_path")
    val profilePath: String? = null
)

@Serializable
data class NetworkTMDBPeopleImages(
    @SerialName(value = "profiles")
    val profiles: List<NetworkTMDBPeopleProfile>? = null
)

@Serializable
data class NetworkTMDBPeopleProfile(
    @SerialName(value = "aspect_ratio")
    val aspectRatio: Double? = null,
    @SerialName(value = "file_path")
    val filePath: String? = null,
    @SerialName(value = "height")
    val height: Int? = null,
    @SerialName(value = "iso_639_1")
    val iso6391: String? = null,
    @SerialName(value = "vote_average")
    val voteAverage: Float? = null,
    @SerialName(value = "vote_count")
    val voteCount: Int? = null,
    @SerialName(value = "width")
    val width: Int? = null
)

fun NetworkTMDBPeopleDetail.asExternalModel(): People =
    People(
        adult = adult,
        alsoKnownAs = alsoKnownAs,
        biography = biography,
        birthday = birthday,
        deathday = deathday,
        gender = gender,
        homepage = homepage,
        id = id,
        images = images?.asExternalModel(),
        imdbId = imdbId,
        knownForDepartment = knownForDepartment,
        title = name,
        placeOfBirth = placeOfBirth,
        popularity = popularity,
        posterPath = profilePath
    )

fun NetworkTMDBPeopleImages.asExternalModel(): List<Image> =
    profiles?.map {
        Image(
            aspectRatio = it.aspectRatio,
            filePath = it.filePath,
            height = it.height,
            iso6391 = it.iso6391,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount,
            width = it.width
        )
    }.orEmpty()

fun List<NetworkTMDBPeopleProfile>.asExternalModel(): List<Image> =
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