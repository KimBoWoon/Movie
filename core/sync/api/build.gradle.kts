plugins {
    alias(libs.plugins.cheeke.android.library)
}

android {
    namespace = "com.cheeke.surfy.core.sync.api"

    testFixtures {
        enable = true
    }
}

dependencies {
    testFixturesImplementation("androidx.annotation:annotation:1.10.0")
    testFixturesImplementation(libs.kotlinx.coroutines.core)
    testFixturesImplementation(project(":core:userdata:api"))
}