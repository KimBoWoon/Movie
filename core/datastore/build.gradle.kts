plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.movie.core.datastore"
}

dependencies {
    arrayOf(
        projects.core.common,
        libs.kotlinx.serialization.json,
        libs.androidx.junit,
    ).forEach {
        implementation(it)
    }

    api(libs.androidx.datastore)
    api(projects.core.model)

//    testImplementation(projects.core.testing)
}