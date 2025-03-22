plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
}

android {
    namespace = "com.bowoon.datastore_test"
}

dependencies {
    implementation(libs.hilt.android.testing)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
}