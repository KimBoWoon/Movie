package com.cheeke.surfy.convention

enum class AppBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(applicationIdSuffix = ".debug"),
    RELEASE,
}
