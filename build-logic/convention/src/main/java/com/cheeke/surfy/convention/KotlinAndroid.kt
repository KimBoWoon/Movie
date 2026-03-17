package com.cheeke.surfy.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.configureKotlinAndroid(
    commonExtensions: CommonExtension
) {
    commonExtensions.apply {
        compileSdk = Config.ApplicationSetting.COMPILE_SDK_VERSION

        defaultConfig.apply {
            minSdk = Config.ApplicationSetting.MIN_SDK_VERSION
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        lint.apply {
            abortOnError = false
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
        }

        buildFeatures.apply {
            buildConfig = true
        }
    }
}