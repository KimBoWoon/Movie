import com.android.build.api.dsl.ApplicationExtension
import com.bowoon.convention.AppBuildType
import com.bowoon.convention.Config
import com.bowoon.convention.Config.getProp
import com.bowoon.convention.configureKotlinAndroid
import com.bowoon.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import java.io.ByteArrayOutputStream

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    compileSdk = Config.Application.Movie.compileSdkVersion
                    minSdk = Config.Application.Movie.minSdkVersion
                    extensions.configure<ApplicationExtension> {
                        applicationId = Config.Application.Movie.applicationId
                        targetSdk = Config.Application.Movie.targetSdkVersion
                        versionName = Config.Application.Movie.versionName
                        versionCode = Config.Application.Movie.versionCode
                        testInstrumentationRunner = "com.bowoon.testing.MovieTestRunner"

                        signingConfigs {
                            register(Config.Application.Movie.Sign.Release.name) {
                                storeFile = file(getProp(Config.Application.Movie.Sign.Release.storeFile))
                                storePassword = getProp(Config.Application.Movie.Sign.Release.storePassword)
                                keyAlias = getProp(Config.Application.Movie.Sign.Release.keyAlias)
                                keyPassword = getProp(Config.Application.Movie.Sign.Release.keyPassword)
                            }
                            register(Config.Application.Movie.Sign.Debug.name) {
                                storeFile = file(getProp(Config.Application.Movie.Sign.Debug.storeFile))
                                storePassword = getProp(Config.Application.Movie.Sign.Debug.storePassword)
                                keyAlias = getProp(Config.Application.Movie.Sign.Debug.keyAlias)
                                keyPassword = getProp(Config.Application.Movie.Sign.Debug.keyPassword)
                            }
                        }
                    }

//                    setProperty(
//                        "archivesBaseName",
//                        "${Config.Application.Movie.appName}-v${versionName}-${SimpleDateFormat(Config.ApplicationSetting.dateFormat, Locale.KOREAN).format(System.currentTimeMillis())}"
//                    )
                }

                namespace = Config.Application.Movie.applicationId

                val gitHash = ByteArrayOutputStream().use {
//                    DefaultProviderFactory().exec {
//                        commandLine("git", "rev-parse", "--short", "HEAD")
//                        standardOutput = it
//                    }
                    exec {
                        commandLine("git", "rev-parse", "--short", "HEAD")
                        standardOutput = it
                    }
                    it.toString().trim()
                }

                buildTypes {
                    debug {
                        applicationIdSuffix = AppBuildType.DEBUG.applicationIdSuffix
                        isMinifyEnabled = false
                        isDebuggable = true
                        isJniDebuggable = true
                        buildConfigField("Boolean", "IS_DEBUGGING_LOGGING", "true")
                        buildConfigField("String", "GIT_HASH", "\"$gitHash\"")
                        manifestPlaceholders["appName"] = "Movie-QA"
                        signingConfig = signingConfigs.getByName(Config.Application.Movie.Sign.Debug.name)
                    }
                    release {
                        applicationIdSuffix = AppBuildType.RELEASE.applicationIdSuffix
                        isMinifyEnabled = true
                        isShrinkResources = true
                        isDebuggable = false
                        isJniDebuggable = false
                        proguardFiles(
                            getDefaultProguardFile(Config.ApplicationSetting.DEFAULT_PROGUARD_FILE),
                            Config.ApplicationSetting.PROGUARD_FILE
                        )
                        buildConfigField("Boolean", "IS_DEBUGGING_LOGGING", "false")
                        buildConfigField("String", "GIT_HASH", "\"$gitHash\"")
                        manifestPlaceholders["appName"] = "Movie"
                        signingConfig = signingConfigs.getByName(Config.Application.Movie.Sign.Release.name)
                    }
                }

                configureKotlinAndroid(this)
            }

            dependencies {
                add("testImplementation", project(":core:testing"))
                add("implementation", libs.findLibrary("androidx.compose.material3.icons").get())
                add("implementation", libs.findLibrary("androidx.core.ktx").get())
                add("implementation", libs.findLibrary("androidx.appcompat").get())
                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx.junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx.espresso.core").get())
            }
        }
    }
}