plugins {
    alias(libs.plugins.cheeke.android.library)
}

android {
    namespace = "com.cheeke.surfy.core.datamanager.api"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    testFixtures {
        enable = true
    }
}

dependencies {
    arrayOf(
        project(":core:model")
    ).forEach {
        api(it)
    }

    testFixturesImplementation("androidx.annotation:annotation:1.10.0")
    testFixturesImplementation(libs.kotlinx.coroutines.core)
}