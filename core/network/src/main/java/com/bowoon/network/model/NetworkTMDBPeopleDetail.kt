package com.bowoon.network.model

import com.bowoon.model.TMDBPeopleDetail
import com.bowoon.model.TMDBPeopleImages
import com.bowoon.model.TMDBPeopleProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTMDBPeopleDetail(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("also_known_as")
    val alsoKnownAs: List<String>? = null,
    @SerialName("biography")
    val biography: String? = null,
    @SerialName("birthday")
    val birthday: String? = null,
    @SerialName("deathday")
    val deathday: String? = null,
    @SerialName("gender")
    val gender: Int? = null,
    @SerialName("homepage")
    val homepage: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("images")
    val images: NetworkTMDBPeopleImages? = null,
    @SerialName("imdb_id")
    val imdbId: String? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("place_of_birth")
    val placeOfBirth: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("profile_path")
    val profilePath: String? = null
)

@Serializable
data class NetworkTMDBPeopleImages(
    @SerialName("profiles")
    val profiles: List<NetworkTMDBPeopleProfile>? = null
)

@Serializable
data class NetworkTMDBPeopleProfile(
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

fun NetworkTMDBPeopleDetail.asExternalModel(): TMDBPeopleDetail =
    TMDBPeopleDetail(
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
        name = name,
        placeOfBirth = placeOfBirth,
        popularity = popularity,
        profilePath = profilePath
    )

fun NetworkTMDBPeopleImages.asExternalModel(): TMDBPeopleImages =
    TMDBPeopleImages(
        profiles = profiles?.asExternalModel()
    )

fun List<NetworkTMDBPeopleProfile>.asExternalModel(): List<TMDBPeopleProfile> =
    map {
        TMDBPeopleProfile(
            aspectRatio = it.aspectRatio,
            filePath = it.filePath,
            height = it.height,
            iso6391 = it.iso6391,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount,
            width = it.width
        )
    }