plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.datamanager.impl"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    arrayOf(
        project(":core:database:impl"),
        project(":core:network:api"),
        project(":core:datamanager:api"),
        project(":core:userdata:api")
    ).forEach {
        implementation(it)
    }

    arrayOf(
        project(":core:common"),
    ).forEach {
        api(it)
    }

    arrayOf(
        libs.kotlinx.coroutines.test,
        libs.kotlinx.serialization.json,
        project(":core:testing")
    ).forEach {
        androidTestImplementation(it)
    }
}