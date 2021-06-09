/*
 * Generate stubs before compiling.
 */

tasks.configureEach {
    val match = "cinterop.+([A-Z][a-z]+[A-Z][a-z]*\\d\\d.*)".toRegex().matchEntire(name)
    if (match != null) {
        val target = match.groupValues[1]
        tasks.findByName("compileKotlin$target")?.mustRunAfter(tasks.getByName(match.value))
    }
}

