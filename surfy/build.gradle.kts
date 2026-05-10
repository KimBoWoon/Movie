plugins {
    alias(libs.plugins.cheeke.android.application)
    alias(libs.plugins.cheeke.android.application.compose)
    alias(libs.plugins.cheeke.hilt)
    alias(libs.plugins.cheeke.android.application.firebase)
    alias(libs.plugins.cheeke.android.application.flavors)
}

dependencies {
    arrayOf(
        project(":core:common"),
        project(":core:data"),
        project(":core:ui"),
        project(":core:sync"),
        project(":core:model"),
        project(":core:notifications"),
        project(":core:firebase"),
        project(":core:domain"),
        project(":core:analytics"),
        project(":feature:home"),
        project(":feature:detail"),
        project(":feature:search"),
        project(":feature:favorite"),
        project(":feature:setting"),
        libs.coil.compose,
        libs.androidx.compose.material3.navigationSuite,
        libs.androidx.compose.material3.adaptive.navigation3,
        libs.androidx.lifecycle.viewmodel.navigation3,
        libs.androidx.work.ktx,
        libs.androidx.splash,
        libs.androidx.startup,
        libs.hilt.ext.work,

        libs.androidx.compose.hilt.navigation,
    ).forEach {
        implementation(it)
    }

    ksp(libs.hilt.ext.compiler)

    arrayOf(
        libs.hilt.android.testing,
        project(":core:testing")
    ).forEach {
        androidTestImplementation(it)
    }

    arrayOf(
        project(":core:datastore-test"),
        project(":core:testing"),
        libs.hilt.android.testing,
        libs.kotlin.test,
        libs.androidx.navigation.testing,
        libs.robolectric,
        libs.androidx.compose.ui.test.junit4
    ).forEach {
        testImplementation(it)
    }
}