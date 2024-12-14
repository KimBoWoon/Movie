plugins {
    id("bowoon.library")
}

android {
    namespace = "com.bowoon.model"
}

dependencies {
    arrayOf(
        libs.kotlinx.serialization.json
    ).forEach {
        implementation(it)
    }
}