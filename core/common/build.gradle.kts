plugins {
    id("bowoon.library")
    id("bowoon.library.compose")
    id("bowoon.hilt")
}

android {
    namespace = "com.bowoon.common"
}

dependencies {
    arrayOf(
        libs.kotlinx.coroutines.test,
        libs.turbine
    ).forEach {
        testImplementation(it)
    }
}