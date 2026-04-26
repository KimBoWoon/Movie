plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.android.library.compose)
}

android {
    namespace = "com.cheeke.surfy.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(project(":core:model"))
//    implementation(libs.androidx.compose.material3.navigationSuite)
//    implementation(libs.androidx.compose.material3.adaptive.navigation3)
//    implementation(libs.androidx.savedstate.compose)
//    implementation(libs.androidx.lifecycle.viewModel.navigation3)

//    testImplementation(libs.truth)

//    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
//    androidTestImplementation(libs.androidx.test.ext)
//    androidTestImplementation(libs.androidx.compose.ui.testManifest)
//    androidTestImplementation(libs.androidx.lifecycle.viewModel.testing)
//    androidTestImplementation(libs.truth)
}