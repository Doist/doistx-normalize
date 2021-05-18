import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import java.lang.IllegalStateException

val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()

// Split compilation and test in CI.
// Apple-specific targets on macOS, Windows-specific targets on Windows, everything else on Linux.
val applePlatforms = arrayOf("ios", "watchos", "tvos", "macos")
val windowsPlatforms = arrayOf("mingw")
val nonLinuxPlatforms = applePlatforms + windowsPlatforms

fun compileRegex(platforms: Array<String>, prefix: String = ""): Regex {
    val group = platforms.joinToString("|", prefix = prefix, transform = String::capitalize)
    return Regex("^compileKotlin($group).*$")
}
tasks.register("ciCompile") {
    group = "build"
    dependsOn(tasks.matching {
        when {
            os.isLinux -> it.name.matches(compileRegex(nonLinuxPlatforms, prefix = "?!"))
            os.isMacOsX -> it.name.matches(compileRegex(applePlatforms, ""))
            os.isWindows -> it.name.matches(compileRegex(windowsPlatforms, ""))
            else -> throw IllegalStateException("Unsupported CI host")
        }
    }.map { it.name })
}

fun testRegex(platforms: Array<String>, prefix: String = ""): Regex {
    val group = platforms.joinToString("|", prefix = prefix)
    return Regex("^($group).*Test$")
}
tasks.register("ciTests") {
    group = "verification"
    dependsOn(tasks.matching {
        when {
            os.isLinux -> it.name.matches(testRegex(nonLinuxPlatforms, prefix = "?!"))
            os.isMacOsX -> it.name.matches(testRegex(applePlatforms))
            os.isWindows -> it.name.matches(testRegex(windowsPlatforms))
            else -> throw IllegalStateException("Unsupported CI host")
        }
    }.map { it.name })
}
