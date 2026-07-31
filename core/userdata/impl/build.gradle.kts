plugins {
    alias(libs.plugins.cheeke.android.library)
    alias(libs.plugins.cheeke.hilt)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.cheeke.surfy.core.userdata.impl"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    // Generates the java Protobuf-lite code for the Protobufs in this project. See
    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
    // for more information.
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

androidComponents.beforeVariants {
    android.sourceSets.register(it.name) {
        val buildDir = layout.buildDirectory.get().asFile
        java.srcDir(srcDir = buildDir.resolve(relative = "generated/source/proto/${it.name}/java"))
        kotlin.srcDir(srcDir = buildDir.resolve(relative = "generated/source/proto/${it.name}/kotlin"))
    }
}

dependencies {
    arrayOf(
        project(":core:database:impl"),
        project(":core:network:api"),
        project(":core:userdata:api"),
        libs.androidx.compose.paging,
        libs.hilt.android.testing,
        libs.protobuf.kotlin.lite,
        libs.androidx.datastore
    ).forEach {
        implementation(it)
    }

    arrayOf(
        project(":core:common")
    ).forEach {
        api(it)
    }

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(testFixtures(project(":core:userdata:api")))
}