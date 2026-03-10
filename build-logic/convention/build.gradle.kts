import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.cheeke.surfy.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
    jvmToolchain {
        val javaVersion = JavaVersion.VERSION_17.toString()
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.android.plugin)
    compileOnly(libs.firebase.crashlytics.gradlePlugin)
    compileOnly(libs.firebase.performance.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = libs.plugins.cheeke.android.application.compose.get().pluginId
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = libs.plugins.cheeke.android.application.asProvider().get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = libs.plugins.cheeke.android.library.compose.get().pluginId
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.cheeke.android.library.asProvider().get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = libs.plugins.cheeke.hilt.get().pluginId
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFirebase") {
            id = libs.plugins.cheeke.android.application.firebase.get().pluginId
            implementationClass = "AndroidApplicationFirebaseConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.cheeke.android.feature.get().pluginId
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidFlavors") {
            id = libs.plugins.cheeke.android.application.flavors.get().pluginId
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.cheeke.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidTest") {
            id = libs.plugins.cheeke.android.test.get().pluginId
            implementationClass = "AndroidTestConventionPlugin"
        }
    }
}
