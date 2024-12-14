import com.android.build.gradle.LibraryExtension
import com.bowoon.convention.Config
import com.bowoon.convention.configureFlavors
import com.bowoon.convention.configureKotlinAndroid
import com.bowoon.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.parcelize")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    compileSdk = Config.Library.MIN_SDK_VERSION
                    minSdk = Config.Library.COMPILE_SDK_VERSION
                    buildTypes {
                        release {
                            isMinifyEnabled = true
                            isJniDebuggable = false
                            buildConfigField("Boolean", "IS_DEBUGGING_LOGGING", "false")
                        }
                        debug {
                            isMinifyEnabled = false
                            buildConfigField("Boolean", "IS_DEBUGGING_LOGGING", "true")
                        }
                    }
                }

                testOptions {
                    unitTests {
                        isIncludeAndroidResources = true
                    }
                }

                configureFlavors(this)
                configureKotlinAndroid(this)
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx.core.ktx").get())
                add("implementation", libs.findLibrary("androidx.appcompat").get())
                add("implementation", libs.findLibrary("material").get())
                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx.junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx.espresso.core").get())
                add("androidTestImplementation", kotlin("test"))
                add("testImplementation", kotlin("test"))
                add("testImplementation", libs.findLibrary("robolectric").get())
            }
        }
    }
}