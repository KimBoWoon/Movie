package com.bowoon.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }

        composeOptions.apply {
            kotlinCompilerExtensionVersion = libs.findVersion("compose.compiler").get().toString()
        }

        dependencies {
            val bom = libs.findLibrary("androidx.compose.bom").get()
            "implementation"(dependency = platform(bom))
            "implementation"(dependency = libs.findLibrary("androidx.activity.compose").get())
            "implementation"(dependency = libs.findLibrary("androidx.compose.ui").get())
            "implementation"(dependency = libs.findLibrary("androidx.compose.ui.graphics").get())
            "implementation"(dependency = libs.findLibrary("androidx.compose.ui.tooling.preview").get())
            "implementation"(dependency = libs.findLibrary("androidx.compose.material3").get())
            "debugImplementation"(dependency = libs.findLibrary("androidx.compose.ui.tooling").get())
            "androidTestImplementation"(dependency = platform(bom))
            "androidTestImplementation"(dependency = libs.findLibrary("androidx.compose.ui.test.junit4").get())
            "androidTestImplementation"(dependency = libs.findLibrary("androidx.compose.ui.test.manifest").get())
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.freeCompilerArgs.set(compilerOptions.freeCompilerArgs.get() + buildComposeMetricsParameters())
    }
}

private fun Project.buildComposeMetricsParameters(): List<String> {
    val metricParameters = mutableListOf<String>()
    val enableMetricsProvider = project.providers.gradleProperty("enableComposeCompilerMetrics")
    val enableMetrics = (enableMetricsProvider.orNull == "true")
    if (enableMetrics) {
        val metricsFolder = File(project.layout.buildDirectory.toString(), "compose-metrics")
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + metricsFolder.absolutePath
        )
    }

    val enableReportsProvider = project.providers.gradleProperty("enableComposeCompilerReports")
    val enableReports = (enableReportsProvider.orNull == "true")
    if (enableReports) {
        val reportsFolder = File(project.layout.buildDirectory.toString(), "compose-reports")
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + reportsFolder.absolutePath
        )
    }
    return metricParameters.toList()
}
