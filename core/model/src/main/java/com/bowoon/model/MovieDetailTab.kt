package com.bowoon.model

enum class MovieDetailTab(val label: String) {
    MOVIE_INFO(label = "영화 정보"),
    SERIES(label = "시리즈"),
    ACTOR_AND_CREW(label = "배우 / 감독"),
    REVIEWS(label = "리뷰"),
    IMAGES(label = "이미지"),
    SIMILAR(label = "다른 영화")
}