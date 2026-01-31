plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.movie.core.domain"
}

dependencies {
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    arrayOf(
        projects.core.common
    ).forEach {
        implementation(it)
    }

    api(projects.core.data)
    api(projects.core.model)

    testImplementation(libs.androidx.paging.testing)
}