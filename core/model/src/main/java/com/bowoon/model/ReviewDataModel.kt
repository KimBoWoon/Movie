package com.bowoon.model

sealed interface ReviewDataModel {
    object Separator : ReviewDataModel
    data class Item(val review: Review) : ReviewDataModel
}