plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.domain"
}

dependencies {
    arrayOf(
        projects.core.common
    ).forEach {
        implementation(it)
    }

    api(projects.core.data)
    api(projects.core.model)

    testImplementation(libs.androidx.paging.testing)
}