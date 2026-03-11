package com.cheeke.surfy.model

enum class SearchType(val label: String) {
    MULTI(label = "전체"),
    MOVIE(label = "영화"),
    TV(label = "TV"),
    PEOPLE(label = "인물"),
    SERIES(label = "시리즈")
}