package com.bowoon.convention

enum class AppBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(applicationIdSuffix = ".debug"),
    RELEASE,
}
