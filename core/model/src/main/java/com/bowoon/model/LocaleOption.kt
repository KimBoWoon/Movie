package com.bowoon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class LocaleOption(
    val code: String = "",
    val label: String = "",
    val isSelected: Boolean = false
) : Parcelable