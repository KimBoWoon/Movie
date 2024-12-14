plugins {
    id("bowoon.library")
    id("bowoon.hilt")
}

android {
    namespace = "com.bowoon.domain"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.model
    ).forEach {
        implementation(it)
    }
}