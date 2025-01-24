plugins {
    id("bowoon.application")
    id("bowoon.application.compose")
    id("bowoon.hilt")
    id("bowoon.android.application.firebase")
}

android {
    namespace = "com.bowoon.movie"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
        compileSdk = 35
        targetSdk = 35
        applicationId = "com.bowoon.movie"
        versionCode = 1
        versionName = "0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.named("debug").get()
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.named("debug").get()
        }
    }
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
        libs.androidx.work.ktx,
        libs.androidx.splash,
        libs.androidx.startup,
        libs.hilt.ext.work
    ).forEach {
        implementation(it)
    }

    ksp(libs.hilt.compiler)
}
