package com.cheeke.surfy.detail.api.movie

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class MovieNavKey(
    val id: Int
) : NavKey