package com.bowoon.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.configureKotlinAndroid(
    commonExtensions: CommonExtension<*, *, *, *, *, *>
) {
    commonExtensions.apply {
        compileSdk = Config.ApplicationSetting.COMPILE_SDK_VERSION

        defaultConfig {
            minSdk = Config.ApplicationSetting.MIN_SDK_VERSION
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
        }

        buildFeatures {
            buildConfig = true
            viewBinding = true
            dataBinding.enable = true
        }
    }
}