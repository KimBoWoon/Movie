import com.android.build.api.dsl.LibraryExtension
import com.cheeke.convention.configureGradleManagedDevices
import com.cheeke.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "cheeke.android.library")
            apply(plugin = "cheeke.hilt")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(commonExtension = this)
            }

            dependencies {
                "implementation"(project(":core:ui"))
                "implementation"(dependency = libs.findLibrary("androidx.compose.material3.icons").get())
                "implementation"(dependency = libs.findLibrary("androidx.compose.hilt.navigation").get())
                "implementation"(dependency = libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                "implementation"(dependency = libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                "implementation"(dependency = libs.findLibrary("androidx.navigation.compose").get())
                "implementation"(dependency = libs.findLibrary("kotlinx.serialization.json").get())
                "testImplementation"(dependency = libs.findLibrary("androidx.navigation.testing").get())
                "androidTestImplementation"(dependency = libs.findLibrary("androidx.lifecycle.runtimeTesting").get())
            }
        }
    }
}