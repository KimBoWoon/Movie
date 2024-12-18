import com.android.build.gradle.LibraryExtension
import com.bowoon.convention.Config
import com.bowoon.convention.configureGradleManagedDevices
import com.bowoon.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    buildConfigField("String", "KOBIS_OPEN_API_KEY", "\"${Config.getProp("kobis_open_api_key")}\"")
                    buildConfigField("String", "KMDB_OPEN_API_KEY", "\"${Config.getProp("kmdb_open_api_key")}\"")
                }
            }

            dependencies {
                add("implementation", project(":core:ui"))
//                add("testImplementation", project(":core:testing"))

                add("implementation", libs.findLibrary("androidx.compose.hilt.navigation").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("implementation", libs.findLibrary("androidx.navigation.compose").get())
                add("implementation", libs.findLibrary("kotlinx.serialization.json").get())
                add("testImplementation", libs.findLibrary("androidx.navigation.testing").get())
                add("androidTestImplementation", libs.findLibrary("androidx.lifecycle.runtimeTesting").get())
            }
        }
    }
}