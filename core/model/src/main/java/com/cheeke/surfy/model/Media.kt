package com.cheeke.surfy.model

interface Media {
    val id: Int?
    val certification: String?
    val posterPath: String?
    val tagline: String?
    val title: String?
    val runtime: Int?
    val originalTitle: String?
    val genres: List<Genre>?
    val releaseDate: String?
    val firstAirDate: String?
    val lastAirDate: String?
    val voteAverage: Float?
    val mediaType: MediaType
    val isFavorite: Boolean

    /**
     * deeplink
     */
    val deepLinkPath: String
    fun <VisitResult> accept(visitor: MediaVisitor<VisitResult>): VisitResult
}

interface MediaVisitor<VisitResult> {
    fun visitMovie(movie: Movie): VisitResult
    fun visitTv(tv: Tv): VisitResult
    fun visitPeople(people: People): VisitResult
    fun visitSeries(series: Series): VisitResult
}