package com.cheeke.surfy.convention

import java.io.File
import java.io.FileInputStream
import java.util.Properties

object Config {
    private val prop: Properties = Properties().apply {
        load(FileInputStream(File("./sign", "local.properties")))
    }

    fun getProp(propertyKey: String): String =
        runCatching {
            prop.getProperty(propertyKey)
        }.getOrDefault("\"key not found\"")

    object ApplicationSetting {
        const val COMPILE_SDK_VERSION = 36
        const val MIN_SDK_VERSION = 26
        const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
        const val SURFY_TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
        const val DEFAULT_PROGUARD_FILE = "proguard-android-optimize.txt"
        const val PROGUARD_FILE = "proguard-rules.pro"
        const val DATE_FORMAT = "HHmmss"
    }

    object Library {
        const val MIN_SDK_VERSION = 26
        const val COMPILE_SDK_VERSION = 36
    }

    sealed class Application(
        val appName: String,
        val applicationId: String,
        val compileSdkVersion: Int = ApplicationSetting.COMPILE_SDK_VERSION,
        val minSdkVersion: Int = ApplicationSetting.MIN_SDK_VERSION,
        val targetSdkVersion: Int = ApplicationSetting.COMPILE_SDK_VERSION,
        val versionCode: Int,
        val versionName: String,
    ) {
        object Surfy : Application(
            appName = "surfy",
            applicationId = "com.cheeke.surfy",
            compileSdkVersion = ApplicationSetting.COMPILE_SDK_VERSION,
            minSdkVersion = ApplicationSetting.MIN_SDK_VERSION,
            targetSdkVersion = ApplicationSetting.COMPILE_SDK_VERSION,
            versionCode = 1,
            versionName = "1.0.0"
        ) {
            object Sign {
                object Release {
                    const val NAME = "Release"
                    const val STORE_FILE = "store_file_path"
                    const val STORE_PASSWORD = "store_password"
                    const val KEY_ALIAS = "key_alias"
                    const val KEY_PASSWORD = "key_password"
                }

                object Debug {
                    const val NAME = "Debug"
                    const val STORE_FILE = "store_file_path"
                    const val STORE_PASSWORD = "store_password"
                    const val KEY_ALIAS = "key_alias"
                    const val KEY_PASSWORD = "key_password"
                }
            }
        }
    }
}
