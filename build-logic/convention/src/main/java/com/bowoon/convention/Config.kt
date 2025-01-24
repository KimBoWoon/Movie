package com.bowoon.convention

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
        const val COMPILE_SDK_VERSION = 35
        const val MIN_SDK_VERSION = 23
        const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        const val defaultProguardFile = "proguard-android-optimize.txt"
        const val proguardFile = "proguard-rules.pro"
        const val dateFormat = "HHmmss"
    }

    object Library {
        const val MIN_SDK_VERSION = 23
        const val COMPILE_SDK_VERSION = 35
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
        object Movie : Application(
            appName = "movie",
            applicationId = "com.bowoon.movie",
            compileSdkVersion = ApplicationSetting.COMPILE_SDK_VERSION,
            minSdkVersion = ApplicationSetting.MIN_SDK_VERSION,
            targetSdkVersion = ApplicationSetting.COMPILE_SDK_VERSION,
            versionCode = 1,
            versionName = "1.0.0"
        ) {
            object Sign {
                object Release {
                    const val name = "Release"
                    const val storeFile = "store_file_path"
                    const val storePassword = "store_password"
                    const val keyAlias = "key_alias"
                    const val keyPassword = "key_password"
                }

                object Debug {
                    const val name = "Debug"
                    const val storeFile = "store_file_path"
                    const val storePassword = "store_password"
                    const val keyAlias = "key_alias"
                    const val keyPassword = "key_password"
                }
            }
        }
    }
}
