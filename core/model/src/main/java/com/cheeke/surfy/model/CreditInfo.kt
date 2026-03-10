package com.cheeke.surfy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

interface CreditInfo {
    val adult: Boolean?
    val id: Int?
    val gender: Int?
    val name: String?
    val originalName: String?
    val popularity: Double?
    val profilePath: String?
}

@Serializable
@Parcelize
data class Credits(
    val cast: List<Cast>? = null,
    val crew: List<Crew>? = null
) : Parcelable

@Serializable
@Parcelize
data class Cast(
    override val adult: Boolean? = null,
    val castId: Int? = null,
    val character: String? = null,
    val creditId: String? = null,
    override val gender: Int? = null,
    override val id: Int? = null,
    val knownForDepartment: String? = null,
    override val name: String? = null,
    val order: Int? = null,
    override val originalName: String? = null,
    override val popularity: Double? = null,
    override val profilePath: String? = null
) : Parcelable, CreditInfo

@Serializable
@Parcelize
data class Crew(
    override val adult: Boolean? = null,
    val creditId: String? = null,
    val department: String? = null,
    override val gender: Int? = null,
    override val id: Int? = null,
    val job: String? = null,
    val knownForDepartment: String? = null,
    override val name: String? = null,
    override val originalName: String? = null,
    override val popularity: Double? = null,
    override val profilePath: String? = null
) : Parcelable, CreditInfo