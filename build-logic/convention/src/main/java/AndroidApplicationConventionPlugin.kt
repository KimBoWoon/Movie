import com.android.build.api.dsl.ApplicationExtension
import com.bowoon.convention.Config
import com.bowoon.convention.Config.getProp
import com.bowoon.convention.configureFlavors
import com.bowoon.convention.configureKotlinAndroid
import com.bowoon.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import java.text.SimpleDateFormat
import java.util.Locale

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("com.google.devtools.ksp")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("org.jetbrains.kotlin.plugin.parcelize")
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    compileSdk = Config.Application.Movie.compileSdkVersion
                    minSdk = Config.Application.Movie.minSdkVersion
                    extensions.configure<ApplicationExtension> {
                        namespace = Config.Application.Movie.applicationId
                        applicationId = Config.Application.Movie.applicationId
                        targetSdk = Config.Application.Movie.targetSdkVersion
                        versionName = Config.Application.Movie.versionName
                        versionCode = Config.Application.Movie.versionCode
                        testInstrumentationRunner = Config.ApplicationSetting.testInstrumentationRunner

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

                    SimpleDateFormat(Config.ApplicationSetting.dateFormat, Locale.getDefault()).run {
                        setProperty("archivesBaseName", "${Config.Application.Movie.appName}-v${versionName}-${format(System.currentTimeMillis())}")
                    }
                }

                buildTypes {
                    debug {
//                        applicationIdSuffix = MovieAppBuildType.DEBUG.applicationIdSuffix
                        isMinifyEnabled = false
                        buildConfigField("Boolean", "IS_DEBUGGING_LOGGING", "true")
                        signingConfig = signingConfigs.getByName(Config.Application.Movie.Sign.Debug.name)
                    }
                    release {
//                        applicationIdSuffix = MovieAppBuildType.RELEASE.applicationIdSuffix
                        isMinifyEnabled = true
                        isShrinkResources = true
                        isDebuggable = false
                        proguardFiles(
                            getDefaultProguardFile(Config.ApplicationSetting.defaultProguardFile),
                            Config.ApplicationSetting.proguardFile
                        )
                        buildConfigField("Boolean", "IS_DEBUGGING_LOGGING", "false")
                        signingConfig = signingConfigs.getByName(Config.Application.Movie.Sign.Release.name)
                    }
                }

                configureFlavors(this)
                configureKotlinAndroid(this)
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx.core.ktx").get())
                add("implementation", libs.findLibrary("androidx.appcompat").get())
                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx.junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx.espresso.core").get())
            }
        }
    }
}