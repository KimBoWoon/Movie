plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
}

android {
    namespace = "com.cheeke.surfy.core.sync"
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.model,
        projects.core.network,
        projects.core.data,
        projects.core.notifications,
        libs.androidx.work.ktx,
        libs.hilt.ext.work
    ).forEach {
        implementation(it)
    }

    ksp(libs.hilt.ext.compiler)
}