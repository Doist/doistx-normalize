 /*
 * Set up build and verification targets, depending on what's enabled by the "targets" property.
 */

import org.gradle.api.GradleException
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem

val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
val host = when {
    os.isMacOsX -> "macos"
    os.isWindows -> "mingw"
    os.isLinux -> "linux"
    else -> ""
}
val commonTargets = setOf("jvm", "js", "android", "wasm32")
val nativeTargets = when {
    os.isMacOsX -> setOf("macos", "ios", "watchos", "tvos")
    os.isLinux -> setOf("mingw")
    os.isWindows -> setOf("linux")
    else -> emptySet()
}
val crossTargets = setOf("mingw", "linux").subtract(listOf(host))

// Disable unwanted targets.
val targets = property("targets") as String
tasks.configureEach {
    onlyIf { task ->
        val targetsToDisable = when (targets) {
            // All possible targets are enabled, so disable nothing.
            "all" -> emptySet()
            // Native targets only, so disable common and cross-compiled targets.
            "native" -> commonTargets union crossTargets
            // Common targets only, so disable native and cross-compiled targets.
            "common" -> nativeTargets union crossTargets
            // Host OS only, so disable everything except the host.
            "host" -> commonTargets union nativeTargets union crossTargets
            // Unknown targets, fail early.
            else -> throw GradleException(
                "Property 'targets' must be 'all', 'native', 'common', or 'host', found '$targets'"
            )
        }

        targetsToDisable.none { target ->
            task.name.startsWith(target) || task.name.contains(target.capitalize())
        }
    }
}


