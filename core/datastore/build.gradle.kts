plugins {
    id("bowoon.library")
    id("bowoon.hilt")
}

android {
    namespace = "com.bowoon.datastore"
}

dependencies {
    arrayOf(
        projects.core.common,
        libs.kotlinx.serialization.json,
        libs.androidx.junit,
        libs.androidx.datastore,
        projects.core.model
    ).forEach {
        implementation(it)
    }

//    testImplementation(projects.core.testing)
}