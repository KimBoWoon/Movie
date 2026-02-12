package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Movie(
    val adult: Boolean? = null,
    val alternativeTitles: AlternativeTitles? = null,
    val backdropPath: String? = null,
    val belongsToCollection: BelongsToCollection? = null,
    val budget: Long? = null,
    val credits: Credits? = null,
    override val genres: List<Genre>? = null,
    val homepage: String? = null,
    override val id: Int? = null,
    val images: Images? = null,
    val imdbId: String? = null,
    val keywords: Keywords? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    override val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    override val posterPath: String? = null,
    val productionCompanies: List<ProductionCompany>? = null,
    val productionCountries: List<ProductionCountry>? = null,
    override val releaseDate: String? = null,
    val releases: Releases? = null,
    val revenue: Long? = null,
    val reviews: Reviews? = null,
    val runtime: Int? = null,
    val spokenLanguages: List<SpokenLanguage>? = null,
    val status: String? = null,
    val tagline: String? = null,
    override val title: String? = null,
    val video: Boolean? = null,
    val videos: Videos? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val certification: String? = null,
    val series: Series? = null
) : Parcelable, Media

@Serializable
@Parcelize
data class AlternativeTitles(
    val titles: List<AlternativeTitle>? = null
) : Parcelable

@Serializable
@Parcelize
data class BelongsToCollection(
    val backdropPath: String? = null,
    val id: Int? = null,
    val name: String? = null,
    val posterPath: String? = null
) : Parcelable

@Serializable
@Parcelize
data class Backdrop(
    val filePath: String? = null
) : Parcelable

@Serializable
@Parcelize
data class Keywords(
    val keywords: List<Keyword>? = null
) : Parcelable

@Serializable
@Parcelize
data class Releases(
    val countries: List<Country>? = null
) : Parcelable

@Serializable
@Parcelize
data class Country(
    val certification: String? = null,
    val descriptors: List<String>? = null,
    val iso31661: String? = null,
    val primary: Boolean? = null,
    val releaseDate: String? = null
) : Parcelable

@Serializable
@Parcelize
data class Translations(
    val translations: List<Translation>? = null
) : Parcelable

@Serializable
@Parcelize
data class Translation(
    val translationInfo: TranslationInfo? = null,
    val englishName: String? = null,
    val iso31661: String? = null,
    val iso6391: String? = null,
    val name: String? = null
) : Parcelable

@Serializable
@Parcelize
data class TranslationInfo(
    val homepage: String? = null,
    val overview: String? = null,
    val runtime: Int? = null,
    val tagline: String? = null,
    val title: String? = null
) : Parcelable