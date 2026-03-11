plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.datastore_test"
}

dependencies {
    arrayOf(
        libs.hilt.android.testing,
        project(":core:common"),
        project(":core:datastore")
    ).forEach {
        implementation(it)
    }
}