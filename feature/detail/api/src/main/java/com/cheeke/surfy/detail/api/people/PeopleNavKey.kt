package com.cheeke.surfy.detail.api.people

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class PeopleNavKey(
    val id: Int
) : NavKey