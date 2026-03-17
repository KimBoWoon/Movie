import com.android.build.api.dsl.ApplicationExtension
import com.cheeke.surfy.convention.configureFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationFlavorsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(receiver = target) {
            extensions.configure<ApplicationExtension> {
                configureFlavors(commonExtension = this)
            }
        }
    }
}