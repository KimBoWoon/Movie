package com.cheeke.surfy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class SpokenLanguage(
    val englishName: String? = null,
    val iso6391: String? = null,
    val name: String? = null
) : Parcelable