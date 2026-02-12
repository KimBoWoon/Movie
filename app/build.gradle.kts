plugins {
    alias(libs.plugins.bowoon.android.application)
    alias(libs.plugins.bowoon.android.application.compose)
    alias(libs.plugins.bowoon.hilt)
    alias(libs.plugins.bowoon.android.application.firebase)
    alias(libs.plugins.bowoon.android.application.flavors)
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.data,
        projects.core.ui,
        projects.core.sync,
        projects.core.model,
        projects.core.notifications,
        projects.core.firebase,
        projects.core.domain,
        projects.core.navigation,
        projects.feature.home,
        projects.feature.detail,
        projects.feature.search,
        projects.feature.favorite,
        projects.feature.my,
        libs.coil.compose,
        libs.androidx.compose.material3.navigationSuite,
        libs.androidx.compose.material3.adaptive.navigation3,
        libs.androidx.work.ktx,
        libs.androidx.splash,
        libs.androidx.startup,
        libs.hilt.ext.work
    ).forEach {
        implementation(it)
    }

    ksp(libs.hilt.ext.compiler)

    arrayOf(
        libs.hilt.android.testing,
        projects.core.testing
    ).forEach {
        androidTestImplementation(it)
    }

    arrayOf(
        projects.core.datastoreTest,
        projects.core.testing,
        libs.hilt.android.testing,
        libs.kotlin.test,
        libs.androidx.navigation.testing,
        libs.robolectric,
        libs.androidx.compose.ui.test.junit4
    ).forEach {
        testImplementation(it)
    }
}