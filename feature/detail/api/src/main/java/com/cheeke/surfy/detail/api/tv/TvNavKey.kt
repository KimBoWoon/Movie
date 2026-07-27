package com.cheeke.surfy.detail.api.tv

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class TvNavKey(
    val id: Int
) : NavKey