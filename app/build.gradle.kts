import com.bowoon.convention.Config
import com.bowoon.convention.Config.getProp

plugins {
    id("bowoon.application")
    id("bowoon.application.compose")
    id("bowoon.hilt")
    id("bowoon.android.application.firebase")
}

android {
    /*signingConfigs {
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
    }*/
    buildTypes {
        debug {
//                        applicationIdSuffix = MovieAppBuildType.DEBUG.applicationIdSuffix
            isMinifyEnabled = false
            buildConfigField("Boolean", "IS_DEBUGGING_LOGGING", "true")
            //signingConfig = signingConfigs.getByName(Config.Application.Movie.Sign.Debug.name)
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
            //signingConfig = signingConfigs.getByName(Config.Application.Movie.Sign.Release.name)
        }
    }
}

dependencies {
    arrayOf(
        projects.core.common,
        projects.core.data,
        projects.core.ui,
        projects.core.sync,
        projects.feature.home,
        projects.feature.detail,
        projects.feature.search,
        projects.feature.favorite,
        projects.feature.my,
        projects.feature.people,
        libs.coil.compose,
        libs.androidx.lifecycle.runtime.ktx,
        libs.androidx.navigation.compose,
        libs.androidx.compose.material3.navigationSuite,
        libs.threetenabp,
        libs.androidx.work.ktx,
        libs.androidx.splash,
        libs.androidx.startup,
        libs.hilt.ext.work
    ).forEach {
        implementation(it)
    }

    ksp(libs.hilt.compiler)
}
