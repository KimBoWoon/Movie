import org.gradle.api.internal.provider.DefaultProviderFactory
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
    val logs = ByteArrayOutputStream().use {
        DefaultProviderFactory().exec {
            commandLine("git", "log", "--oneline", "develop", "-5")
            standardOutput = it
        }
//        DefaultExecOperations().exec {
//            commandLine = listOf("git", "log", "--oneline", "develop", "-5")
//            standardOutput = it
//        }
        it.toString().trim()
    }
    val result = """
$logs
""".trimIndent()
    releaseNote.delete()
    releaseNote.writeText(result)
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