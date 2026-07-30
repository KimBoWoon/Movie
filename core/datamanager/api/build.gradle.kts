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
}

dependencies {
    arrayOf(
        project(":core:model")
    ).forEach {
        api(it)
    }
}