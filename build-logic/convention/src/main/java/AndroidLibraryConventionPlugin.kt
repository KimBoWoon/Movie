import com.android.build.api.dsl.LibraryExtension
import com.cheeke.convention.Config
import com.cheeke.convention.configureFlavors
import com.cheeke.convention.configureKotlinAndroid
import com.cheeke.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "com.android.library")
            apply(plugin = "kotlin-parcelize")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    compileSdk = Config.Library.MIN_SDK_VERSION
                    minSdk = Config.Library.COMPILE_SDK_VERSION
                    testInstrumentationRunner = Config.ApplicationSetting.TEST_INSTRUMENTATION_RUNNER

                    buildTypes {
                        release {
                            proguardFiles(
                                getDefaultProguardFile(Config.ApplicationSetting.DEFAULT_PROGUARD_FILE),
                                Config.ApplicationSetting.PROGUARD_FILE
                            )
                            buildConfigField(type = "Boolean", name = "IS_DEBUGGING_LOGGING", value = "false")
                            buildConfigField(type = "Boolean", name = "BENCHMARK", value = "false")
                        }
                        debug {
                            buildConfigField(type = "Boolean", name = "IS_DEBUGGING_LOGGING", value = "true")
                            buildConfigField(type = "Boolean", name = "BENCHMARK", value = "false")
                        }
                        create("benchmark") {
                            buildConfigField(type = "Boolean", name = "IS_DEBUGGING_LOGGING", value = "true")
                            buildConfigField(type = "Boolean", name = "BENCHMARK", value = "true")
                        }
                    }
                }

                testOptions {
                    unitTests {
                        isIncludeAndroidResources = true
                    }
                }

                configureFlavors(commonExtension = this)
                configureKotlinAndroid(commonExtensions = this)
            }

            dependencies {
                "testImplementation"(project(":core:testing"))
                "androidTestImplementation"(project(":core:testing"))
                "implementation"(dependency = libs.findLibrary("androidx.core.ktx").get())
                "implementation"(dependency = libs.findLibrary("androidx.appcompat").get())
                "implementation"(dependency = libs.findLibrary("material").get())
                "testImplementation"(dependency = libs.findLibrary("junit").get())
                "androidTestImplementation"(dependency = libs.findLibrary("androidx.junit").get())
                "androidTestImplementation"(dependency = libs.findLibrary("androidx.espresso.core").get())
                "androidTestImplementation"(kotlin("test"))
                "testImplementation"(kotlin("test"))
                "testImplementation"(dependency = libs.findLibrary("robolectric").get())
            }
        }
    }
}