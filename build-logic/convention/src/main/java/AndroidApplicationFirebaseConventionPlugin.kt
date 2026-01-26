import com.android.build.api.dsl.ApplicationExtension
import com.bowoon.convention.libs
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(receiver = target) {
            apply(plugin = "com.google.gms.google-services")
//            apply(plugin = "com.google.firebase.firebase-perf")
            apply(plugin = "com.google.firebase.crashlytics")

            dependencies {
                val bom = libs.findLibrary("firebase-bom").get()
                "implementation"(dependency = platform(bom))
                "implementation"(dependency = libs.findLibrary("firebase.analytics").get())
                "implementation"(dependency = libs.findLibrary("firebase.performance").get()) {
                    /*
                    Exclusion of protobuf / protolite dependencies is necessary as the
                    datastore-proto brings in protobuf dependencies. These are the source of truth
                    for Now in Android.
                    That's why the duplicate classes from below dependencies are excluded.
                    */
                    exclude(group = "com.google.protobuf", module = "protobuf-javalite")
                    exclude(group = "com.google.firebase", module = "protolite-well-known-types")
                }
                "implementation"(dependency = libs.findLibrary("firebase.crashlytics").get())
                "implementation"(dependency = libs.findLibrary("firebase.message").get())
            }

            extensions.configure<ApplicationExtension> {
                buildTypes.configureEach {
                    configure<CrashlyticsExtension> {
                        mappingFileUploadEnabled = true
                    }
                }
            }
        }
    }
}
