import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem

val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()

// Split compilation and test in CI.
// Apple-specific targets on macOS, Windows-specific targets on Windows, everything else on Linux.
tasks.register("ciCompile") {
    group = "build"
    when {
        os.isLinux -> dependsOn(
            tasks.matching { it.name.matches(Regex("^compileKotlin(?!Ios|Macos|Mingw).*$")) }.map { it.name })
        os.isMacOsX -> dependsOn(
            tasks.matching { it.name.matches(Regex("^compileKotlin(Ios|Macos).*$")) }.map { it.name })
        os.isWindows -> dependsOn(
            tasks.matching { it.name.matches(Regex("^compileKotlin(Mingw).*$")) }.map { it.name })
    }
}
tasks.register("ciTests") {
    group = "verification"
    when {
        os.isLinux -> dependsOn(
            tasks.matching { it.name.matches(Regex("^(?!ios|macos|mingw).*Test$")) }.map { it.name })
        os.isMacOsX -> dependsOn(
            tasks.matching { it.name.matches(Regex("^(ios|macos).*Test$")) }.map { it.name })
        os.isWindows -> dependsOn(
            tasks.matching { it.name.matches(Regex("^(mingw).*Test$")) }.map { it.name })
    }
}
