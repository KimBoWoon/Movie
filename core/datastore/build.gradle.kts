plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.bowoon.surfy.core.datastore"
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
        projects.core.common,
        libs.kotlinx.serialization.json,
        libs.androidx.junit,
    ).forEach {
        implementation(it)
    }

    arrayOf(
        libs.protobuf.kotlin.lite,
        libs.androidx.datastore,
        projects.core.model
    ).forEach {
        api(it)
    }

    arrayOf(
        projects.core.datastoreTest
    ).forEach {
        testImplementation(it)
    }
}