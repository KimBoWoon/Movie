plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.movie.core.sync"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.model,
        projects.core.network,
        projects.core.data,
        libs.androidx.work.ktx,
        libs.hilt.ext.work,
        libs.threetenabp
    ).forEach {
        implementation(it)
    }

    ksp(libs.hilt.ext.compiler)
}