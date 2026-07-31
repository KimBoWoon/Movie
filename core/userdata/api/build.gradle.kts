plugins {
    alias(libs.plugins.cheeke.android.library)
}

android {
    namespace = "com.cheeke.surfy.core.userdata.api"

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
        project(":core:common"),
        project(":core:model")
    ).forEach {
        api(it)
    }

    testFixturesImplementation(libs.kotlinx.coroutines.core)
}