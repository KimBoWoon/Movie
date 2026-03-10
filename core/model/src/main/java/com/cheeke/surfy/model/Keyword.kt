package com.cheeke.surfy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Keyword(
    val id: Int? = null,
    val name: String? = null
) : Parcelable