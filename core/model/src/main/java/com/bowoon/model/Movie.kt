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
    val genres: List<Genre>? = null,
    val homepage: String? = null,
    val id: Int? = null,
    val images: Images? = null,
    val imdbId: String? = null,
    val keywords: Keywords? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val productionCompanies: List<ProductionCompany>? = null,
    val productionCountries: List<ProductionCountry>? = null,
    val releaseDate: String? = null,
    val releases: Releases? = null,
    val revenue: Long? = null,
    val reviews: MovieReviews? = null,
    val runtime: Int? = null,
    val spokenLanguages: List<SpokenLanguage>? = null,
    val status: String? = null,
    val tagline: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val videos: Videos? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val certification: String? = null,
    val isFavorite: Boolean = false
) : Parcelable

@Serializable
@Parcelize
data class AlternativeTitles(
    val titles: List<AlternativeTitle>? = null
) : Parcelable

@Serializable
@Parcelize
data class AlternativeTitle(
    val iso31661: String? = null,
    val title: String? = null,
    val type: String? = null
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
data class Credits(
    val cast: List<Cast>? = null,
    val crew: List<Crew>? = null
) : Parcelable

sealed interface CreditInfo {
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

@Serializable
@Parcelize
data class Keywords(
    val keywords: List<Keyword>? = null
) : Parcelable

@Serializable
@Parcelize
data class Keyword(
    val id: Int? = null,
    val name: String? = null
) : Parcelable

@Serializable
@Parcelize
data class ProductionCompany(
    val id: Int? = null,
    val logoPath: String? = null,
    val name: String? = null,
    val originCountry: String? = null
) : Parcelable

@Serializable
@Parcelize
data class ProductionCountry(
    val iso31661: String? = null,
    val name: String? = null
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

//@Serializable
//@Parcelize
//data class Result(
//    val adult: Boolean? = null,
//    val backdropPath: String? = null,
//    val genreIds: List<Int>? = null,
//    val id: Int? = null,
//    val originalLanguage: String? = null,
//    val originalTitle: String? = null,
//    val overview: String? = null,
//    val popularity: Double? = null,
//    val posterPath: String? = null,
//    val releaseDate: String? = null,
//    val title: String? = null,
//    val video: Boolean? = null,
//    val voteAverage: Double? = null,
//    val voteCount: Int? = null
//) : Parcelable

@Serializable
@Parcelize
data class SpokenLanguage(
    val englishName: String? = null,
    val iso6391: String? = null,
    val name: String? = null
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

@Serializable
@Parcelize
data class Videos(
    val results: List<VideoInfo>? = null
) : Parcelable

@Serializable
@Parcelize
data class VideoInfo(
    val id: String? = null,
    val iso31661: String? = null,
    val iso6391: String? = null,
    val key: String? = null,
    val name: String? = null,
    val official: Boolean? = null,
    val publishedAt: String? = null,
    val site: String? = null,
    val size: Int? = null,
    val type: String? = null
) : Parcelable

//@Serializable
//@Parcelize
//data class MovieLists(
//    val id: Int? = null,
//    val page: Int? = null,
//    val results: List<MovieListResult?>? = null,
//    val totalPages: Int? = null,
//    val totalResults: Int? = null
//) : Parcelable
//
//@Serializable
//@Parcelize
//data class MovieListResult(
//    val description: String? = null,
//    val favoriteCount: Int? = null,
//    val id: Int? = null,
//    val iso6391: String? = null,
//    val itemCount: Int? = null,
//    val listType: String? = null,
//    val name: String? = null,
//    val posterPath: String? = null
//) : Parcelable

//@Serializable
//@Parcelize
//data class Reviews(
//    val id: Int? = null,
//    val page: Int? = null,
//    val results: List<ReviewsResult?>? = null,
//    val totalPages: Int? = null,
//    val totalResults: Int? = null
//) : Parcelable
//
//@Serializable
//@Parcelize
//data class ReviewsResult(
//    val author: String? = null,
//    val authorDetails: AuthorDetails? = null,
//    val content: String? = null,
//    val createdAt: String? = null,
//    val id: String? = null,
//    val updatedAt: String? = null,
//    val url: String? = null
//) : Parcelable

//@Serializable
//@Parcelize
//data class AuthorDetails(
//    val avatarPath: String? = null,
//    val name: String? = null,
//    val rating: Int? = null,
//    val username: String? = null
//) : Parcelable