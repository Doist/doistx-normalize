import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem

// Generate stubs before compiling for Linux.
tasks.findByName("compileKotlinLinuxX64")?.mustRunAfter("cinteropInteropLinuxX64")

// Disable cross-compilation/publication of the Linux target.
val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
tasks.matching { it.name.contains("linux", true) }.configureEach { onlyIf { os.isLinux } }
