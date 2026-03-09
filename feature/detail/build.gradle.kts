plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.android.library.compose)
    alias(libs.plugins.bowoon.android.feature)
}

android {
    namespace = "com.bowoon.surfy.feature.detail"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.data,
        projects.core.domain,
        projects.core.datastore,
        projects.core.model,
        projects.core.notifications,
        projects.core.firebase,
        projects.core.navigation,
        projects.core.analytics,
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.media3.exoplayer,
        libs.androidx.media3.ui,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.androidx.paging.common)
    testImplementation(libs.turbine)

    androidTestImplementation(projects.core.testing)
}