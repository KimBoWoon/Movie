plugins {
    id("bowoon.library")
    id("bowoon.hilt")
}

android {
    namespace = "com.bowoon.sync"
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
}