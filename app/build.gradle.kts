import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.bowoon.android.application)
    alias(libs.plugins.bowoon.android.application.compose)
    alias(libs.plugins.bowoon.hilt)
    alias(libs.plugins.bowoon.android.application.firebase)
    alias(libs.plugins.bowoon.android.application.flavors)
}

tasks.register("createReleaseNote") {
    val releaseNote = File("releaseNote.txt")
    val logs = ByteArrayOutputStream().use { os ->
        exec {
            executable = "git"
            args = listOf<String>("log", "HEAD..develop", "--oneline")
            standardOutput = os
        }
        os.toString().trim()
    }
    releaseNote.delete()
    releaseNote.writeText(
        text = logs.takeIf { it.isNotEmpty() }?.trimIndent() ?: "empty logs..."
    )
//    val logs = ByteArrayOutputStream().use {
////        DefaultProviderFactory().exec {
////            commandLine("git", "log", "release/movie_v1.0.0", "--oneline")
////            standardOutput = it
////        }
////        DefaultExecOperations().exec {
////            commandLine = listOf("git", "log", "release/movie_v1.0.0", "--oneline")
////            standardOutput = it
////        }
////        exec {
////            commandLine("git", "log", "HEAD", "..", "develop", "--oneline")
////            standardOutput = it
////        }
////        it.toString().trim()
//    }
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.data,
        projects.core.ui,
        projects.core.sync,
        projects.core.model,
        projects.core.notifications,
        projects.core.firebase,
        projects.core.domain,
        projects.feature.home,
        projects.feature.detail,
        projects.feature.search,
        projects.feature.favorite,
        projects.feature.my,
        projects.feature.people,
        projects.feature.series,
        libs.coil.compose,
        libs.androidx.navigation.compose,
        libs.androidx.compose.material3.navigationSuite,
        libs.threetenabp,
        libs.androidx.work.ktx,
        libs.androidx.splash,
        libs.androidx.startup,
        libs.hilt.ext.work
    ).forEach {
        implementation(it)
    }

//    ksp(libs.hilt.compiler)
    ksp(libs.hilt.ext.compiler)

    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(projects.core.testing)
}