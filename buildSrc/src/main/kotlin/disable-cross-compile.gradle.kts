import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem

// Disable cross-compilation/publication of the Linux target.
val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
tasks.matching { it.name.contains("linux", true) }.configureEach { onlyIf { os.isLinux } }
