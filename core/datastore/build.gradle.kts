plugins {
    alias(libs.plugins.bowoon.android.library)
    alias(libs.plugins.bowoon.hilt)
    id("com.google.protobuf") version "0.9.6"
}

android {
    namespace = "com.bowoon.movie.core.datastore"
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
        java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
        kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
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

    api(libs.protobuf.kotlin.lite)
    api(libs.androidx.datastore)
    api(projects.core.model)
    testImplementation(projects.core.datastoreTest)
}