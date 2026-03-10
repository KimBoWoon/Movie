package com.cheeke.convention

enum class AppBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(applicationIdSuffix = ".debug"),
    RELEASE,
}
