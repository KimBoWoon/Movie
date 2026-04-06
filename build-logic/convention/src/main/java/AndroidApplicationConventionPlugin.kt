import com.android.build.api.dsl.ApplicationExtension
import com.cheeke.surfy.convention.AppBuildType
import com.cheeke.surfy.convention.Config
import com.cheeke.surfy.convention.Config.getProp
import com.cheeke.surfy.convention.configureKotlinAndroid
import com.cheeke.surfy.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "com.android.application")

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    compileSdk = Config.Application.Surfy.compileSdkVersion
                    minSdk = Config.Application.Surfy.minSdkVersion
                    applicationId = Config.Application.Surfy.applicationId
                    targetSdk = Config.Application.Surfy.targetSdkVersion
                    versionName = Config.Application.Surfy.versionName
                    versionCode = Config.Application.Surfy.versionCode
                    testInstrumentationRunner = Config.ApplicationSetting.SURFY_TEST_INSTRUMENTATION_RUNNER

                    signingConfigs {
                        register(Config.Application.Surfy.Sign.Release.NAME) {
                            storeFile = file(getProp(propertyKey = Config.Application.Surfy.Sign.Release.STORE_FILE))
                            storePassword = getProp(propertyKey = Config.Application.Surfy.Sign.Release.STORE_PASSWORD)
                            keyAlias = getProp(propertyKey = Config.Application.Surfy.Sign.Release.KEY_ALIAS)
                            keyPassword = getProp(propertyKey = Config.Application.Surfy.Sign.Release.KEY_PASSWORD)
                        }
                        register(Config.Application.Surfy.Sign.Debug.NAME) {
                            storeFile = file(getProp(propertyKey = Config.Application.Surfy.Sign.Debug.STORE_FILE))
                            storePassword = getProp(propertyKey = Config.Application.Surfy.Sign.Debug.STORE_PASSWORD)
                            keyAlias = getProp(propertyKey = Config.Application.Surfy.Sign.Debug.KEY_ALIAS)
                            keyPassword = getProp(propertyKey = Config.Application.Surfy.Sign.Debug.KEY_PASSWORD)
                        }
                    }

//                    setProperty(
//                        "archivesBaseName",
//                        "${Config.Application.Movie.appName}-v${versionName}-${SimpleDateFormat(Config.ApplicationSetting.dateFormat, Locale.KOREAN).format(System.currentTimeMillis())}"
//                    )
                }

                namespace = Config.Application.Surfy.applicationId

                val gitHash = project.providers.exec {
                    commandLine("git", "rev-parse", "--short", "HEAD")
                }.standardOutput.asText.map { it.trim() }.get()

                buildTypes {
                    debug {
                        applicationIdSuffix = AppBuildType.DEBUG.applicationIdSuffix
                        isMinifyEnabled = false
                        isDebuggable = true
                        isJniDebuggable = true
                        buildConfigField(type = "String", name = "GIT_HASH", value = "\"$gitHash\"")
                        manifestPlaceholders["appName"] = "${Config.Application.Surfy.appName}-debug"
                        signingConfig = signingConfigs.getByName(Config.Application.Surfy.Sign.Debug.NAME)
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
                        buildConfigField(type = "String", name = "GIT_HASH", value = "\"$gitHash\"")
                        manifestPlaceholders["appName"] = Config.Application.Surfy.appName
                        signingConfig = signingConfigs.getByName(Config.Application.Surfy.Sign.Release.NAME)
                    }
                    create("benchmark") {
                        initWith(buildTypes.getByName("release"))
                        signingConfig = signingConfigs.getByName("debug")
                        matchingFallbacks += listOf("release")
                        isDebuggable = false
                        manifestPlaceholders["appName"] = "${Config.Application.Surfy.appName}-Benchmark"
                    }
                }

                configureKotlinAndroid(commonExtensions = this)
            }

            dependencies {
                "testImplementation"(project(":core:testing"))
                "implementation"(dependency = libs.findLibrary("androidx.compose.material3.icons").get())
                "implementation"(dependency = libs.findLibrary("androidx.core.ktx").get())
                "implementation"(dependency = libs.findLibrary("androidx.appcompat").get())
                "testImplementation"(dependency = libs.findLibrary("junit").get())
                "androidTestImplementation"(dependency = libs.findLibrary("androidx.junit").get())
                "androidTestImplementation"(dependency = libs.findLibrary("androidx.espresso.core").get())
            }
        }
    }
}