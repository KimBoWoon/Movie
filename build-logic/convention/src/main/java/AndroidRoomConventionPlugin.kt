import androidx.room.gradle.RoomExtension
import com.cheeke.convention.libs
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "androidx.room")
            apply(plugin = "com.google.devtools.ksp")

            extensions.configure<KspExtension> {
                arg(k = "room.generateKotlin", v = "true")
            }

            extensions.configure<RoomExtension> {
                schemaDirectory(path = "$projectDir/schemas")
            }

            dependencies {
                "implementation"(dependency = libs.findLibrary("androidx.room.runtime").get())
                "implementation"(dependency = libs.findLibrary("androidx.room.ktx").get())
                "ksp"(dependency = libs.findLibrary("androidx.room.compiler").get())
            }
        }
    }
}
