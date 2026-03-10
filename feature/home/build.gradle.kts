plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
    alias(libs.plugins.cheeke.android.feature)
}

android {
    namespace = "com.cheeke.surfy.feature.home"
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
        libs.coil.compose,
        libs.androidx.navigation3.runtime,
        libs.androidx.compose.hilt.navigation,
        libs.androidx.compose.paging
    ).forEach {
        implementation(it)
    }

    testImplementation(libs.androidx.paging.testing)
}