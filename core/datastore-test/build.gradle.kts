plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.datastore_test"
}

dependencies {
    implementation(libs.hilt.android.testing)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
}