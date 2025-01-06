plugins {
    id("bowoon.library")
    id("bowoon.hilt")
}

android {
    namespace = "com.bowoon.database"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.model,
        libs.kotlinx.serialization.json,
        libs.androidx.junit,
        libs.androidx.datastore,
        libs.androidx.room.runtime,
        libs.androidx.room.ktx,
        libs.threetenabp
    ).forEach {
        implementation(it)
    }

    arrayOf(
        libs.androidx.room.compiler
    ).forEach {
        ksp(it)
    }
}