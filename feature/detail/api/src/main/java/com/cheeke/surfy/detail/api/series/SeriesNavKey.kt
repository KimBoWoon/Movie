package com.cheeke.surfy.detail.api.series

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SeriesNavKey(
    val id: Int
) : NavKey