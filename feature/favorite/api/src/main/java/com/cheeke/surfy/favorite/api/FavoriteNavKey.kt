package com.cheeke.surfy.favorite.api

import androidx.annotation.Keep
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class FavoriteNavKey(
    val tab: FavoriteContentType = FavoriteContentType.MOVIE
) : NavKey