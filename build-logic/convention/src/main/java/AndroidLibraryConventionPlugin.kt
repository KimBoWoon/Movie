import com.android.build.gradle.LibraryExtension
import com.bowoon.convention.Config
import com.bowoon.convention.configureFlavors
import com.bowoon.convention.configureKotlinAndroid
import com.bowoon.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "kotlin-parcelize")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    compileSdk = Config.Library.MIN_SDK_VERSION
                    minSdk = Config.Library.COMPILE_SDK_VERSION
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    buildTypes {
                        release {
                            proguardFiles(
                                getDefaultProguardFile(Config.ApplicationSetting.defaultProguardFile),
                                Config.ApplicationSetting.proguardFile
                            )
                            buildConfigField("Boolean", "IS_DEBUGGING_LOGGING", "false")
                        }
                        debug {
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
                add("testImplementation", project(":core:testing"))
                add("androidTestImplementation", project(":core:testing"))
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