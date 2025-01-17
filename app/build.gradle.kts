plugins {
    id("bowoon.application")
    id("bowoon.application.compose")
    id("bowoon.hilt")
    id("bowoon.android.application.firebase")
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.data,
        projects.core.ui,
        projects.core.sync,
        projects.feature.home,
        projects.feature.detail,
        projects.feature.search,
        projects.feature.favorite,
        projects.feature.my,
        projects.feature.people,
        libs.coil.compose,
        libs.androidx.lifecycle.runtime.ktx,
        libs.androidx.navigation.compose,
        libs.androidx.compose.material3.navigationSuite,
        libs.threetenabp,
        "androidx.core:core-splashscreen:1.0.0"
    ).forEach {
        implementation(it)
    }
}