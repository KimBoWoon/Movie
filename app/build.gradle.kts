plugins {
    id("bowoon.application")
    id("bowoon.application.compose")
    id("bowoon.hilt")
}

dependencies {
    arrayOf(
        projects.core.ui
    ).forEach {
        implementation(it)
    }
}