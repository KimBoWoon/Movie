import com.bowoon.convention.configureFlavors

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.bowoon.android.test)
}

android {
    namespace = "com.bowoon.benchmark"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    // Use the same flavor dimensions as the application to allow generating Baseline Profiles on prod,
    // which is more close to what will be shipped to users (no fake data), but has ability to run the
    // benchmarks on demo, so we benchmark on stable data.
    configureFlavors(commonExtension = this) { flavor ->
        buildConfigField(
            type = "String",
            name = "APP_FLAVOR_SUFFIX",
            value = "\"${flavor.applicationIdSuffix ?: ""}\""
        )
    }

    buildTypes {
        // This benchmark buildType is used for benchmarking, and should function like your
        // release build (for example, with minification on). It"s signed with a debug key
        // for easy local/CI testing.
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
        }
    }

//    flavorDimensions += listOf("contentType")
//    productFlavors {
//        create("demo") { dimension = "contentType" }
//        create("prod") { dimension = "contentType" }
//    }

    targetProjectPath = ":surfy"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}